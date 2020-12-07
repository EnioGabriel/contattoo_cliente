package com.example.apptattoo.model;

import android.util.Log;

import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.example.apptattoo.helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class ConsultaTatuagem {
    private String cor, fotoConsulta, nomeUsuario, fotoUsuario, idConsulta, idCliente, localCorpo, observacoes, telefoneUsuario;
    private int altura, largura;
    private Usuario usuarioLogado;

    public ConsultaTatuagem() {
        //gerando Id da consulta assim q o costrutor for chamado
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference consultaRef = firebaseRef.child("consultaAberta");
        String idConsulta = consultaRef.push().getKey();//push - gera um id unico
        setIdConsulta(idConsulta);

        //preparando dados do usuario logado
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
    }

    public boolean salvarConsultaFechada(String idTatuador){

        Map objeto = new HashMap<>();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Atribuindo dados do usuario logado
        setNomeUsuario(UsuarioFirebase.getNomeUsuario(usuarioLogado.getId()));
        setFotoUsuario(usuarioLogado.getCaminhoFoto());
        setTelefoneUsuario(usuarioLogado.getCelular());

        //Montar objeto para salvar
        HashMap<String, Object> dadosConsulta = new HashMap<>();
        dadosConsulta.put("nomeUsuario", getNomeUsuario());
        dadosConsulta.put("telefoneUsuario", getTelefoneUsuario());
        dadosConsulta.put("fotoUsuario", getFotoUsuario());
        dadosConsulta.put("idTatuador", idTatuador);
        dadosConsulta.put("idCliente", getIdCliente());
        dadosConsulta.put("fotoConsulta", getFotoConsulta());
        dadosConsulta.put("observacoes", getObservacoes());
        dadosConsulta.put("idConsulta", getIdConsulta());
        dadosConsulta.put("altura", getAltura());
        dadosConsulta.put("largura", getLargura());
        dadosConsulta.put("localCorpo", getLocalCorpo());
        dadosConsulta.put("cor", getCor());

        String idsAtualizaçao = "/"+ idTatuador + "/" +getIdCliente()+"/"+getIdConsulta();
        objeto.put("/consultaFechada"+idsAtualizaçao,dadosConsulta);

        firebaseRef.updateChildren(objeto);
        return true;
    }

    public boolean salvarConsultaAberta(DataSnapshot tatuadoresSnapshot){

        Map objeto = new HashMap<>();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        for (DataSnapshot tatuadores : tatuadoresSnapshot.getChildren()){

            String idTatuador = tatuadores.getKey();

            //Atribuindo dados do usuario logado
            setNomeUsuario(UsuarioFirebase.getNomeUsuario(usuarioLogado.getId()));// Se der bug usar esse: usuarioLogado.getNomeUsuario();
            setFotoUsuario(usuarioLogado.getCaminhoFoto());
            setTelefoneUsuario(usuarioLogado.getCelular());


            String combinacaoId =  "/"+ getIdCliente()+"/"+getIdConsulta()+ "/"+ idTatuador;
            objeto.put("/consultaAberta"+ combinacaoId,this );
        }
        firebaseRef.updateChildren(objeto);
        return true;
    }
    /*
    public boolean salvarConsultaAberta(){

        Map objeto = new HashMap<>();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Atribuindo dados do usuario logado
        setNomeUsuario(UsuarioFirebase.getNomeUsuario(usuarioLogado.getId()));
        setFotoUsuario(usuarioLogado.getCaminhoFoto());
        setTelefoneUsuario(usuarioLogado.getCelular());

        String combinacaoId = "/"+getIdConsulta()+ "/"+ getIdCliente();
        objeto.put("/consultaAberta"+ combinacaoId,this );

        firebaseRef.updateChildren(objeto);
        return true;
    }
     */

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getTelefoneUsuario() {
        return telefoneUsuario;
    }

    public void setTelefoneUsuario(String telefoneUsuario) {
        this.telefoneUsuario = telefoneUsuario;
    }

    public String getFotoConsulta() {
        return fotoConsulta;
    }

    public void setFotoConsulta(String fotoConsulta) {
        this.fotoConsulta = fotoConsulta;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(String idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getLocalCorpo() {
        return localCorpo;
    }

    public void setLocalCorpo(String localCorpo) {
        this.localCorpo = localCorpo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public int getLargura() {
        return largura;
    }

    public void setLargura(int largura) {
        this.largura = largura;
    }
}
