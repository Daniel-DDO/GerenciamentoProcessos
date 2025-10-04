package br.com.so.services;

import br.com.so.model.ProcessoGenerico;
import br.com.so.model.ProcessoRoundRobin;
import br.com.so.model.Status;

import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RoundRobin implements Escalonador {
    private final ProcessoService service;
    private final Queue<ProcessoRoundRobin> filaProntos = new LinkedList<>();
    private final List<ProcessoRoundRobin> finalizados = new ArrayList<>();
    @Setter
    private int quantum;
    private int tickAtual = 0;

    public RoundRobin(ProcessoService service, int quantum) {
        this.service = service;
        this.quantum = quantum;
    }

    @Override
    public void adicionar(ProcessoGenerico p) {
        ProcessoRoundRobin pr;
        if (p instanceof ProcessoRoundRobin) {
            pr = (ProcessoRoundRobin) p;
        } else {
            //converte para ProcessoRoundRobin preservando campos importantes
            pr = new ProcessoRoundRobin(p.getId(), p.getNome(), p.getPrioridade(), p.getTipo(),
                    p.getTempoTotalCPU(), p.getTempoChegada());
            pr.setTempoRestante(p.getTempoRestante());
            pr.setTempoInicio(p.getTempoInicio());
            pr.setTempoFinalizacao(p.getTempoFinalizacao());
            pr.setStatus(p.getStatus());
        }
        pr.setStatus(Status.PRONTO);
        filaProntos.add(pr);
        System.out.println("[RR] Adicionado processo à fila: ID=" + pr.getId() + " Nome=" + pr.getNome()
                + " TempoRestante=" + pr.getTempoRestante());
    }

    //antes de iniciar, carregar processos do ProcessoService
    private void drainServiceQueue() {
        Queue<ProcessoGenerico> q = service.getReadyQueue();
        while (!q.isEmpty()) {
            ProcessoGenerico p = q.poll();
            if (p != null) adicionar(p);
        }
    }

    public void runSimulation() {
        //traz processos criados via ProcessoService.addProcesso(...)
        drainServiceQueue();

        System.out.println(">>> Iniciando escalonamento Round Robin (quantum=" + quantum + ")");
        while (!filaProntos.isEmpty()) {
            ProcessoRoundRobin p = filaProntos.poll();
            if (p.terminou()) {
                p.setStatus(Status.FINALIZADO);
                p.setTempoFinalizacao(tickAtual);
                finalizados.add(p);
                continue;
            }
            p.setStatus(Status.EXECUTANDO);
            if (p.getTempoInicio() == -1) p.setTempoInicio(tickAtual);
            int tempoExecutar = Math.min(quantum, p.getTempoRestante());
            System.out.println("[RR|tick=" + tickAtual + "] Executando " + p.getNome() +
                    " por " + tempoExecutar + " ticks (restante=" + p.getTempoRestante() + ")");
            for (int i = 0; i < tempoExecutar; i++) {
                p.executarTick();
                tickAtual++;
                for (ProcessoRoundRobin outro : filaProntos) {
                    outro.acumularEspera(1);
                }
            }
            if (p.terminou()) {
                p.setStatus(Status.FINALIZADO);
                p.setTempoFinalizacao(tickAtual);
                finalizados.add(p);
                System.out.println("[RR] Processo " + p.getNome() + " finalizou no tick " + tickAtual);
            } else {
                p.setStatus(Status.PRONTO);
                filaProntos.add(p);
                System.out.println("[RR] Processo " + p.getNome() + " foi preemptado no tick " +
                        tickAtual + " (restante=" + p.getTempoRestante() + ")");
            }
        }
        System.out.println("\n--- Relatório Round Robin ---");
        int somaEspera = 0;
        for (ProcessoRoundRobin p : finalizados) {
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
        //retorna uma cópia com os processos na fila de prontos (em ordem) + os finalizados
        List<ProcessoGenerico> all = new ArrayList<>();
        //filaProntos é Queue<ProcessoRoundRobin> -> elementos são Processos (subclasse)
        all.addAll(filaProntos);
        all.addAll(finalizados);
        return all;
    }

}
