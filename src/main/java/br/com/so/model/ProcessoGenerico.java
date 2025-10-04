package br.com.so.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ProcessoGenerico {
    protected int id;
    protected String nome;
    protected int prioridade;
    protected Tipo tipo;
    protected int tempoTotalCPU;
    protected int tempoRestante;
    protected Status status;
    protected int tempoChegada;
    protected int tempoInicio = -1;
    protected int tempoFinalizacao = -1;
    protected int tempoEspera = 0;

    public ProcessoGenerico(int id, String nome, int prioridade, Tipo tipo, int tempoTotalCPU, int chegada) {
        this.id = id;
        this.nome = nome;
        this.prioridade = prioridade;
        this.tipo = tipo;
        this.tempoTotalCPU = tempoTotalCPU;
        this.tempoRestante = tempoTotalCPU;
        this.status = Status.PRONTO;
        this.tempoChegada = chegada;
    }

    public ProcessoGenerico() {}

    public void executarTick() {
        if (tempoRestante > 0) tempoRestante--;
    }

    public boolean terminou() {
        return tempoRestante <= 0;
    }

    public void acumularEspera(int t) {
        tempoEspera += t;
    }

    public int getTurnaround() {
        if (tempoFinalizacao == -1) return -1;
        return tempoFinalizacao - tempoChegada;
    }

    public int getTempoEsperaAcumulado() {
        return tempoEspera;
    }
}