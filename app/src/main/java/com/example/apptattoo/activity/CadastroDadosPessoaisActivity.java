package com.example.apptattoo.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apptattoo.R;
import com.example.apptattoo.helper.CustomTextWatcher;
import com.example.apptattoo.helper.ValidarCPF;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.Calendar;

public class CadastroDadosPessoaisActivity extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener dataSetListener;
    private TextInputEditText txtCadNome, txtdataNascimento, txtCadCPF, txtCadCelular;
    private Button btnProximo;
    private String CPF, Celular;
    boolean cpfValido = false;
    private int diaAtual, mesAtual,anoAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_dados_pessoais);

        txtdataNascimento = findViewById(R.id.txtDataNascimento);
        txtCadNome = findViewById(R.id.txtCadNome);
        txtCadCPF = findViewById(R.id.txtCadCPF);
        txtCadCelular = findViewById(R.id.txtCadCelular);
        btnProximo = findViewById(R.id.btnProximo);

        //Formatando Campos
        SimpleMaskFormatter smfCPF      = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        SimpleMaskFormatter smfCelular  = new SimpleMaskFormatter("(NN)NNNNN-NNNN");

        MaskTextWatcher mtwCPF      = new MaskTextWatcher(txtCadCPF, smfCPF);
        MaskTextWatcher mtwCelular  = new MaskTextWatcher(txtCadCelular, smfCelular);

        txtCadCPF.addTextChangedListener(mtwCPF);
        txtCadCelular.addTextChangedListener(mtwCelular);

        //ValidandoCampos
        listeners();

        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //verifica se os campos estão vazios
                isVazio();

                int idade = validaIdade(anoAtual,mesAtual,diaAtual);
                //Impede usuario <18 de prosseguir com o cadastro
                if (idade<18&&!isVazio()){
                    dialogIdade();
                }

                //Enviando dados para uma nova intent
                if (!isVazio()&&cpfValido&&idade>=18) {
                    Intent intent = new Intent(CadastroDadosPessoaisActivity.this, CadastroDadosContaActivity.class);
                    intent.putExtra("nome", txtCadNome.getText().toString());
                    intent.putExtra("cpf", CPF);
                    intent.putExtra("nascimento", txtdataNascimento.getText().toString());
                    intent.putExtra("celular", txtCadCelular.getText().toString());
                    startActivityForResult(intent,1000);
                }
            }
        });
    }//Fim onCreate

    private void dialogIdade(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Dialog);
        builder.setMessage("Você não possui a idade adequada para utilizar esse aplicativo!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).show();
    }

    private boolean isVazio(){
        if (txtCadNome.length()==0){
            txtCadNome.setError("Preencha seu nome!");
            return true;
        }
        if (txtCadCPF.length()==0){
            txtCadCPF.setError("Preencha este campo!");
            return true;
        }
        if (txtdataNascimento.length()==0){
            txtdataNascimento.setError("Preencha este campo!");
            return true;
        }
        if (txtCadCelular.length()==0){
            txtCadCelular.setError("Preencha este campo!");
            return true;
        }
        return false;
    }


    private int validaIdade(int ano, int mes, int dia){

        Calendar dataAniversario = Calendar.getInstance();
        dataAniversario.set(ano, mes, dia);//setando a data passada por parametro

        Calendar dataAtual = Calendar.getInstance();//instanciando a dataAtual

        //Data atual nao pode ser menor que a data do aniversario
        if (dataAtual.getTimeInMillis() < dataAniversario.getTimeInMillis())
            txtdataNascimento.setError("Idade incorreta");

        int idade = dataAtual.get(Calendar.YEAR) - dataAniversario.get(Calendar.YEAR);//Pega idade apenas em anos

        //verifica os dias e meses
        if (dataAniversario.get(Calendar.MONTH) > dataAtual.get(Calendar.MONTH) ||
                (dataAniversario.get(Calendar.MONTH) == dataAtual.get(Calendar.MONTH) &&
                        dataAniversario.get(Calendar.DATE) > dataAtual.get(Calendar.DATE)))
            idade--;
        return idade;
    }

    private void listeners(){

        txtCadCPF.addTextChangedListener(new CustomTextWatcher(txtCadCPF,1500) {
            @Override
            public void textWasChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Pega dados "limpos" para validacao
                        CPF = txtCadCPF.getText().toString().replace(".","").replace("-","");

                        if (CPF.length()>0) {
                            //Checa CPF
                            cpfValido = ValidarCPF.isCPF(CPF);

                            if (!cpfValido) {
                                txtCadCPF.setError("CPF inválido");
                            }
                        }
                    }
                });
            }
        });

        /*
        txtCadCPF.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                //Pega dados "limpos" para validacao
                CPF = txtCadCPF.getText().toString().replace(".","").replace("-","");

                if (CPF.length()>0) {
                    //Checa CPF
                    cpfValido = ValidarCPF.isCPF(CPF);

                    if (cpfValido) {
                        Toast.makeText(getApplicationContext(), "Teste " + CPF, Toast.LENGTH_LONG).show();
                    } else
                        txtCadCPF.setError("CPF inválido");
                }
            }
        });//Fim CPF
         */

        //DataNascimento
        txtdataNascimento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {//apenas qnd o usuario clicar
                    txtdataNascimento.setFocusable(true);
                    if(diaAtual<=0) {//só na 1° vez
                        Calendar calendario = Calendar.getInstance();
                        diaAtual = calendario.get(Calendar.DAY_OF_MONTH);
                        mesAtual = calendario.get(Calendar.MONTH);
                        anoAtual = calendario.get(Calendar.YEAR);
                    }
                    //criando calendario
                    DatePickerDialog dialog = new DatePickerDialog(
                            CadastroDadosPessoaisActivity.this,
                            android.R.style.Theme_Holo_Dialog_MinWidth,
                            dataSetListener, anoAtual, mesAtual, diaAtual);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    txtdataNascimento.setError(null);
                    txtdataNascimento.clearFocus();//Tira bug do clique
                }
            }
        });
        //SETANDO DATA
        dataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
                mes = mes+1;//add 1, pq mes inicia com 0
                DecimalFormat df = new DecimalFormat("00");//formatando para sempre ter 2 digitos em dia e mes
                txtdataNascimento.setText(df.format(dia)+"/"+df.format(mes)+"/"+ano);

                //passando a data selecionada como padrao inicial
                diaAtual = dia;
                mesAtual = mes-1;
                anoAtual = ano;
            }
        };//Fim data nascimento
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1000){
            finish();
        }
    }
}