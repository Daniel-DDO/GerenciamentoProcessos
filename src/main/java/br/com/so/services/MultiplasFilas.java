package br.com.so.services;

import br.com.so.model.ProcessoRoundRobin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class MultiplasFilas {
    private List<Queue<ProcessoRoundRobin>> filas;
    private int[] quantums; // Quantum diferente para cada fila
    private String[] nomesFilas;

    public MultiplasFilas() {
        // Cria 3 filas com prioridades diferentes
        filas = new ArrayList<>();
        filas.add(new LinkedList<>()); // Fila 0: Alta prioridade
        filas.add(new LinkedList<>()); // Fila 1: Média prioridade
        filas.add(new LinkedList<>()); // Fila 2: Baixa prioridade

        // Define quantum para cada fila (aumenta para filas de menor prioridade)
        quantums = new int[]{2, 4, 6};

        nomesFilas = new String[]{"ALTA (P5)", "MÉDIA (P3-4)", "BAIXA (P1-2)"};
    }

    public void adicionarProcesso(ProcessoRoundRobin p) {
        int nivelFila = p.getNivelFila();
        filas.get(nivelFila).add(p);
    }

    public void executar() {
        int tempoAtual = 0;
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("   ESCALONAMENTO MÚLTIPLAS FILAS");
        System.out.println("   (3 níveis de prioridade)");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Fila 0 (ALTA):  quantum = " + quantums[0] + "ms");
        System.out.println("Fila 1 (MÉDIA): quantum = " + quantums[1] + "ms");
        System.out.println("Fila 2 (BAIXA): quantum = " + quantums[2] + "ms");
        System.out.println("═══════════════════════════════════════\n");

        // Guarda todos os processos para estatísticas
        List<ProcessoRoundRobin> todosProcessos = new ArrayList<>();
        for (Queue<ProcessoRoundRobin> fila : filas) {
            todosProcessos.addAll(fila);
        }

        while (temHaProcessos()) {
            boolean executou = false;

            // Procura da fila de maior prioridade para menor
            for (int i = 0; i < filas.size(); i++) {
                Queue<ProcessoRoundRobin> filaAtual = filas.get(i);

                if (!filaAtual.isEmpty()) {
                    ProcessoRoundRobin processoAtual = filaAtual.poll();
                    int quantum = quantums[i];

                    System.out.println("┌─────────────────────────────────────┐");
                    System.out.printf("│ Tempo: %dms - %s EXECUTANDO%n", tempoAtual, processoAtual);
                    System.out.printf("│ Fila: %d (%s) | Quantum: %dms%n", i, nomesFilas[i], quantum);
                    System.out.printf("│ Tempo restante: %dms%n", processoAtual.getTempoRestante());
                    mostrarEstadoFilas(processoAtual);
                    System.out.println("└─────────────────────────────────────┘");

                    // Executa por quantum ou até terminar
                    int tempoExecucao = Math.min(quantum, processoAtual.getTempoRestante());

                    for (int j = 0; j < tempoExecucao; j++) {
                        processoAtual.executar(tempoAtual);
                        tempoAtual++;

                        // Incrementa tempo de espera de TODOS os outros processos
                        incrementarEsperaTodos(processoAtual);
                    }

                    if (processoAtual.terminado()) {
                        processoAtual.setTempoTermino(tempoAtual);
                        System.out.println("  ✓ " + processoAtual + " FINALIZADO em " + tempoAtual + "ms\n");
                    } else {
                        // Processo não terminou - pode descer de fila (feedback)
                        int novaFila = Math.min(i + 1, filas.size() - 1); // Desce 1 nível

                        if (novaFila != i) {
                            processoAtual.setNivelFila(novaFila);
                            System.out.printf("  ⬇ %s MOVIDO para Fila %d (%s) (resta %dms)%n%n",
                                    processoAtual, novaFila, nomesFilas[novaFila],
                                    processoAtual.getTempoRestante());
                        } else {
                            System.out.printf("  ⏸ %s PREEMPTADO na Fila %d (resta %dms)%n%n",
                                    processoAtual, i, processoAtual.getTempoRestante());
                        }

                        filas.get(novaFila).add(processoAtual);
                    }

                    executou = true;
                    break; // Volta a verificar da fila de maior prioridade
                }
            }

            if (!executou) {
                System.out.println("CPU ociosa em " + tempoAtual + "ms");
                tempoAtual++;
            }
        }

        exibirEstatisticas(todosProcessos, tempoAtual);
    }

    private boolean temHaProcessos() {
        for (Queue<ProcessoRoundRobin> fila : filas) {
            if (!fila.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void mostrarEstadoFilas(ProcessoRoundRobin atual) {
        System.out.println("│ Estado das filas:");
        for (int i = 0; i < filas.size(); i++) {
            List<ProcessoRoundRobin> processosFila = new ArrayList<>(filas.get(i));
            System.out.printf("│   Fila %d (%s): %s%n", i, nomesFilas[i],
                    processosFila.isEmpty() ? "vazia" : processosFila);
        }
    }

    private void incrementarEsperaTodos(ProcessoRoundRobin atual) {
        for (Queue<ProcessoRoundRobin> fila : filas) {
            for (ProcessoRoundRobin p : fila) {
                if (p != atual) {
                    p.incrementarEspera();
                }
            }
        }
    }

    private void exibirEstatisticas(List<ProcessoRoundRobin> processos, int tempoTotal) {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("   ESTATÍSTICAS FINAIS");
        System.out.println("═══════════════════════════════════════\n");

        int somaEspera = 0;
        int somaTurnaround = 0;

        System.out.println("Processo | Prior | Turnaround | Espera | Início | Término");
        System.out.println("---------|-------|------------|--------|--------|--------");

        for (ProcessoRoundRobin p : processos) {
            int turnaround = p.getTurnaround();
            int espera = p.getTempoEspera();

            System.out.printf("  %-7s|   %d   |   %4dms   | %4dms | %4dms | %5dms%n",
                    p, p.getPrioridade(), turnaround, espera,
                    p.getTempoInicio(), p.getTempoTermino());

            somaEspera += espera;
            somaTurnaround += turnaround;
        }

        System.out.println("\n─────────────────────────────────────");
        System.out.printf("Tempo médio de espera: %.2fms%n", (double) somaEspera / processos.size());
        System.out.printf("Turnaround médio: %.2fms%n", (double) somaTurnaround / processos.size());
        System.out.printf("Tempo total de execução: %dms%n", tempoTotal);
        System.out.println("═══════════════════════════════════════\n");
    }
}