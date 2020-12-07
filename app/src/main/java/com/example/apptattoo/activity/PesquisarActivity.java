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

import com.example.apptattoo.adapter.AdapterPesquisa;
import com.example.apptattoo.R;
import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.helper.RecyclerItemClickListener;
import com.example.apptattoo.helper.UsuarioFirebase;
import com.example.apptattoo.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PesquisarActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPesquisa;
    private SearchView searchViewPesquisa;

    private List<Usuario> listaUsuarios;
    private List<String> listaTipoUsuario;
    private DatabaseReference usuariosRef;
    private DatabaseReference tatuadoresRef;
    private AdapterPesquisa adapterPesquisa;

    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisar);

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


        listaUsuarios = new ArrayList<>();
        listaTipoUsuario = new ArrayList<>();

        //Referenciando o nó para pesquisa
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");
        tatuadoresRef = ConfiguracaoFirebase.getFirebase().child("tatuadores");


        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //Configurar Recycler
        recyclerViewPesquisa.setHasFixedSize(true);
        recyclerViewPesquisa.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapterPesquisa = new AdapterPesquisa(listaUsuarios, PesquisarActivity.this);
        recyclerViewPesquisa.setAdapter(adapterPesquisa);

        //configurar evento de clique
        recyclerViewPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerViewPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioSelecionado = listaUsuarios.get(position);
                        String tipoUsuario = listaTipoUsuario.get(position);
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
                    pesquisarUsuarios(textoDigitado);
                    pesquisarTatuadores(textoDigitado);
                }
                return true;
            }
        });
    }

    private void pesquisarTatuadores(final String texto){
        if (texto.length()>0){
            Query query = tatuadoresRef.orderByChild("nomePesquisa")
                    .startAt(texto)
                    .endAt(texto+"\uf8ff");// \uf8ff espaco em branco
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        //Impede proprio usuario de aparecer na busca
                        Usuario usuario = snapshot.getValue(Usuario.class);
                        if (idUsuarioLogado.equals(usuario.getId()))
                            continue;//Volta para o for e nao realiza a inclusao na lista

                        listaTipoUsuario.add("tatuadores");
                        listaUsuarios.add(usuario);
                    }

                    adapterPesquisa.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void pesquisarUsuarios(final String texto){
        //limpar lista
        listaUsuarios.clear();
        if (texto.length()>0){
            Query query = usuariosRef.orderByChild("nomePesquisa")
                    .startAt(texto)
                    .endAt(texto+"\uf8ff");// \uf8ff espaco em branco
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaUsuarios.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        //Impede proprio usuario de aparecer na busca
                        Usuario usuario = snapshot.getValue(Usuario.class);
                        if (idUsuarioLogado.equals(usuario.getId()))
                            continue;//Volta para o for e nao realiza a inclusao na lista

                        listaTipoUsuario.add("usuarios");
                        listaUsuarios.add(usuario);
                    }

                    adapterPesquisa.notifyDataSetChanged();

                    int total = listaUsuarios.size();
                    Log.i("Total users: ", "total: "+total);
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