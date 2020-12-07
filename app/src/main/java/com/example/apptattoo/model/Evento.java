package com.example.apptattoo.model;

import com.example.apptattoo.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Evento implements Serializable {

    private String nome, celular, valor,idEvento, fotoUsuario, fotoTatuagem, idCliente,idConsulta,
            idTatuador, nomeTatuador, corEvento, horaTermino, horaInicio;
    private Long dataInMillis;
    private int hora, minuto, duracao, ano, mes, dia;

    public Evento() {
        //gerando Id da postagem assim q o costrutor for chamado
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference eventoRef = firebaseRef.child("evento");
        String idEvento = eventoRef.push().getKey();//push - gera um id unico
        setIdEvento(idEvento);
    }

    public void salvarEvento(String idTatuador, Long dataInMillis){
        setCorEvento("Azul");
        setDataInMillis(dataInMillis);
        DatabaseReference eventoRef = ConfiguracaoFirebase.getFirebase()
                .child("evento")
                .child(idTatuador)
                .child(getIdEvento());
        eventoRef.setValue(this);
    }

    public Long getDataInMillis() {
        return dataInMillis;
    }

    public void setDataInMillis(Long dataInMillis) {
        this.dataInMillis = dataInMillis;
    }

    public String getHoraTermino() {
        return horaTermino;
    }

    public void setHoraTermino(String horaTermino) {
        this.horaTermino = horaTermino;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getNomeTatuador() {
        return nomeTatuador;
    }

    public void setNomeTatuador(String nomeTatuador) {
        this.nomeTatuador = nomeTatuador;
    }

    public String getCorEvento() {
        return corEvento;
    }

    public void setCorEvento(String corEvento) {
        this.corEvento = corEvento;
    }

    public String getIdTatuador() {
        return idTatuador;
    }

    public void setIdTatuador(String idTatuador) {
        this.idTatuador = idTatuador;
    }

    public String getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(String idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getFotoTatuagem() {
        return fotoTatuagem;
    }

    public void setFotoTatuagem(String fotoTatuagem) {
        this.fotoTatuagem = fotoTatuagem;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }
}
