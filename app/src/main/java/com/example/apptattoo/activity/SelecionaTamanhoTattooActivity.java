package com.example.apptattoo.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.apptattoo.R;

public class SelecionaTamanhoTattooActivity extends AppCompatActivity {

    private Button btnConfirmar, btnCancelar, btnProximo;
    private EditText txtAltura, txtLargura;
    private RadioGroup radioGroupTamanhoTattoo;
    private RadioButton radioButtonClienteDefine, radioButtonTatuadorDefine;
    private int altura, largura;
    private String recebeParteCorpo, recebeIdTatuador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleciona_tamanho);

        //Pegando a parte do corpo selecionada que veio da activity anterior
        Intent intent = getIntent();
        recebeParteCorpo = intent.getStringExtra("parteCorpo");
        recebeIdTatuador = intent.getStringExtra("idTatuador");

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        radioButtonClienteDefine = findViewById(R.id.radioButtonClienteDefine);
        radioButtonTatuadorDefine = findViewById(R.id.radioButtonTatuadorDefine);

        btnProximo = findViewById(R.id.btnProximoTamanho);
        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioButtonClienteDefine.isChecked()||radioButtonTatuadorDefine.isChecked()){
                    novaTela();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Selecione uma das opções",Toast.LENGTH_SHORT).show();
                }
            }
        });

        radioGroupTamanhoTattoo = findViewById(R.id.radioGroupTamanhoTattoo);
        radioGroupTamanhoTattoo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int opcaoSelecionada = radioGroup.indexOfChild(radioButton);

                if (opcaoSelecionada==0){
                    radioGroupTamanhoTattoo.clearCheck();
                    abrirDialog();
                }
            }
        });
    }

    private void novaTela(){
        Intent i = new Intent(getApplicationContext(), SelecionaCorImagemTattooActivity.class);
        i.putExtra("idTatuador", recebeIdTatuador);
        i.putExtra("alturaTattoo", String.valueOf(altura));
        i.putExtra("larguraTattoo", String.valueOf(largura));
        i.putExtra("parteCorpo", recebeParteCorpo);
        startActivity(i);
    }

    private void abrirDialog(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        dialogBuilder.setCancelable(false);//bloqueia a tela
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_tamanho_tattoo, null);

        btnConfirmar = dialogView.findViewById(R.id.btnConfirmarTamanho);
        btnCancelar = dialogView.findViewById(R.id.btnCancelarTamanho);

        txtAltura = dialogView.findViewById(R.id.txtAlturaTattoo);
        txtLargura = dialogView.findViewById(R.id.txtLarguraTattoo);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmarCampos()) {
                    altura = Integer.parseInt(txtAltura.getText().toString());
                    largura = Integer.parseInt(txtLargura.getText().toString());
                    dialogBuilder.dismiss();
                    novaTela();
                }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private boolean confirmarCampos(){
        if (txtAltura.getText().toString().equals("")||txtAltura.getText().toString().equals("0")) {
            txtAltura.setError("Preencha esse campo!");
            return false;
        }
        if (txtLargura.getText().toString().equals("")||txtLargura.getText().toString().equals("0")){
            txtLargura.setError("Preencha esse campo!");
            return false;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}