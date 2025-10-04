package br.com.so.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessoRoundRobin extends ProcessoGenerico {
    private int quantum;

    public ProcessoRoundRobin(int id, String nome, int prioridade, Tipo tipo, int tempoTotalCPU, int chegada) {
        super(id, nome, prioridade, tipo, tempoTotalCPU, chegada);
    }

    public ProcessoRoundRobin(int quantum) {
        super();
        this.quantum = quantum;
    }

    public int getTurnaround() {
        if (tempoFinalizacao == -1) return -1;
        return tempoFinalizacao - tempoChegada;
    }

    public int getTempoEsperaAcumulado() {
        return tempoEspera;
    }
}
