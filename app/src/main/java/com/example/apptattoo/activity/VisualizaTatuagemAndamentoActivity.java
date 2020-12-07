package com.example.apptattoo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.apptattoo.R;
import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.model.LocalEstudio;
import com.example.apptattoo.model.Proposta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class VisualizaTatuagemAndamentoActivity extends AppCompatActivity {

    private ImageView imgTattooAndamento;
    private EditText txtLocalizacaoEstudio, txtNomeTatuador, txtNomeEstudio, txtValor;

    private Proposta tatuagemRecebida;
    private LocalEstudio localEstudio;

    private DatabaseReference localEstudioRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualiza_tatuagem_andamento);

        imgTattooAndamento = findViewById(R.id.imgFotoTatuagemAndamento);
        txtLocalizacaoEstudio = findViewById(R.id.txtLocalizacaoEstudioAndamento);
        txtNomeEstudio = findViewById(R.id.txtNomeEstudioAndamento);
        txtNomeTatuador = findViewById(R.id.txtNomeTatuadorEmAndamento);
        txtValor = findViewById(R.id.txtValorAndamento);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tatuagemRecebida = (Proposta) bundle.getSerializable("tatuagemSelecionada");
        }

        localEstudioRef = ConfiguracaoFirebase.getFirebase()
                .child("funcionariosEstudio")
                .child(tatuagemRecebida.getIdTatuador());

        recuperarLocalizacaoEstudio();

    }

    private void preenchendoDados(){
        Uri uriFotoUsuario = Uri.parse(tatuagemRecebida.getFotoTatuagem());
        Glide.with(getApplicationContext()).load(uriFotoUsuario).into(imgTattooAndamento);
        txtLocalizacaoEstudio.setText(localEstudio.getCep()+", "+localEstudio.getLocalidade()+", "+localEstudio.getBairro()+", "+localEstudio.getLogradouro()+", "+localEstudio.getNumeroCasa()+", "+localEstudio.getComplemento());
        txtNomeEstudio.setText(localEstudio.getNomeEstudio());
        txtNomeTatuador.setText(tatuagemRecebida.getNomeTatuador());
        if (tatuagemRecebida.getTipoOrcamento().equals("sessaoUnica"))
            txtValor.setText(tatuagemRecebida.getValorTotalSessaoUnica());
        else
            txtValor.setText(tatuagemRecebida.getValorPorSessao());
    }

    private void recuperarLocalizacaoEstudio() {
        localEstudioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    localEstudio = snap.getValue(LocalEstudio.class);
                    preenchendoDados();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}