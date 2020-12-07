package com.example.apptattoo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apptattoo.R;
import com.example.apptattoo.helper.GerenciarDialogCarregamento;
import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.helper.UsuarioFirebase;
import com.example.apptattoo.model.ConsultaTatuagem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class ObservacoesTatuagemActivity extends AppCompatActivity {

    private String recebeParteCorpo, recebeAltura, recebeLargura, recebeCor, idUsuarioLogado, recebeIdTatuador;
    private byte[] recebeTattoo;

    private Button btnFinalizarObservacoes;
    private EditText txtObservacoesTattoo;

    private ViewGroup content;

    private Calendar diaHoje;

    private DataSnapshot tatuadoresSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observacoes_tatuagem);

        diaHoje = Calendar.getInstance();

        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //Pegando dados da activity anterior
        Intent intent = getIntent();
        recebeIdTatuador = intent.getStringExtra("idTatuador");
        recebeParteCorpo = intent.getStringExtra("parteCorpo");
        recebeAltura = intent.getStringExtra("alturaTattoo");
        recebeLargura = intent.getStringExtra("larguraTattoo");
        recebeCor = intent.getStringExtra("colorida");
        recebeTattoo = intent.getByteArrayExtra("imgTattoo");

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        btnFinalizarObservacoes = findViewById(R.id.btnFinalizarObservacoes);
        txtObservacoesTattoo = findViewById(R.id.txtObservacoesTattoo);
        content = findViewById(R.id.viewGroupObs);

        //Usado para adicionar idTatuadore em consultaAberta
        recuperarTodosTatuadores();

        btnFinalizarObservacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarConsulta();
            }
        });
    }

    private void enviarConsulta(){
        GerenciarDialogCarregamento.mostrarDialog(ObservacoesTatuagemActivity.this,"Enviando consulta");
        final ConsultaTatuagem consulta = new ConsultaTatuagem();
        consulta.setIdCliente(idUsuarioLogado);
        consulta.setObservacoes(txtObservacoesTattoo.getText().toString());
        consulta.setCor(recebeCor);
        consulta.setLocalCorpo(recebeParteCorpo);
        consulta.setAltura(Integer.parseInt(recebeAltura));
        consulta.setLargura(Integer.parseInt(recebeLargura));

        //Salvar no firebase storage
        StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        final StorageReference imagemRef = storageReference
                .child("imagens")
                .child("consulta")
                .child(consulta.getIdConsulta()+".jpeg");

        UploadTask uploadTask = imagemRef.putBytes(recebeTattoo);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Erro ao enviar consulta, tente novamente", Toast.LENGTH_SHORT).show();
                GerenciarDialogCarregamento.fecharDialog();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri url) {
                        consulta.setFotoConsulta(url.toString());

                        //salvando consulta fechada
                        if (recebeIdTatuador!=null){
                            if (consulta.salvarConsultaFechada(recebeIdTatuador)){
                                GerenciarDialogCarregamento.fecharDialog();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//Limpando pilha de activities
                                startActivity(i);
                                finish();
                            }
                        }

                        //salvando consulta aberta
                        else if (consulta.salvarConsultaAberta(tatuadoresSnapshot)){
                            GerenciarDialogCarregamento.fecharDialog();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//Limpando pilha de activities
                            startActivity(i);
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void recuperarTodosTatuadores(){

                //recuperar tatuadores
                DatabaseReference seguidoresRef = ConfiguracaoFirebase.getFirebase()
                        .child("tatuadores");

                seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tatuadoresSnapshot = dataSnapshot;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}