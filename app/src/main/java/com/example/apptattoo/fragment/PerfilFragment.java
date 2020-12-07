package com.example.apptattoo.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.apptattoo.R;
import com.example.apptattoo.activity.EditarPerfilActivity;
import com.example.apptattoo.adapter.AdapterGrid;
import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.helper.UsuarioFirebase;
import com.example.apptattoo.model.Postagem;
import com.example.apptattoo.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {
    private ProgressBar progressBarPerfil;
    private TextView txtPublicacoes, txtSeguidores, txtSeguindo;
    private Button btnEditarPerfil;
    private CircleImageView imgPerfil;
    private GridView gridViewPerfil;

    private AdapterGrid adapterGrid;

    private ValueEventListener valueEventListenerPerfilUsuarios;

    private FirebaseUser usuarioPerfl;

    private DatabaseReference postagensUsuarioRef;
    private DatabaseReference perfilUsuarios;
    private DatabaseReference usuarioRef;

    private Context contexto;



    public PerfilFragment() {
        // Required empty public constructor
    }

    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void recuperarFoto(){
        //SETANDO FOTO DE PERFIL
        Uri url = usuarioPerfl.getPhotoUrl();
        //Se tiver foto, faz download e coloca na ImageView
        if (url != null) {
            Glide.with(getActivity())
                    .load(url)
                    .into(imgPerfil);
        }else {
            imgPerfil.setImageResource(R.drawable.avatar);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_perfil, container, false);

        usuarioPerfl = UsuarioFirebase.getUsuarioAtual();

        //config dos componentes
        txtPublicacoes = view.findViewById(R.id.txtPublicacoes);
        txtSeguidores = view.findViewById(R.id.txtSeguidores);
        txtSeguindo = view.findViewById(R.id.txtSeguindo);
        btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        gridViewPerfil = view.findViewById(R.id.gridPerfil);
        imgPerfil = view.findViewById(R.id.imgPerfil);
        progressBarPerfil = view.findViewById(R.id.progressBarPerfil);

        progressBarPerfil.setVisibility(View.GONE);

        btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(intent);
            }
        });
        //Criando referencia das postagens
        postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens")
                .child(usuarioPerfl.getUid());

        carregarPostagem();

        inicializarImageLoader();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarFoto();
        recuperarDadosPerfilUsuarios();
    }

    @Override
    public void onStop() {
        super.onStop();
        perfilUsuarios.removeEventListener(valueEventListenerPerfilUsuarios);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        contexto = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        contexto = null;
    }

    private void recuperarDadosPerfilUsuarios(){
        usuarioRef = ConfiguracaoFirebase.getFirebase().child("usuarios");
        perfilUsuarios = usuarioRef.child(UsuarioFirebase.getIdentificadorUsuario());
        valueEventListenerPerfilUsuarios = perfilUsuarios.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                        //convertendo int para string
                        String postagens = String.valueOf(usuario.getPublicacoes());
                        String seguidores = String.valueOf(usuario.getSeguidores());
                        String seguindo = String.valueOf(usuario.getSeguindo());
                        //Setando valores na tela
                        txtSeguindo.setText(seguindo);
                        txtSeguidores.setText(seguidores);
                        txtPublicacoes.setText(postagens);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }
    private void inicializarImageLoader(){
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    private void carregarPostagem(){
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Config tamanho do gridLayout
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;//getDisplayMetrics() pega o tamanho da tela do celular
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth(tamanhoImagem);

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Postagem postagem = ds.getValue(Postagem.class);
                    urlFotos.add(postagem.getCaminhoFoto());
                    Log.i("postagem", "url: " + postagem.getCaminhoFoto());
                }
                //Invertendo ordem da lista (Postagem mais nova primeiro)
                Collections.reverse(urlFotos);

                //Config Adapter
                adapterGrid = new AdapterGrid(getActivity(), R.layout.grid_postagem, urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}