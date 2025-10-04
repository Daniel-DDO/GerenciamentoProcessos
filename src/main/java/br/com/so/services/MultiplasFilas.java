package br.com.so.services;

import br.com.so.model.ProcessoGenerico;
import br.com.so.model.ProcessoMultiplasFilas;
import br.com.so.model.Status;
import lombok.Setter;

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
    @Setter
    private int quantum;

    public MultiplasFilas(ProcessoService service, int quantum) {
        this.service = service;
        this.quantum = quantum;
        for (int i = 0; i < 3; i++) filas.add(new LinkedList<>());
    }

    @Override
    public void adicionar(ProcessoGenerico p) {
        ProcessoMultiplasFilas pm;
        if (p instanceof ProcessoMultiplasFilas) {
            pm = (ProcessoMultiplasFilas) p;
        } else {
            pm = new ProcessoMultiplasFilas(p.getId(), p.getNome(), p.getPrioridade(), p.getTipo(),
                    p.getTempoTotalCPU(), p.getTempoChegada());
            pm.setTempoRestante(p.getTempoRestante());
            pm.setTempoInicio(p.getTempoInicio());
            pm.setTempoFinalizacao(p.getTempoFinalizacao());
            pm.setStatus(p.getStatus());
        }
        pm.setFilaAtual(0);
        pm.setStatus(Status.PRONTO);
        filas.get(0).add(pm);
        System.out.println("[MF] Adicionado processo: ID=" + pm.getId() + " Nome=" + pm.getNome() + " -> fila=0");
    }

    private void drainServiceQueue() {
        java.util.Queue<ProcessoGenerico> q = service.getReadyQueue();
        while (!q.isEmpty()) {
            ProcessoGenerico p = q.poll();
            if (p != null) adicionar(p);
        }
    }

    public void runSimulation() {
        //carrega processos criados
        drainServiceQueue();

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

    @Override
    public List<ProcessoGenerico> getProcessos() {
        //reúne proc. de todas as filas na ordem das filas, depois os finalizados
        List<ProcessoGenerico> all = new ArrayList<>();
        for (Queue<ProcessoMultiplasFilas> q : filas) {
            if (q != null && !q.isEmpty()) {
                all.addAll(q);
            }
        }
        all.addAll(finalizados);
        return all;
    }

}
