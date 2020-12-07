package com.example.apptattoo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.example.apptattoo.R;
import com.example.apptattoo.adapter.AdapterPesquisa;
import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.helper.RecyclerItemClickListener;
import com.example.apptattoo.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelecionaTatuadorConsultaActivity extends AppCompatActivity {

    private DatabaseReference tatuadoresRef;

    private RecyclerView recyclerViewPesquisa;
    private AdapterPesquisa adapterPesquisa;
    private SearchView searchViewPesquisa;
    private List<Usuario> listaUsuarios = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleciona_tatuador_consulta);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        recyclerViewPesquisa = findViewById(R.id.recyclerViewPesquisa);
        searchViewPesquisa = findViewById(R.id.searchViewPesquisa);
        searchViewPesquisa.setQueryHint("Pesquisar");
        searchViewPesquisa.onActionViewExpanded();

        tatuadoresRef = ConfiguracaoFirebase.getFirebase().child("tatuadores");

        //Configurar Recycler
        recyclerViewPesquisa.setHasFixedSize(true);
        recyclerViewPesquisa.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapterPesquisa = new AdapterPesquisa(listaUsuarios, SelecionaTatuadorConsultaActivity.this);
        recyclerViewPesquisa.setAdapter(adapterPesquisa);

        //Configurar pesquisa
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String novoTexto) {
                String textoDigitado = novoTexto.toLowerCase();
                if (novoTexto.length()==0){//Limpando tela caso usuario apague toda pesquisa
                    listaUsuarios.clear();
                    adapterPesquisa.notifyDataSetChanged();
                    recyclerViewPesquisa.setVisibility(View.GONE);
                }else{
                    recyclerViewPesquisa.setVisibility(View.VISIBLE);
                    pesquisarTatuadores(textoDigitado);
                }
                return true;
            }
        });

        //configurar evento de clique
        recyclerViewPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerViewPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioSelecionado = listaUsuarios.get(position);
                        String tipoUsuario = "tatuadores";
                        Intent i = new Intent(getApplicationContext(), PerfilPesquisadoActivity.class);
                        i.putExtra("usuarioSelecionado", usuarioSelecionado);
                        i.putExtra("tipoUsuario", tipoUsuario);
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
    }

    private void pesquisarTatuadores(final String texto){
        if (texto.length()>0){
            Query query = tatuadoresRef.orderByChild("nomePesquisa")
                    .startAt(texto)
                    .endAt(texto+"\uf8ff");// \uf8ff espaco em branco
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaUsuarios.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Usuario usuario = snapshot.getValue(Usuario.class);
                        listaUsuarios.add(usuario);
                    }

                    adapterPesquisa.notifyDataSetChanged();

                    int total = listaUsuarios.size();
                    Log.i("Total users: ", "total usuarios: "+total);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}