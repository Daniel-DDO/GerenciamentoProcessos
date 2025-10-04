package br.com.so.services;

import br.com.so.model.ProcessoGenerico;
import br.com.so.model.ProcessoMultiplasFilas;
import br.com.so.model.ProcessoRoundRobin;
import br.com.so.model.Tipo;

import java.util.LinkedList;
import java.util.Queue;

public class ProcessoService {

    private static ProcessoService instance;
    private final Queue<ProcessoGenerico> readyQueue = new LinkedList<>();

    private ProcessoService() {}

    public static synchronized ProcessoService getProcessoService() {
        if (instance == null) instance = new ProcessoService();
        return instance;
    }

    public static ProcessoGenerico criarProcesso(String tipoAlg, int id, String nome, int prioridade, Tipo tipo, int tempoCPU, int chegada) {
        if ("RR".equalsIgnoreCase(tipoAlg)) {
            return new ProcessoRoundRobin(id, nome, prioridade, tipo, tempoCPU, chegada);
        } else {
            return new ProcessoMultiplasFilas(id, nome, prioridade, tipo, tempoCPU, chegada);
        }
    }

    public void addProcesso(ProcessoGenerico p) {
        readyQueue.add(p);
    }

    public ProcessoGenerico pollProcesso() {
        return readyQueue.poll();
    }

    public boolean hasProcessos() {
        return !readyQueue.isEmpty();
    }

    public void printReadyQueue() {
        if (readyQueue.isEmpty()) {
            System.out.println("[Fila de prontos vazia]");
            return;
        }
        System.out.println("Fila de prontos:");
        for (ProcessoGenerico p : readyQueue) {
            System.out.println("ID=" + p.getId()
                    + " | Nome=" + p.getNome()
                    + " | Prioridade=" + p.getPrioridade()
                    + " | Tipo=" + p.getTipo()
                    + " | TempoRestante=" + p.getTempoRestante());
        }
    }

    public Queue<ProcessoGenerico> getReadyQueue() {
        return readyQueue;
    }

    public void clearQueue() {
        readyQueue.clear();
    }
}