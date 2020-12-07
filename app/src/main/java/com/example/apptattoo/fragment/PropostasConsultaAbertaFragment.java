package com.example.apptattoo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptattoo.R;
import com.example.apptattoo.activity.AnalisePropostaActivity;
import com.example.apptattoo.adapter.AdapterPropostas;
import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.helper.RecyclerItemClickListener;
import com.example.apptattoo.helper.UsuarioFirebase;
import com.example.apptattoo.model.Proposta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PropostasConsultaAbertaFragment extends Fragment {

    private RecyclerView recyclerViewPropostas;

    private String idClienteLogado;
    private List<Proposta> listaProposta;

    private DatabaseReference consultaAbertaRef;

    private AdapterPropostas adapterPropostas;

    private ValueEventListener valueEventListenerConsultaAberta;

    public PropostasConsultaAbertaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idClienteLogado = UsuarioFirebase.getIdentificadorUsuario();

        consultaAbertaRef = ConfiguracaoFirebase.getFirebase()
                .child("orcamentosAbertos")
                .child(idClienteLogado);

        listaProposta = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_propostas_consulta_aberta, container, false);

        recyclerViewPropostas = view.findViewById(R.id.recyclerViewPropostasConsultaAberta);

        adapterPropostas = new AdapterPropostas(listaProposta, getContext());
        recyclerViewPropostas.setHasFixedSize(true);
        recyclerViewPropostas.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPropostas.setAdapter(adapterPropostas);

        recyclerViewPropostas.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                recyclerViewPropostas,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        //Instanciando um objeto de Proposta
                        Proposta proposta = listaProposta.get(position);

                        Intent i = new Intent(getContext(), AnalisePropostaActivity.class);
                        i.putExtra("tipoProposta", "consultaAberta");
                        i.putExtra("propostaSelecionada", proposta);//enviando objeto instanciado via intent
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

    @Override
    public void onStart() {
        super.onStart();
        recuperarConsultaAberta();
    }

    @Override
    public void onStop() {
        super.onStop();
        consultaAbertaRef.removeEventListener(valueEventListenerConsultaAberta);
    }

    private void recuperarConsultaAberta(){
        valueEventListenerConsultaAberta = consultaAbertaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaProposta.clear();
                DataSnapshot ds;
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    ds = snap;
                    if (ds != null) {
                        for (DataSnapshot ds1 : ds.getChildren()) {
                            listaProposta.add(ds1.getValue(Proposta.class));
                        }
                    }
                }
                Collections.reverse(listaProposta);//Invertendo ordem da lista
                adapterPropostas.notifyDataSetChanged();

                if (!dataSnapshot.exists()){
                    //recyclerViewMensagem.setVisibility(View.GONE);
                    //lblMsgSeguir.setVisibility(View.VISIBLE);
                    //txtApagaDps.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}