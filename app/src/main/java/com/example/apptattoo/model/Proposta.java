package com.example.apptattoo.model;

import android.util.Log;

import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;

public class Proposta implements Serializable {

    private String nomeTatuador, fotoTatuador, valorTotalSessaoUnica, tempoSessaoUnica,
            tempoSessoesMultiplas, valorTotalSessaoMultipla, valorPorSessao, tipoOrcamento,
            fotoTatuagem, idTatuador, idConsulta, data, hora, fotoUsuario;
    private int qtdSessoes;

    private  HashMap<String, Object> sessaoUnica = new HashMap<>(), sessaoMultipla = new HashMap<>();

    private DatabaseReference propostasRef;

    public Proposta() {
    }

    public Proposta(String idCliente, String idTatuador, String idConsultaRef) {

        //gerando Id da proposta assim q o costrutor for chamado
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference rootReef = firebaseRef.child("propostasAceitas");
        //String idProposta = rootReef.push().getKey();//push - gera um id unico

        propostasRef = rootReef.child(idCliente)
                .child(idTatuador)
                .child(idConsultaRef);
    }

    public boolean salvarProposta(String tipoOrcamento){
        if (tipoOrcamento.equals("sessaoUnica")){
            //Montar hashMap para salvar apenas os dados de sessao unica
            sessaoUnica.put("idTatuador", getIdTatuador());
            sessaoUnica.put("nomeTatuador", getNomeTatuador());
            sessaoUnica.put("fotoTatuador", getFotoTatuador());
            sessaoUnica.put("fotoUsuario", getFotoUsuario());
            sessaoUnica.put("tempoSessaoUnica", getTempoSessaoUnica());
            sessaoUnica.put("valorTotalSessaoUnica", getValorTotalSessaoUnica());
            sessaoUnica.put("tipoOrcamento", getTipoOrcamento());
            sessaoUnica.put("fotoTatuagem", getFotoTatuagem());
            sessaoUnica.put("idConsulta", getIdConsulta());
            sessaoUnica.put("data", getData());
            sessaoUnica.put("hora", getHora());
            propostasRef.setValue(sessaoUnica);
        }else {
            //Montar hashMap para salvar apenas os dados de sessao multipla
            sessaoMultipla.put("idTatuador", getIdTatuador());
            sessaoMultipla.put("nomeTatuador", getNomeTatuador());
            sessaoMultipla.put("fotoTatuador", getFotoTatuador());
            sessaoUnica.put("fotoUsuario", getFotoUsuario());
            sessaoMultipla.put("valorPorSessao", getValorPorSessao());
            sessaoMultipla.put("tempoSessoesMultiplas", getTempoSessoesMultiplas());
            sessaoMultipla.put("valorTotalSessaoMultipla", getValorTotalSessaoMultipla());
            sessaoMultipla.put("qtdSessoes", getQtdSessoes());
            sessaoMultipla.put("tipoOrcamento", getTipoOrcamento());
            sessaoMultipla.put("fotoTatuagem", getFotoTatuagem());
            sessaoMultipla.put("idConsulta", getIdConsulta());
            sessaoMultipla.put("data", getData());
            sessaoMultipla.put("hora", getHora());
            propostasRef.setValue(sessaoMultipla);
        }
        return true;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(String idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getIdTatuador() {
        return idTatuador;
    }

    public void setIdTatuador(String idTatuador) {
        this.idTatuador = idTatuador;
    }

    public String getFotoTatuagem() {
        return fotoTatuagem;
    }

    public void setFotoTatuagem(String fotoTatuagem) {
        this.fotoTatuagem = fotoTatuagem;
    }

    public String getTempoSessaoUnica() {
        return tempoSessaoUnica;
    }

    public void setTempoSessaoUnica(String tempoSessaoUnica) {
        this.tempoSessaoUnica = tempoSessaoUnica;
    }

    public String getTempoSessoesMultiplas() {
        return tempoSessoesMultiplas;
    }

    public void setTempoSessoesMultiplas(String tempoSessoesMultiplas) {
        this.tempoSessoesMultiplas = tempoSessoesMultiplas;
    }

    public String getValorTotalSessaoMultipla() {
        return valorTotalSessaoMultipla;
    }

    public void setValorTotalSessaoMultipla(String valorTotalSessaoMultipla) {
        this.valorTotalSessaoMultipla = valorTotalSessaoMultipla;
    }

    public String getValorPorSessao() {
        return valorPorSessao;
    }

    public void setValorPorSessao(String valorPorSessao) {
        this.valorPorSessao = valorPorSessao;
    }

    public String getTipoOrcamento() {
        return tipoOrcamento;
    }

    public void setTipoOrcamento(String tipoOrcamento) {
        this.tipoOrcamento = tipoOrcamento;
    }

    public int getQtdSessoes() {
        return qtdSessoes;
    }

    public void setQtdSessoes(int qtdSessoes) {
        this.qtdSessoes = qtdSessoes;
    }

    public String getNomeTatuador() {
        return nomeTatuador;
    }

    public void setNomeTatuador(String nomeTatuador) {
        this.nomeTatuador = nomeTatuador;
    }

    public String getFotoTatuador() {
        return fotoTatuador;
    }

    public void setFotoTatuador(String fotoTatuador) {
        this.fotoTatuador = fotoTatuador;
    }

    public String getValorTotalSessaoUnica() {
        return valorTotalSessaoUnica;
    }

    public void setValorTotalSessaoUnica(String valorTotalSessaoUnica) {
        this.valorTotalSessaoUnica = valorTotalSessaoUnica;
    }
}

