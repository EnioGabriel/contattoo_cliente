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
import com.example.apptattoo.activity.VisualizaTatuagemAndamentoActivity;
import com.example.apptattoo.adapter.AdapterTatuagemAndamento;
import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.helper.RecyclerItemClickListener;
import com.example.apptattoo.helper.UsuarioFirebase;
import com.example.apptattoo.model.Proposta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TatuagemEmAndamentoFragment extends Fragment {

    private RecyclerView recyclerViewTatuagemAndamento;
    private TextView lblAgendaVazia;
    private DatabaseReference tatuagemAndamentoRef;
    private List<Proposta> listaTatuagemAndamento;
    private ValueEventListener valueEventListenerTatuagemAndamento;
    private AdapterTatuagemAndamento adapterTatuagemAndamento;

    public TatuagemEmAndamentoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tatuagemAndamentoRef = ConfiguracaoFirebase.getFirebase()
                .child("propostasAceitas")
                .child(UsuarioFirebase.getIdentificadorUsuario());

        listaTatuagemAndamento = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarPropostas();
    }

    @Override
    public void onStop() {
        super.onStop();
        tatuagemAndamentoRef.removeEventListener(valueEventListenerTatuagemAndamento);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tatuagem_em_andamento, container, false);

        recyclerViewTatuagemAndamento = view.findViewById(R.id.recyclerViewTatuagemAndamento);
        lblAgendaVazia = view.findViewById(R.id.lblAgendaVazia);

        adapterTatuagemAndamento = new AdapterTatuagemAndamento(listaTatuagemAndamento,getContext());
        recyclerViewTatuagemAndamento.setHasFixedSize(true);
        recyclerViewTatuagemAndamento.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTatuagemAndamento.setAdapter(adapterTatuagemAndamento);

        recyclerViewTatuagemAndamento.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                recyclerViewTatuagemAndamento,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        //Instanciando um objeto de Proposta
                        Proposta proposta = listaTatuagemAndamento.get(position);

                        Intent i = new Intent(getContext(), VisualizaTatuagemAndamentoActivity.class);
                        i.putExtra("tatuagemSelecionada", proposta);//enviando objeto instanciado via intent
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
        return view;
    }

    private void recuperarPropostas() {
        valueEventListenerTatuagemAndamento = tatuagemAndamentoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTatuagemAndamento.clear();
                DataSnapshot ds;
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    ds = snap;
                    if (ds != null) {
                        for (DataSnapshot ds1 : ds.getChildren()) {
                            listaTatuagemAndamento.add(ds1.getValue(Proposta.class));
                        }
                    }
                }
                adapterTatuagemAndamento.notifyDataSetChanged();

                if (!dataSnapshot.exists()){
                    recyclerViewTatuagemAndamento.setVisibility(View.GONE);
                    lblAgendaVazia.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}