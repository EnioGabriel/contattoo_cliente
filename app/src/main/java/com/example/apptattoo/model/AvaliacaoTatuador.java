package com.example.apptattoo.model;

import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class AvaliacaoTatuador {
    private int qtdAvaliacao;
    private float notaAvaliacao, media;
    private String nomeUsuario, idUsuario;

    public AvaliacaoTatuador() {
    }

    public void salvarAvaliacao(String idTatuador, String idConsulta, Float nota){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", UsuarioFirebase.getDadosUsuarioLogado().getNomeUsuario());
        dadosUsuario.put("idUsuario", UsuarioFirebase.getIdentificadorUsuario());

        DatabaseReference postagensCurtidasRef = firebaseRef
                .child("avaliacaoTatuador")
                .child(idTatuador)
                .child(UsuarioFirebase.getIdentificadorUsuario())
                .child(idConsulta);
        postagensCurtidasRef.setValue(dadosUsuario);

        //Incrementando curtida
        atualizarQtdAvaliacao(1, idTatuador);
        atualizarNotaAvaliacao(nota, idTatuador);
    }

    public void atualizarQtdAvaliacao(int numAvaliadores,String idTatuador){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        DatabaseReference avaliacaoTatuadorRef = firebaseRef
                .child("avaliacaoTatuador")
                .child(idTatuador)
                .child("qtd_avaliacao");
        setQtdAvaliacao(getQtdAvaliacao()+numAvaliadores);
        avaliacaoTatuadorRef.setValue(getQtdAvaliacao());
    }

    public void atualizarNotaAvaliacao(float nota ,String idTatuador){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        DatabaseReference avaliacaoTatuadorRef = firebaseRef
                .child("avaliacaoTatuador")
                .child(idTatuador)
                .child("nota");
        setNotaAvaliacao(getNotaAvaliacao()+nota);
        avaliacaoTatuadorRef.setValue(getNotaAvaliacao());
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void atualizarMedia(){

    }

    public int getQtdAvaliacao() {
        return qtdAvaliacao;
    }

    public void setQtdAvaliacao(int qtdAvaliacao) {
        this.qtdAvaliacao = qtdAvaliacao;
    }

    public float getNotaAvaliacao() {
        return notaAvaliacao;
    }

    public void setNotaAvaliacao(float notaAvaliacao) {
        this.notaAvaliacao = notaAvaliacao;
    }

    public float getMedia() {
        return media;
    }

    public void setMedia(float media) {
        this.media = media;
    }
}
