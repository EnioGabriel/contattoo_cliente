package com.example.apptattoo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.apptattoo.R;
import com.example.apptattoo.adapter.AdapterHorario;
import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.helper.RecyclerViewClickInterface;
import com.example.apptattoo.helper.UsuarioFirebase;
import com.example.apptattoo.model.Horario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelecionaHorario extends AppCompatActivity implements RecyclerViewClickInterface {

    private RecyclerView recyclerViewSelecionaHorario;
    private AdapterHorario adapterHorario;
    private List<Horario> listaHorario;
    private DatabaseReference confirmarHorarioRef;
    private String idCliente, idTatuador, idConsulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleciona_horario);
        listaHorario = new ArrayList<>();

        idCliente = UsuarioFirebase.getIdentificadorUsuario();

        recyclerViewSelecionaHorario = findViewById(R.id.recyclerViewSelecionaHorario);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idTatuador =  bundle.getString("idTatuador");
            idConsulta =  bundle.getString("idConsulta");
        }

        //Configurar Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        confirmarHorarioRef = ConfiguracaoFirebase.getFirebase()
                .child("confirmarHorario")
                .child(idCliente)
                .child(idTatuador)
                .child(idConsulta);

        recuperarHorarios();

        adapterHorario = new AdapterHorario(listaHorario, getApplicationContext(), this);
        recyclerViewSelecionaHorario.setHasFixedSize(true);
        recyclerViewSelecionaHorario.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewSelecionaHorario.setAdapter(adapterHorario);

    }

    private void recuperarHorarios() {
        confirmarHorarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaHorario.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    listaHorario.add(snap.getValue(Horario.class));
                }
                adapterHorario.notifyDataSetChanged();
            }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){

            }
        });
    }

    //Acessando btnAceitar do RecyclerView
    @Override
    public void onItemClick(int position) {
        Intent intent=new Intent();
        Horario horario = listaHorario.get(position);
        intent.putExtra("horarioSelecionado", horario);
        setResult(RESULT_OK,intent);
        finish();
    }

    // btnVoltar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}