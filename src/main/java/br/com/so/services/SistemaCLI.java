package br.com.so.services;

import br.com.so.model.*;
import java.util.*;

public class SistemaCLI {
    private final Scanner sc;
    private Escalonador escalonador = null;
    private int chegadaCounter = 0;

    public SistemaCLI(Scanner sc) {
        this.sc = sc;
    }

    public void run() {
        while (true) {
            System.out.println();
            System.out.println("===== MENU DO SIMULADOR =====");
            System.out.println("1 - Selecionar algoritmo (RR ou MF)");
            System.out.println("2 - Criar processo");
            System.out.println("3 - Listar processos criados");
            System.out.println("4 - Iniciar execução");
            System.out.println("0 - Voltar ao menu principal");
            System.out.print("> ");
            String opt = sc.nextLine().trim();

            switch (opt) {
                case "0" -> {
                    System.out.println("Retornando ao menu principal...");
                    return;
                }
                case "1" -> escolherAlgoritmo();
                case "2" -> criarProcesso();
                case "3" -> listarProcessos();
                case "4" -> iniciarExecucao();
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private void escolherAlgoritmo() {
        System.out.print("Digite RR para Round Robin ou MF para Múltiplas Filas: ");
        String a = sc.nextLine().trim().toUpperCase();

        switch (a) {
            case "RR" -> {
                System.out.print("Digite o quantum desejado (int): ");
                try {
                    int q = Integer.parseInt(sc.nextLine());
                    escalonador = new RoundRobin(ProcessoService.getProcessoService(), q);
                    System.out.println("Round Robin selecionado com quantum = " + q);
                } catch (NumberFormatException e) {
                    System.out.println("Valor inválido para quantum.");
                }
            }
            case "MF" -> {
                escalonador = new MultiplasFilas(ProcessoService.getProcessoService(), 0);
                System.out.println("Múltiplas Filas selecionado (3 níveis de prioridade).");
            }
            default -> System.out.println("Opção inválida. Escolha RR ou MF.");
        }
    }

    private void criarProcesso() {
        if (escalonador == null) {
            System.out.println("Escolha um algoritmo antes de criar processos.");
            return;
        }

        try {
            boolean idValido;
            int id;
            do {
                System.out.print("ID (int): ");
                id = Integer.parseInt(sc.nextLine());
                idValido = true;
                for (ProcessoGenerico p : escalonador.getProcessos()) {
                    if (p.getId() == id) {
                        System.out.println("ID já existe! Digite outro.");
                        idValido = false;
                        break;
                    }
                }
            } while (!idValido);

            System.out.print("Nome: ");
            String nome = sc.nextLine().trim();

            System.out.print("Prioridade (0-2) - 0 = alta: ");
            int pr = Integer.parseInt(sc.nextLine());

            System.out.print("Tipo (1-CPU_BOUND, 2-IO_BOUND): ");
            int tp = Integer.parseInt(sc.nextLine());
            Tipo tipo = (tp == 2) ? Tipo.IO_BOUND : Tipo.CPU_BOUND;

            System.out.print("Tempo total de CPU (em ms): ");
            int t = Integer.parseInt(sc.nextLine());

            chegadaCounter++;

            ProcessoGenerico p = ProcessoService.criarProcesso(
                    (escalonador instanceof RoundRobin) ? "RR" : "MF",
                    id, nome, pr, tipo, t, chegadaCounter
            );

            escalonador.adicionar(p);
            System.out.println("Processo criado e adicionado à fila de prontos!");

        } catch (NumberFormatException e) {
            System.out.println("Erro: valor numérico inválido. Processo não criado.");
        } catch (Exception e) {
            System.out.println("Erro inesperado ao criar processo: " + e.getMessage());
        }
    }


    private void listarProcessos() {
        if (escalonador == null) {
            System.out.println("Nenhum algoritmo selecionado.");
            return;
        }

        List<ProcessoGenerico> lista = escalonador.getProcessos();
        if (lista == null || lista.isEmpty()) {
            System.out.println("Nenhum processo criado ainda.");
            return;
        }

        System.out.println("\n--- LISTA DE PROCESSOS ---");
        System.out.printf("%-5s %-10s %-10s %-12s %-12s %-10s%n",
                "ID", "Nome", "Prioridade", "Tipo", "Chegada", "TempoCPU");
        for (ProcessoGenerico p : lista) {
            System.out.printf("%-5d %-10s %-10d %-12s %-12d %-10d%n",
                    p.getId(), p.getNome(), p.getPrioridade(), p.getTipo(),
                    p.getTempoChegada(), p.getTempoTotalCPU());
        }
    }

    private void iniciarExecucao() {
        if (escalonador == null) {
            System.out.println("Selecione um algoritmo primeiro.");
            return;
        }

        List<ProcessoGenerico> processos = escalonador.getProcessos();
        if (processos == null || processos.isEmpty()) {
            System.out.println("Nenhum processo criado!");
            return;
        }

        System.out.println("\n=== Iniciando Simulação ===");
        escalonador.runSimulation();
        mostrarResultadosFinais();
    }

    private void mostrarResultadosFinais() {
        List<ProcessoGenerico> lista = escalonador.getProcessos();
        if (lista == null || lista.isEmpty()) return;

        System.out.println("\n===== RESULTADOS FINAIS =====");
        System.out.printf("%-5s %-10s %-12s %-15s %-15s%n",
                "ID", "Nome", "Turnaround", "Tempo Espera", "Tempo Finalização");

        lista.sort(Comparator.comparingInt(ProcessoGenerico::getTempoFinalizacao));

        int somaEspera = 0;
        for (ProcessoGenerico p : lista) {
            somaEspera += p.getTempoEsperaAcumulado();
            System.out.printf("%-5d %-10s %-12d %-15d %-15d%n",
                    p.getId(), p.getNome(), p.getTurnaround(),
                    p.getTempoEsperaAcumulado(), p.getTempoFinalizacao());
        }

        double mediaEspera = (double) somaEspera / lista.size();
        System.out.println("\nTempo médio de espera: " + String.format("%.2f", mediaEspera));
        System.out.println("Simulação finalizada com sucesso!");
    }
}
