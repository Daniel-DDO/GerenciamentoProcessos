package br.com.so.services;

import br.com.so.model.ProcessoGenerico;
import br.com.so.model.ProcessoMultiplasFilas;
import br.com.so.model.Status;
import br.com.so.services.ProcessoService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MultiplasFilas implements Escalonador {
    private final ProcessoService service;
    private final List<Queue<ProcessoMultiplasFilas>> filas = new ArrayList<>();
    private final int[] quantums = new int[]{2, 4, 8};
    private final List<ProcessoMultiplasFilas> finalizados = new ArrayList<>();
    private int tickAtual = 0;
    private int quantum;

    public MultiplasFilas(ProcessoService service, int quantum) {
        this.service = service;
        this.quantum = quantum;
        for (int i = 0; i < 3; i++) filas.add(new LinkedList<>());
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public void adicionar(ProcessoGenerico p) {

    }

    public void runSimulation() {
        System.out.println(">>> Iniciando escalonamento Múltiplas Filas (3 níveis)");
        while (filas.stream().anyMatch(q -> !q.isEmpty())) {
            ProcessoMultiplasFilas p = null;
            int nivel = -1;
            for (int i = 0; i < filas.size(); i++) {
                if (!filas.get(i).isEmpty()) {
                    nivel = i;
                    p = filas.get(i).poll();
                    break;
                }
            }
            if (p == null) break;
            p.setStatus(Status.EXECUTANDO);
            if (p.getTempoInicio() == -1) p.setTempoInicio(tickAtual);
            int quantum = quantums[nivel];
            int tempoExecutar = Math.min(quantum, p.getTempoRestante());
            System.out.println("[MF|tick=" + tickAtual + "] Executando " + p.getNome() +
                    " (fila=" + nivel + ") por " + tempoExecutar +
                    " ticks (restante=" + p.getTempoRestante() + ")");
            for (int i = 0; i < tempoExecutar; i++) {
                p.executarTick();
                tickAtual++;
                for (int j = 0; j < filas.size(); j++) {
                    for (ProcessoMultiplasFilas q : filas.get(j)) {
                        q.acumularEspera(1);
                    }
                }
            }
            if (p.terminou()) {
                p.setStatus(Status.FINALIZADO);
                p.setTempoFinalizacao(tickAtual);
                finalizados.add(p);
                System.out.println("[MF] Processo " + p.getNome() + " finalizou no tick " + tickAtual);
            } else {
                int novaFila = Math.min(2, nivel + 1);
                p.setFilaAtual(novaFila);
                p.setStatus(Status.PRONTO);
                filas.get(novaFila).add(p);
                System.out.println("[MF] Processo " + p.getNome() +
                        " foi preemptado e movido para fila " + novaFila +
                        " (restante=" + p.getTempoRestante() + ")");
            }
        }
        System.out.println("\n--- Relatório Múltiplas Filas ---");
        int somaEspera = 0;
        for (ProcessoMultiplasFilas p : finalizados) {
            System.out.println("Processo " + p.getNome() +
                    ": turnaround=" + p.getTurnaround() +
                    ", espera=" + p.getTempoEsperaAcumulado());
            somaEspera += p.getTempoEsperaAcumulado();
        }
        double mediaEspera = finalizados.isEmpty() ? 0 : (double) somaEspera / finalizados.size();
        System.out.println("Tempo médio de espera: " + mediaEspera);
    }
}