package com.example.apptattoo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.apptattoo.R;
import com.example.apptattoo.adapter.AdapterGrid;
import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.helper.UsuarioFirebase;
import com.example.apptattoo.model.LocalEstudio;
import com.example.apptattoo.model.Postagem;
import com.example.apptattoo.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilPesquisadoActivity extends AppCompatActivity {

    private Button btnSeguir, btnConsultar, btnInfoPesquisado, btnPortifolioPesquisado;
    private ImageView imageViewPerfil, imgFotoEstudio;
    private CircleImageView imgPerfil;
    private TextView txtPublicacoes, txtSeguindo, txtSeguidores, lblSemPublicacoes, lblNomeEstudio, lblLocalEstudio, lblMediaAvaliacao;
    private RatingBar ratingBarAvaliacao;
    private GridView gridViewPerfil;
    private ProgressBar progressBarPerfil;
    private ConstraintLayout conteudoInfo;

    private AdapterGrid adapterGrid;

    private Usuario usuarioRecebido;
    private Usuario usuarioLogado;
    private LocalEstudio localEstudio;

    private DatabaseReference usuarioRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference usuarioRecebidoRef;
    private DatabaseReference perfilUsuarios;
    private DatabaseReference seguidoresRef;
    private DatabaseReference postagensUsuarioRef;
    private ValueEventListener valueEventListenerPerfilUsuarios;

    private String idUsuarioLogado;
    private String tipoUsuarioRecebido;
    private String postagens, seguidores, seguindo;
    private List<Postagem> listaPostagens;
    private Float nota, qtdAvaliacoes, media;
    private boolean semPostagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_pesquisado);

        usuarioRef = ConfiguracaoFirebase.getFirebase().child("usuarios");
        seguidoresRef = ConfiguracaoFirebase.getFirebase().child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        conteudoInfo = findViewById(R.id.conteudoInfo);
        conteudoInfo.setVisibility(View.INVISIBLE);

        //INICIALIZANDO COMPONENTES DE CONTEUDO INFO
        lblLocalEstudio = findViewById(R.id.lblLocalizacaoEstudioPerfilPesquisado);
        lblNomeEstudio = findViewById(R.id.lblNomeEstudioPerfilPesquisado);
        lblMediaAvaliacao = findViewById(R.id.lblMediaAvaliacaoPerfilPesquisado);
        ratingBarAvaliacao = findViewById(R.id.ratingBarAvaliacaoPerfilPesquisado);
        ratingBarAvaliacao.setFocusable(false);
        ratingBarAvaliacao.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        imgFotoEstudio = findViewById(R.id.imgEstudioPerfilPesquisado);


        gridViewPerfil = findViewById(R.id.gridPerfilPesquisado);
        lblSemPublicacoes = findViewById(R.id.lblSemPublicacoes);

        btnPortifolioPesquisado = findViewById(R.id.btnPortifolioPesquisado);
        btnInfoPesquisado = findViewById(R.id.btnInfoPesquisado);

        btnInfoPesquisado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Controlando telas de Informacoes
                btnInfoPesquisado.setText(R.string.info_selecionada);
                btnPortifolioPesquisado.setText(R.string.portifolio_padrao);
                gridViewPerfil.setVisibility(View.INVISIBLE);
                conteudoInfo.setVisibility(View.VISIBLE);
                lblSemPublicacoes.setVisibility(View.GONE);

            }
        });

        btnPortifolioPesquisado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Controlando telas de portifolio
                btnInfoPesquisado.setText(R.string.info_padrao);
                btnPortifolioPesquisado.setText(R.string.portifolio_selecionado);
                gridViewPerfil.setVisibility(View.VISIBLE);
                conteudoInfo.setVisibility(View.INVISIBLE);
                if (semPostagem){
                    lblSemPublicacoes.setVisibility(View.VISIBLE);
                }

            }
        });


        btnSeguir = findViewById(R.id.btnSeguirPerfilPesquisado);
        btnSeguir.setText("Caregando..");

        btnConsultar = findViewById(R.id.btnConsultarPerfilPesquisado);
        btnConsultar.setVisibility(View.GONE);

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SelecionaParteCorpoActivity.class);
                i.putExtra("idTatuador", usuarioRecebido.getId());
                startActivity(i);

            }
        });

        //Desabilitando logo toolbar
        imageViewPerfil = findViewById(R.id.imageViewPerfil);
        imageViewPerfil.setVisibility(View.GONE);

        imgPerfil = findViewById(R.id.imgPerfilPesquisado);
        txtPublicacoes = findViewById(R.id.txtPublicacoesPesquisado);
        txtSeguidores = findViewById(R.id.txtSeguidoresPesquisado);
        txtSeguindo = findViewById(R.id.txtSeguindoPesquisado);

        progressBarPerfil = findViewById(R.id.progressBarPerfilPesquisado);
        progressBarPerfil.setVisibility(View.GONE);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        //Recuperar dados do usuario passsados pelo recyclerClick
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuarioRecebido = (Usuario) bundle.getSerializable("usuarioSelecionado");
            tipoUsuarioRecebido = bundle.getString("tipoUsuario");
            getSupportActionBar().setTitle(usuarioRecebido.getNome());

            //SETANDO FOTO DE PERFIL
            String caminhoFoto = usuarioRecebido.getCaminhoFoto();
            if (caminhoFoto != null) {
                Uri url = Uri.parse(caminhoFoto);
                Glide.with(PerfilPesquisadoActivity.this)
                        .load(url)
                        .into(imgPerfil);
            }
        }

        usuarioRecebidoRef = ConfiguracaoFirebase.getFirebase().child(tipoUsuarioRecebido);
        //Criando referencia das postagens
        postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens")
                .child(usuarioRecebido.getId());

        carregarPostagem();

        inicializarImageLoader();

        //abre foto clicada
        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Postagem postagem = listaPostagens.get(position);
                Intent intent = new Intent(PerfilPesquisadoActivity.this, VisualizarPostagemActivity.class);
                intent.putExtra("postagem", postagem);
                intent.putExtra("usuario", usuarioRecebido);
                startActivity(intent);

            }
        });
    }

    private void recuperarAvaliacaoTatuador(){
        DatabaseReference avaliacaoTatuadorRef = ConfiguracaoFirebase.getFirebase()
                .child("avaliacaoTatuador")
                .child(usuarioRecebido.getId());
        avaliacaoTatuadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("nota")){
                    nota = dataSnapshot.child("nota").getValue(Float.class);
                    qtdAvaliacoes = dataSnapshot.child("qtd_avaliacao").getValue(Float.class);
                    media = nota/qtdAvaliacoes;
                }
                else {
                    media = 0.0f;
                }
                setandoCamposInformacoes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void recuperarDadosEstudio() {
        if (tipoUsuarioRecebido.equals("tatuadores")) {
            DatabaseReference funcionariosEstudioRef = ConfiguracaoFirebase.getFirebase()
                    .child("funcionariosEstudio")
                    .child(usuarioRecebido.getId());
            funcionariosEstudioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()){
                        localEstudio = snap.getValue(LocalEstudio.class);
                    }
                    recuperarAvaliacaoTatuador();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void setandoCamposInformacoes(){
        lblNomeEstudio.setText(localEstudio.getNomeEstudio());
        lblLocalEstudio.setText(localEstudio.getLocalidade()+", "+localEstudio.getBairro()+", "
                +localEstudio.getLogradouro()+","+localEstudio.getNumeroCasa());
        if (media==0.0f){
            lblMediaAvaliacao.setText("S/A");
            ratingBarAvaliacao.setStepSize(0.1f);//Refletindo valor na estrela
            ratingBarAvaliacao.setRating(0);
        }
        else {
            String mediaFormatada = String.format("%.2f", media);
            lblMediaAvaliacao.setText("("+mediaFormatada+")");
            ratingBarAvaliacao.setStepSize(0.1f);
            ratingBarAvaliacao.setRating(media);
        }

        Uri url = Uri.parse(localEstudio.getCaminhoFoto());
        Glide.with(PerfilPesquisadoActivity.this)
                .load(url)
                .into(imgFotoEstudio);

    }

    private void recuperarDadosUsuarioLogado() {
        usuarioLogadoRef = usuarioRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Recupera dados do usuario logado
                usuarioLogado = dataSnapshot.getValue(Usuario.class);
                verificaSegueUsuario();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Removendo campo de consulta caso seja perfil de usuario
        if (tipoUsuarioRecebido.equals("tatuadores")) {
            btnConsultar.setVisibility(View.VISIBLE);
        } else {
            btnPortifolioPesquisado.setVisibility(View.GONE);
            btnInfoPesquisado.setVisibility(View.GONE);
            gridViewPerfil.setVisibility(View.VISIBLE);
        }
        recuperarDadosPerfilUsuarios();
        recuperarDadosUsuarioLogado();
        recuperarDadosEstudio();
    }

    @Override
    protected void onStop() {
        super.onStop();
        perfilUsuarios.removeEventListener(valueEventListenerPerfilUsuarios);
    }

    private void recuperarDadosPerfilUsuarios() {
        perfilUsuarios = usuarioRecebidoRef.child(usuarioRecebido.getId());
        valueEventListenerPerfilUsuarios = perfilUsuarios.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                        if (dataSnapshot.exists()) {
                            //convertendo int para string
                            postagens = String.valueOf(usuario.getPublicacoes());
                            seguidores = String.valueOf(usuario.getSeguidores());
                            seguindo = String.valueOf(usuario.getSeguindo());
                            //Setando valores na tela
                            txtSeguindo.setText(seguindo);
                            txtSeguidores.setText(seguidores);
                            txtPublicacoes.setText(postagens);
                        }
                        if (Integer.parseInt(postagens) == 0) {
                            semPostagem=true;
                            lblSemPublicacoes.setVisibility(View.VISIBLE);
                            gridViewPerfil.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void verificaSegueUsuario() {
        //Pegando id de usuario logado e usuario que vai seguir
        DatabaseReference seguidorRef = seguidoresRef
                .child(usuarioRecebido.getId())
                .child(idUsuarioLogado);
        seguidorRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //Ja esta seguindo
                            habilitarBotaoSeguir(true);
                            deixarDeSeguir(true);
                        } else {
                            //Ainda nao esta seguindo
                            habilitarBotaoSeguir(false);

                            btnSeguir.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    salvarSeguidor(usuarioLogado, usuarioRecebido);
                                    deixarDeSeguir(true);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void inicializarImageLoader() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    private void carregarPostagem() {
        listaPostagens = new ArrayList<>();
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
                    listaPostagens.add(postagem);
                    urlFotos.add(postagem.getCaminhoFoto());
                }

                //Invertendo ordem das listas (Postagem mais nova primeiro)
                Collections.reverse(listaPostagens);
                Collections.reverse(urlFotos);

                //Config Adapter
                adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_postagem, urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void salvarSeguidor(Usuario usuarioLogado, Usuario usuarioSeguir) {
        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();
        dadosUsuarioLogado.put("nome", usuarioSeguir.getNome());
        dadosUsuarioLogado.put("caminhoFoto", usuarioSeguir.getCaminhoFoto());
        DatabaseReference seguidorRef = seguidoresRef
                .child(usuarioSeguir.getId())
                .child(usuarioLogado.getId());
        seguidorRef.setValue(dadosUsuarioLogado);

        incrementarSeguidores(usuarioLogado, usuarioSeguir);
    }

    private void deixarDeSeguir(boolean seguindo) {
        if (seguindo) {
            verificaSegueUsuario();
            btnSeguir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

                    DatabaseReference deixarDeSeguirRef = firebaseRef
                            .child("seguidores")
                            .child(usuarioRecebido.getId())
                            .child(usuarioLogado.getId());
                    deixarDeSeguirRef.removeValue();

                    decrementarSeguidores(usuarioLogado, usuarioRecebido);
                    removerFeedPostagem();
                }
            });
        }
    }

    private void removerFeedPostagem() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        DatabaseReference removerFeedRef = firebaseRef
                .child("feed")
                .child(usuarioLogado.getId())
                .child(usuarioRecebido.getId());
        removerFeedRef.removeValue();
    }

    private void decrementarSeguidores(Usuario usuarioLogado, Usuario usuarioSeguir) {
        recuperarDadosUsuarioLogado();

        //Decrementar seguindo
        int qtdSeguindo = usuarioLogado.getSeguindo() - 1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", qtdSeguindo);
        DatabaseReference usuarioSeguindo = usuarioRef.child(usuarioLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);


        //Decrementar seguidores
        int qtdSeguidores = Integer.parseInt(seguidores) - 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", qtdSeguidores);
        DatabaseReference usuarioSeguidores = usuarioRecebidoRef.child(usuarioSeguir.getId());
        usuarioSeguidores.updateChildren(dadosSeguidores);
    }

    private void incrementarSeguidores(Usuario usuarioLogado, Usuario usuarioSeguir) {
        recuperarDadosUsuarioLogado();

        //Incrementar seguindo
        int qtdSeguindo = usuarioLogado.getSeguindo() + 1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", qtdSeguindo);
        DatabaseReference usuarioSeguindo = usuarioRef.child(usuarioLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);

        //Incrementar seguidores
        int qtdSeguidores = Integer.parseInt(seguidores) + 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", qtdSeguidores);
        DatabaseReference usuarioSeguidores = usuarioRecebidoRef.child(usuarioSeguir.getId());
        usuarioSeguidores.updateChildren(dadosSeguidores);
    }

    private void habilitarBotaoSeguir(boolean segueUsuario) {
        if (segueUsuario) {
            btnSeguir.setText("Seguindo");
        } else {
            btnSeguir.setText("Seguir");
        }
    }

    //corrigindo btnVoltar
    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(PerfilPesquisadoActivity.this, MainActivity.class));
        finish();
        return false;
    }
}