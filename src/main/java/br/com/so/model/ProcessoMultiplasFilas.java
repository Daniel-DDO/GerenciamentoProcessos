package br.com.so.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessoMultiplasFilas extends ProcessoGenerico {
    private int filaAtual = 0;

    public ProcessoMultiplasFilas(int id, String nome, int prioridade, Tipo tipo, int tempoTotalCPU, int chegada) {
        super(id, nome, prioridade, tipo, tempoTotalCPU, chegada);
    }

    public ProcessoMultiplasFilas() {
        super();
    }

    public int getTurnaround() {
        if (tempoFinalizacao == -1) return -1;
        return tempoFinalizacao - tempoChegada;
    }

    public int getTempoEsperaAcumulado() {
        return tempoEspera;
    }
}
