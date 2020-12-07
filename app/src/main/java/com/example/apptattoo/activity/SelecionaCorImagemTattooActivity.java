package com.example.apptattoo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apptattoo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class SelecionaCorImagemTattooActivity extends AppCompatActivity {

    private String recebeParteCorpo, recebeAltura, recebeLargura, recebeIdTatuador, colorida;
    private boolean temImagem = false;
    private byte[] dadosImagem;

    private Switch switchCor;
    private ImageView imgAddImagemConsulta;
    private Button btnProximoCor;
    private TextView lblAddImagem;

    private static final int CODIGO_PERMISSAO = 123;//qlqr numero
    private static final int SELECAO_GALERIA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleciona_cor_imagem_tattoo);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        colorida = "não";

        //Pegando dados da activity anterior
        Intent intent = getIntent();
        recebeIdTatuador = intent.getStringExtra("idTatuador");
        recebeParteCorpo = intent.getStringExtra("parteCorpo");
        recebeAltura = intent.getStringExtra("alturaTattoo");
        recebeLargura = intent.getStringExtra("larguraTattoo");

        switchCor = findViewById(R.id.switchCor);
        lblAddImagem = findViewById(R.id.lblAddImagem);
        imgAddImagemConsulta = findViewById(R.id.imgAddImagemConsulta);
        btnProximoCor = findViewById(R.id.btnProximoCor);

        switchCor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (isChecked){
                   colorida = "Sim";
               }else {
                   colorida = "Nao";
               }
            }
        });

        btnProximoCor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temImagem){
                    Intent i = new Intent(getApplicationContext(), ObservacoesTatuagemActivity.class);
                    i.putExtra("idTatuador", recebeIdTatuador);
                    i.putExtra("parteCorpo", recebeParteCorpo);
                    i.putExtra("alturaTattoo", String.valueOf(recebeAltura));
                    i.putExtra("larguraTattoo", String.valueOf(recebeLargura));
                    i.putExtra("colorida", colorida);
                    i.putExtra("imgTattoo", dadosImagem);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getApplicationContext(),"É necessário adicionar uma imagem de referência para sua tatuagem",Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgAddImagemConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificandoPermissoes();
            }
        });

    }

    public void verificandoPermissoes(){

        String[] permissoes = {Manifest.permission.READ_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissoes[0]) == PackageManager.PERMISSION_GRANTED)
        {

            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (i.resolveActivity(getPackageManager())!=null){
                startActivityForResult(i, SELECAO_GALERIA);
            }

        }else {
            ActivityCompat.requestPermissions(SelecionaCorImagemTattooActivity.this, permissoes,CODIGO_PERMISSAO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verificandoPermissoes();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;
            try {
                //Selecao apenas da galeria de fotos
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                }
                //caso tenha sido escolhido uma imagem
                if (imagem!=null){

                    //Habilitando click proximo
                    temImagem = true;

                    //Removendo o label
                    lblAddImagem.setVisibility(View.GONE);

                    //mostra imagem na tela
                    imgAddImagemConsulta.setImageBitmap(imagem);

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    dadosImagem = baos.toByteArray();

                    /*
                    //Salvar imagem no firebase
                    final StorageReference imagemRef = storageRef
                            .child("imagens")
                            .child("perfil")
                            .child(identificarUsuario+".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);


                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Erro ao fazer upload da imagem", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Sucesso ao fazer upload da imagem", Toast.LENGTH_LONG).show();

                            //preparando para salvar foto no DB
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    atualizarFotoUsuario(url);
                                }
                            });
                        }
                    });*/
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}