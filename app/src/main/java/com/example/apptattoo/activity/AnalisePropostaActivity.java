package com.example.apptattoo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.apptattoo.R;
import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.helper.UsuarioFirebase;
import com.example.apptattoo.model.Evento;
import com.example.apptattoo.model.Horario;
import com.example.apptattoo.model.Proposta;
import com.google.firebase.database.DatabaseReference;

public class AnalisePropostaActivity extends AppCompatActivity {

    private Button btnAceitarSessaoUnica, btnAceitarSessaoMultipla;
    private TextView lblTempoEstimadoProposta, lblValorSessaoUnicaProposta, lblValorPorSessaoProposta,
            lblTempoCadaSessaoProposta, lblQtdSessoesProposta, lblValorTotalSessaoMultiplaProposta;
    private ConstraintLayout layoutSessaoUnica, layoutSessaoMultipla;
    private ImageView imgFotoTattooProposta;
    private Evento evento;

    private Proposta propostaRecebida, propostaEnviada;

    private String tipoProposta, idCliente;

    private DatabaseReference orcamentoAbertoRef, orcamentoPrivadoRef, confirmarHorarioRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analise_proposta);

        //Instanciando evento
        evento = new Evento();

        idCliente = UsuarioFirebase.getIdentificadorUsuario();

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        //Inicializando componentes
        btnAceitarSessaoUnica = findViewById(R.id.btnAceitarSessaoUnica);
        btnAceitarSessaoMultipla = findViewById(R.id.btnAceitarSessaoMultipla);

        imgFotoTattooProposta = findViewById(R.id.imgFotoTattooProposta);

        lblTempoEstimadoProposta = findViewById(R.id.lblTempoEstimadoProposta);
        lblValorSessaoUnicaProposta = findViewById(R.id.lblValorSessaoUnicaProposta);
        lblValorPorSessaoProposta = findViewById(R.id.lblValorPorSessaoProposta);
        lblTempoCadaSessaoProposta = findViewById(R.id.lblTempoCadaSessaoProposta);
        lblQtdSessoesProposta = findViewById(R.id.lblQtdSessoesProposta);
        lblValorTotalSessaoMultiplaProposta = findViewById(R.id.lblValorTotalSessaoMultiplaProposta);

        layoutSessaoUnica = findViewById(R.id.layoutSessaoUnica);
        layoutSessaoMultipla = findViewById(R.id.layoutSessaoMultipla);

        //Recuperar dados do usuario passsados pelo recyclerClick de PropostaConsulta...Fragment
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            propostaRecebida = (Proposta) bundle.getSerializable("propostaSelecionada");
            tipoProposta = bundle.getString("tipoProposta");
        }

        confirmarHorarioRef = ConfiguracaoFirebase.getFirebase()
                .child("confirmarHorario")
                .child(idCliente)
                .child(propostaRecebida.getIdTatuador());

        //SETANDO FOTO DA TATTOO
        String fotoTattoo = propostaRecebida.getFotoTatuagem();
        if (fotoTattoo != null) {
            Uri url = Uri.parse(fotoTattoo);
            Glide.with(AnalisePropostaActivity.this)
                    .load(url)
                    .into(imgFotoTattooProposta);
        }

        if (propostaRecebida.getTipoOrcamento().equals("sessaoUnica")) {
            layoutSessaoMultipla.setVisibility(View.GONE);
            lblTempoEstimadoProposta.setText(propostaRecebida.getTempoSessaoUnica());
            lblValorSessaoUnicaProposta.setText(propostaRecebida.getValorTotalSessaoUnica());
        } else if (propostaRecebida.getTipoOrcamento().equals("sessaoMultipla")) {
            layoutSessaoUnica.setVisibility(View.GONE);
            lblValorPorSessaoProposta.setText(propostaRecebida.getValorPorSessao());
            lblTempoCadaSessaoProposta.setText(propostaRecebida.getTempoSessoesMultiplas());
            lblQtdSessoesProposta.setText(String.valueOf(propostaRecebida.getQtdSessoes()));
            lblValorTotalSessaoMultiplaProposta.setText(propostaRecebida.getValorTotalSessaoMultipla());
        } else {//Sessao completa
            lblTempoEstimadoProposta.setText(propostaRecebida.getTempoSessaoUnica());
            lblValorSessaoUnicaProposta.setText(propostaRecebida.getValorTotalSessaoUnica());

            lblValorPorSessaoProposta.setText(propostaRecebida.getValorPorSessao());
            lblTempoCadaSessaoProposta.setText(propostaRecebida.getTempoSessoesMultiplas());
            lblQtdSessoesProposta.setText(String.valueOf(propostaRecebida.getQtdSessoes()));
            lblValorTotalSessaoMultiplaProposta.setText(propostaRecebida.getValorTotalSessaoMultipla());
        }
        //Instanciando Obj proposta
        propostaEnviada = new Proposta(idCliente, propostaRecebida.getIdTatuador(), propostaRecebida.getIdConsulta());

        btnAceitarSessaoUnica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populandoObjeto();

                if (btnAceitarSessaoUnica.getText().equals("Finalizar")){
                    if (propostaEnviada.salvarProposta("sessaoUnica")) {
                        Toast.makeText(getApplicationContext(), "Tatuagem agendada com sucesso!", Toast.LENGTH_SHORT).show();
                        excluirConsulta();
                        finish();
                    }
                }
                else {
                    //Chamando Activity
                    Intent intent = new Intent(AnalisePropostaActivity.this, SelecionaHorario.class);
                    intent.putExtra("idTatuador", propostaRecebida.getIdTatuador());
                    intent.putExtra("idConsulta", propostaRecebida.getIdConsulta());
                    startActivityForResult(intent, 1);
                }
            }
        });

        btnAceitarSessaoMultipla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populandoObjeto();
                if (btnAceitarSessaoMultipla.getText().equals("Finalizar")){
                    if (propostaEnviada.salvarProposta("sessaoMultipla")) {
                        Toast.makeText(getApplicationContext(), "Tatuagem agendada com sucesso!", Toast.LENGTH_SHORT).show();
                        excluirConsulta();
                        finish();
                    }
                }
                else {
                    //Chamando Activity
                    Intent intent = new Intent(AnalisePropostaActivity.this, SelecionaHorario.class);
                    intent.putExtra("idTatuador", propostaRecebida.getIdTatuador());
                    intent.putExtra("idConsulta", propostaRecebida.getIdConsulta());
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    private void populandoEvento(Horario horarioSelecionado){
        evento.setAno(horarioSelecionado.getAno());
        evento.setMes(horarioSelecionado.getMes());
        evento.setDia(horarioSelecionado.getDia());
        evento.setHora(horarioSelecionado.getHora());
        evento.setMinuto(horarioSelecionado.getMinuto());
        evento.setDuracao(horarioSelecionado.getDuracao());
        evento.setDataInMillis(horarioSelecionado.getDataInMillis());
        evento.setNome(UsuarioFirebase.getNomeUsuario(idCliente));
        if (propostaRecebida.getTipoOrcamento().equals("sessaoUnica")){
            evento.setValor(propostaRecebida.getValorTotalSessaoUnica());
        }
        else{
            evento.setValor(propostaRecebida.getValorPorSessao());
        }
        evento.setCelular(UsuarioFirebase.telefoneUsuario);
        evento.setFotoUsuario(UsuarioFirebase.getDadosUsuarioLogado().getCaminhoFoto());
        evento.setFotoTatuagem(propostaRecebida.getFotoTatuagem());
        evento.setIdCliente(idCliente);
        evento.setHoraInicio(horarioSelecionado.getHoraInicio());
        evento.setHoraTermino(horarioSelecionado.getHoraTermino());
        evento.setHoraTermino(horarioSelecionado.getHoraTermino());
        evento.setIdConsulta(propostaRecebida.getIdConsulta());
    }


    public void populandoObjeto() {
        propostaEnviada.setNomeTatuador(propostaRecebida.getNomeTatuador());
        propostaEnviada.setFotoTatuador(propostaRecebida.getFotoTatuador());
        propostaEnviada.setFotoUsuario(UsuarioFirebase.getDadosUsuarioLogado().getCaminhoFoto());
        propostaEnviada.setValorTotalSessaoUnica(propostaRecebida.getValorTotalSessaoUnica());
        propostaEnviada.setTempoSessaoUnica(propostaRecebida.getTempoSessaoUnica());
        propostaEnviada.setTempoSessoesMultiplas(propostaRecebida.getTempoSessoesMultiplas());
        propostaEnviada.setValorTotalSessaoMultipla(propostaRecebida.getValorTotalSessaoMultipla());
        propostaEnviada.setValorPorSessao(propostaRecebida.getValorPorSessao());
        propostaEnviada.setTipoOrcamento(propostaRecebida.getTipoOrcamento());
        propostaEnviada.setFotoTatuagem(propostaRecebida.getFotoTatuagem());
        propostaEnviada.setIdTatuador(propostaRecebida.getIdTatuador());
        propostaEnviada.setQtdSessoes(propostaRecebida.getQtdSessoes());
        propostaEnviada.setIdConsulta(propostaRecebida.getIdConsulta());
        propostaEnviada.setHora(propostaRecebida.getHora());
        propostaEnviada.setData(propostaRecebida.getData());
    }

    private void excluirConsulta() {
        if (tipoProposta.equals("consultaAberta")) {

            DatabaseReference consultaAbertaRef = ConfiguracaoFirebase.getFirebase()
                    .child("consultaAberta")
                    .child(idCliente)
                    .child(propostaRecebida.getIdConsulta());
            consultaAbertaRef.removeValue();

            orcamentoAbertoRef = ConfiguracaoFirebase.getFirebase()
                    .child("orcamentosAbertos")
                    .child(idCliente)
                    .child(propostaRecebida.getIdTatuador())
                    .child(propostaRecebida.getIdConsulta());

            orcamentoAbertoRef.removeValue();
        } else {
            orcamentoPrivadoRef = ConfiguracaoFirebase.getFirebase()
                    .child("orcamentosPrivados")
                    .child(idCliente)
                    .child(propostaRecebida.getIdTatuador())
                    .child(propostaRecebida.getIdConsulta());
            orcamentoPrivadoRef.removeValue();
        }
    }

    // Recebe os dados da activity SelecionaHorario;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Verfica se o requestCode é o mesmo que foi passado
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Horario horarioSelecionado = (Horario) data.getSerializableExtra("horarioSelecionado");
            if (propostaRecebida.getTipoOrcamento().equals("sessaoUnica")){
                btnAceitarSessaoUnica.setText("Finalizar");
            }else {
                btnAceitarSessaoMultipla.setText("Finalizar");
            }

            populandoEvento(horarioSelecionado);
            evento.salvarEvento(propostaRecebida.getIdTatuador(),horarioSelecionado.getDataInMillis());
        }
    }

    // btnVoltar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}