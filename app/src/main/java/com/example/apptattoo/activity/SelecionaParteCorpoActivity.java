package com.example.apptattoo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.apptattoo.R;
import com.example.apptattoo.adapter.AdapterPartesCorpo;

public class SelecionaParteCorpoActivity extends AppCompatActivity {

    private RecyclerView RecyclerViewPartesCorpo;
    private Button btnProximo;

    private AdapterPartesCorpo adapterPartesCorpo;

    private String[] itensCorpo = {"Antebraço - Interno","Antebraço - Externo","Atrás da orelha","Barriga","Braço - Completo","Braço - Cima","Cabeça" ,"Canela","Costas - Completo", "Costas - Baixo", "Costas - Cima","Costelas","Cotovelo","Coxa - Frontal","Coxa - Lateral","Coxa - Traseira","Dedo","Garganta","Glúteo","Joelho", "Mão","Ombro","Panturrilha", "Pé", "Peito - Completo", "Peito - Esquerdo","Peito - Centro","Peito - Direito","Perna - Completo","Pescoço","Punho","Quadril","Rosto","Tornozelo"};
    String recebeIdTatuador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleciona_parte_corpo);

        //Recuperando id tatuador caso seja consulta fechada
        Intent intent = getIntent();
        recebeIdTatuador = intent.getStringExtra("idTatuador");

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        RecyclerViewPartesCorpo = findViewById(R.id.RecyclerViewPartesCorpo);
        btnProximo = findViewById(R.id.btnProximoConsulta);

        //Configurar Recycler
        RecyclerViewPartesCorpo.setHasFixedSize(true);
        RecyclerViewPartesCorpo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //Adicionar adaptador na lista
        adapterPartesCorpo = new AdapterPartesCorpo(getApplicationContext(),R.layout.linha_list_view,itensCorpo);
        RecyclerViewPartesCorpo.setAdapter(adapterPartesCorpo);

        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapterPartesCorpo.recuperaItemCLicado()!=null){
                    String parteCorpo = adapterPartesCorpo.recuperaItemCLicado();
                    Intent i = new Intent(getApplicationContext(),SelecionaTamanhoTattooActivity.class);
                    i.putExtra("parteCorpo",parteCorpo);
                    i.putExtra("idTatuador", recebeIdTatuador);
                    startActivity(i);
                }
            }
        });
/*
        //configurar evento de clique
        RecyclerViewPartesCorpo.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                RecyclerViewPartesCorpo,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String parteCorpo = itensCorpo[position];
                        //adapterPartesCorpo.AdapterPartesCorpo(true, position);
                        Toast.makeText(getApplicationContext(),"ENTROU "+parteCorpo,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
 */
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}