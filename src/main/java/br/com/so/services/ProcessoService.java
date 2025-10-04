package br.com.so.services;

import br.com.so.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import br.com.so.services.*;
import java.util.Scanner;

@Getter
@Setter
public class ProcessoService {

    private static ProcessoService processoService;

    public static ProcessoService getProcessoService() {
        if (processoService == null) {
            processoService = new ProcessoService();
        }
        return processoService;
    }

    private List<ProcessoGenerico> processo = carregarProcessos();

    private List<ProcessoGenerico> carregarProcessos() {
        if (processo == null) {
            processo = new ArrayList<>();
        }
        return processo;
    }

    public void listarProcessos() {
        for (int i = 0; i < processo.size(); i++) {
            System.out.println("\nProcesso "+i+": ");
            System.out.println(processo.get(i)+"\n");
        }
    }

    /*
    sobre a prioridade dos processos, nós definimos o seguinte:
    0 - baixa
    1 - abaixo do normal
    2 - normal
    3 - acima do normal
    4 - alta
    5 - tempo real
    ou seja, vai de 0 a 5. qualquer valor fora disso, lança um erro e pede de novo.
     */

    public void criarProcessoMultiplasFilas(Integer pid, String nome, Integer prioridade, Integer tempoCpu, EnumStatus status, EnumTipo tipo) {
        verificarPrioridade(prioridade);
        if (prioridade == -1) {
            return;
        }

        ProcessoGenerico processo = new ProcessoMultiplasFilas(pid, nome, prioridade, tempoCpu, status, tipo);

        if (verificarProcesso(pid) == -1) {
            System.out.println("Erro! Processo com PID já existente.\n");
        }

        this.processo.add(processo);
    }

    public void criarProcessoRoundRobin(Integer pid, String nome, Integer tempoCpu, EnumStatus status, EnumTipo tipo) {
        ProcessoGenerico processo = new ProcessoRoundRobin(pid, nome, tempoCpu, status, tipo);
        if (verificarProcesso(pid) == -1) {
            System.err.println("\nErro! Processo com PID já existente.\n");
            return;
        }

        this.processo.add(processo);
    }

    public Integer verificarPrioridade(Integer prioridade) {
        if (prioridade < 0 || prioridade > 5) {
            System.err.println("Prioridade inválida. Digite um valor de 0 a 5, seguindo o padrão: \n"+
                    "0 - Baixa\n"+
                    "1 - Abaixo do normal\n"+
                    "2 - Normal\n"+
                    "3 - Acima do normal\n"+
                    "4 - Alta\n"+
                    "5 - Tempo real\n");
            return -1;
        }
        return prioridade;
    }

    public int verificarProcesso(Integer pid) {
        for (int i = 0; i < processo.size(); i++) {
            if (pid.equals(processo.get(i).getPid())) {
                return -1;
            }
        }
        return pid;
    }

}

/*
package br.com.so.services;

import br.com.so.model.*;
        import java.util.*;
        import java.util.concurrent.atomic.AtomicInteger;

public class ProcessoService {
    private static final AtomicInteger pidCounter = new AtomicInteger(1);
    private List<ProcessoGenerico> todosProcessos;
    private Queue<ProcessoGenerico> filaProntos;
    private int tempoAtual;
    private int quantum;

    public ProcessoService() {
        this.todosProcessos = new ArrayList<>();
        this.filaProntos = new LinkedList<>();
        this.tempoAtual = 0;
        this.quantum = 3; // valor padrão
    }

    // Cria processo Round Robin
    public ProcessoRoundRobin criarProcessoRoundRobin(String nome, Integer tempoCpu, EnumTipo tipo) {
        ProcessoRoundRobin processo = new ProcessoRoundRobin(
                pidCounter.getAndIncrement(),
                nome,
                tempoCpu,
                tipo
        );
        processo.setTempoChegada(tempoAtual);
        todosProcessos.add(processo);
        filaProntos.offer(processo);
        System.out.println("Processo criado: "+processo);
        return processo;
    }

    // Cria processo Múltiplas Filas
    public ProcessoMultiplasFilas criarProcessoMultiplasFilas(String nome, Integer prioridade, Integer tempoCpu, EnumTipo tipo) {
        ProcessoMultiplasFilas processo = new ProcessoMultiplasFilas(
                pidCounter.getAndIncrement(),
                nome,
                prioridade,
                tempoCpu,
                tipo
        );
        processo.setTempoChegada(tempoAtual);
        todosProcessos.add(processo);
        filaProntos.offer(processo);
        System.out.println("Processo criado: "+processo);
        return processo;
    }

    // Mostra fila de prontos
    public void mostrarFilaProntos() {
        System.out.println("\nFILA DE PRONTOS:\n");
        System.out.println("═══════════════════════════════════════════════════════════");
        if (filaProntos.isEmpty()) {
            System.out.println("   [Fila vazia]");
        } else {
            int posicao = 1;
            for (ProcessoGenerico p : filaProntos) {
                System.out.println("   " + posicao++ + ". " + p);
            }
        }
        System.out.println("═══════════════════════════════════════════════════════════\n");
    }

    // Calcula estatísticas
    public void mostrarEstatisticas() {
        System.out.println("\nESTATÍSTICAS FINAIS:");
        System.out.println("═══════════════════════════════════════════════════════════");

        double tempoEsperaTotal = 0;

        for (ProcessoGenerico p : todosProcessos) {
            int turnaround = p.getTurnaround();
            System.out.printf("PID %d (%s):\n", p.getPid(), p.getNome());
            System.out.printf("   • Tempo de Turnaround: %d ms\n", turnaround);
            System.out.printf("   • Tempo de Espera: %d ms\n", p.getTempoEspera());
            System.out.printf("   • Tempo de CPU: %d ms\n", p.getTempoCpu());
            tempoEsperaTotal += p.getTempoEspera();
        }

        double tempoMedioEspera = todosProcessos.isEmpty() ? 0 : tempoEsperaTotal / todosProcessos.size();
        System.out.println("───────────────────────────────────────────────────────────");
        System.out.printf("Tempo Médio de Espera: %.2f ms\n", tempoMedioEspera);
        System.out.println("═══════════════════════════════════════════════════════════\n");
    }

    // Getters e Setters
    public List<ProcessoGenerico> getTodosProcessos() {
        return todosProcessos;
    }

    public Queue<ProcessoGenerico> getFilaProntos() {
        return filaProntos;
    }

    public int getTempoAtual() {
        return tempoAtual;
    }

    public void setTempoAtual(int tempoAtual) {
        this.tempoAtual = tempoAtual;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
        System.out.println("Quantum configurado para: "+quantum+" ms");
    }

    public void limpar() {
        todosProcessos.clear();
        filaProntos.clear();
        tempoAtual = 0;
        pidCounter.set(1);
    }
}*/