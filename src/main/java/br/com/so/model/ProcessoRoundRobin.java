package br.com.so.model;

public class ProcessoRoundRobin extends ProcessoGenerico {


    private int nivelFila;
    private int tempoRestante;
    private int tempoEspera;
    private int tempoInicio;
    private int tempoTermino;

    public ProcessoRoundRobin(Integer pid, String nome, Integer tempoCpu, EnumStatus status, EnumTipo tipo) {
        super (pid, nome, tempoCpu, status, tipo);
    }
}
