package com.example.apptattoo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.example.apptattoo.R;
import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.helper.UsuarioFirebase;
import com.example.apptattoo.model.AvaliacaoTatuador;
import com.example.apptattoo.model.Evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class VisualizaTatuagemFinalizadaActivity extends AppCompatActivity {

    private ImageView imgTattooFinalizada;
    private EditText txtNomeTatuador, txtValor;
    private Button btnAvaliarTatuagem;

    private Evento tatuagemRecebida;

    private float nota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualiza_tatuagem_finalizada);

        imgTattooFinalizada = findViewById(R.id.imgFotoTatuagemFinalizada);
        txtNomeTatuador = findViewById(R.id.txtNomeTatuadorFinalizado);
        txtValor = findViewById(R.id.txtValorFinalizado);
        btnAvaliarTatuagem = findViewById(R.id.btnAvaliarTatuagem);
        btnAvaliarTatuagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialog();
            }
        });

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tatuagemRecebida = (Evento) bundle.getSerializable("tatuagemSelecionada");
        }
        preenchendoDados();
        verificaAvaliacao();
    }

    private void abrirDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VisualizaTatuagemFinalizadaActivity.this, R.style.AppTheme_Dialog);
        View view = getLayoutInflater().inflate(R.layout.dialog_avaliacao_tatuagem, null);
        RatingBar ratingBarAvaliacao = view.findViewById(R.id.ratingBarTatuagemFinalizada);
        ratingBarAvaliacao.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                nota = v;
            }
        });
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                btnAvaliarTatuagem.setText("Tatuagem avaliada");
                btnAvaliarTatuagem.setEnabled(false);
                //PEGA A NOTA ARMAZENADA E ENVIA PRO BANCO
                enviaAvaliacao();
                verificaAvaliacao();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setView(view);
        builder.show();
    }

    private void verificaAvaliacao() {
        DatabaseReference avalaliacaoTatuadorRef = ConfiguracaoFirebase.getFirebase().child("avaliacaoTatuador")
                .child(tatuagemRecebida.getIdTatuador())
                .child(UsuarioFirebase.getIdentificadorUsuario());

        //Recuperar dados da avaliacao do tatuador
        avalaliacaoTatuadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Verifica se ja a tatuagem já foi Avaliada
                if (dataSnapshot.hasChild(tatuagemRecebida.getIdConsulta())) {
                    btnAvaliarTatuagem.setText("Tatuagem avaliada");
                    btnAvaliarTatuagem.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void enviaAvaliacao() {
        DatabaseReference avalaliacaoTatuadorRef = ConfiguracaoFirebase.getFirebase().child("avaliacaoTatuador")
                .child(tatuagemRecebida.getIdTatuador());

        //Recuperar dados da avaliacao do tatuador
        avalaliacaoTatuadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int qtdAvaliacoes = 0;
                float notaTotal = 0;
                if (dataSnapshot.hasChild("qtd_avaliacao")) {
                    int avaliacoes = dataSnapshot.child("qtd_avaliacao").getValue(Integer.class);
                    qtdAvaliacoes = avaliacoes;
                }
                if (dataSnapshot.hasChild("nota")) {
                    float notas = dataSnapshot.child("nota").getValue(Float.class);
                    notaTotal = notas;
                }

                //Verifica se ja a tatuagem já foi Avaliada
                if (dataSnapshot.hasChild(tatuagemRecebida.getIdConsulta())) {
                    btnAvaliarTatuagem.setText("Tatuagem avaliada");
                    btnAvaliarTatuagem.setEnabled(false);
                }

                //Monta objeto para curtida

                final AvaliacaoTatuador avaliacaoTatuador = new AvaliacaoTatuador();
                avaliacaoTatuador.setQtdAvaliacao(qtdAvaliacoes);
                avaliacaoTatuador.setNotaAvaliacao(notaTotal);

                avaliacaoTatuador.salvarAvaliacao(tatuagemRecebida.getIdTatuador(), tatuagemRecebida.getIdConsulta(), nota);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void preenchendoDados() {

        Uri uriFotoUsuario = Uri.parse(tatuagemRecebida.getFotoTatuagem());
        Glide.with(getApplicationContext()).load(uriFotoUsuario).into(imgTattooFinalizada);

        txtNomeTatuador.setText("@" + tatuagemRecebida.getNomeTatuador());
        txtValor.setText(tatuagemRecebida.getValor());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}