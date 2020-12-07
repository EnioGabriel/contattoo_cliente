package com.example.apptattoo.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.apptattoo.R;
import com.example.apptattoo.activity.VisualizaTatuagemFinalizadaActivity;
import com.example.apptattoo.adapter.AdapterTatuagemFinalizada;
import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.helper.RecyclerItemClickListener;
import com.example.apptattoo.helper.UsuarioFirebase;
import com.example.apptattoo.model.Evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TatuagemFinalizadaFragment extends Fragment {

    private RecyclerView recyclerViewTatuagemFinalizada;
    private TextView lblAgendaVazia, lblAgendarTatuagem;
    private DatabaseReference tatuagemFinalizadaRef;
    private List<Evento> listaTatuagemFinalizada;
    private ValueEventListener valueEventListenerTatuagemFinalizada;
    private AdapterTatuagemFinalizada adapterTatuagemFinalizada;

    public TatuagemFinalizadaFragment() {
        tatuagemFinalizadaRef = ConfiguracaoFirebase.getFirebase()
                .child("tatuagemFinalizada")
                .child(UsuarioFirebase.getIdentificadorUsuario());

        listaTatuagemFinalizada = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarTatuagemFinalizada();
    }

    @Override
    public void onStop() {
        super.onStop();
        tatuagemFinalizadaRef.removeEventListener(valueEventListenerTatuagemFinalizada);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tatuagem_finalizada, container, false);

        recyclerViewTatuagemFinalizada = view.findViewById(R.id.recyclerViewTatuagemFinalizada);

        recyclerViewTatuagemFinalizada.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(), recyclerViewTatuagemFinalizada, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Instanciando um objeto de Proposta
                Evento evento = listaTatuagemFinalizada.get(position);

                Intent i = new Intent(getContext(), VisualizaTatuagemFinalizadaActivity.class);
                i.putExtra("tatuagemSelecionada", evento);//enviando objeto instanciado via intent
                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }
        ));

        adapterTatuagemFinalizada = new AdapterTatuagemFinalizada(listaTatuagemFinalizada,getContext());
        recyclerViewTatuagemFinalizada.setHasFixedSize(true);
        recyclerViewTatuagemFinalizada.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTatuagemFinalizada.setAdapter(adapterTatuagemFinalizada);
        return view;
    }
    private void recuperarTatuagemFinalizada() {
        valueEventListenerTatuagemFinalizada = tatuagemFinalizadaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTatuagemFinalizada.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : snap.getChildren()){
                        listaTatuagemFinalizada.add(ds.getValue(Evento.class));
                    }
                }
                adapterTatuagemFinalizada.notifyDataSetChanged();

                if (!dataSnapshot.exists()){
                    /*
                    recyclerViewTatuagemFinalizada.setVisibility(View.GONE);
                    lblAgendarTatuagem.setVisibility(View.VISIBLE);
                    lblAgendaVazia.setVisibility(View.VISIBLE);
                     */
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}