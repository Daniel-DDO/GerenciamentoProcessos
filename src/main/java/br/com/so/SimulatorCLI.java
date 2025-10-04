package br.com.so;

import br.com.so.model.ProcessoGenerico;
import br.com.so.model.Tipo;
import br.com.so.services.MultiplasFilas;
import br.com.so.services.ProcessoService;
import br.com.so.services.RoundRobin;

import java.util.Scanner;

public class SimulatorCLI {

    public static void run(Scanner sc) throws InterruptedException {
        System.out.println("\n--- Simulador de Escalonamento (CLI) ---");
        System.out.println("Escolha o algoritmo: 1) Round Robin  2) Múltiplas Filas");
        System.out.print("Opção: ");
        String algoritmo = sc.nextLine().trim();

        ProcessoService service = ProcessoService.getProcessoService();

        System.out.print("Defina o quantum global (inteiro > 0, ex.: 3): ");
        int quantum = Integer.parseInt(sc.nextLine().trim());

        RoundRobin rr = new RoundRobin(service, quantum);
        MultiplasFilas mf = new MultiplasFilas(service, quantum);

        System.out.println("Crie processos. Digite 'start' para iniciar o escalonamento. Digite 'done' para encerrar e voltar ao menu.");

        while (true) {
            System.out.print("Comando (new/list/start/done): ");
            String cmd = sc.nextLine().trim();

            if (cmd.equalsIgnoreCase("new")) {
                System.out.print("ID (int): ");
                int id = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Nome: ");
                String nome = sc.nextLine().trim();
                System.out.print("Prioridade (inteiro, maior valor = maior prioridade): ");
                int prioridade = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Tipo (1 = CPU-bound / 2 = IO-bound): ");
                int t = Integer.parseInt(sc.nextLine().trim());
                Tipo tipo = (t == 2) ? Tipo.IO_BOUND : Tipo.CPU_BOUND;
                System.out.print("Tempo total de CPU (unidades inteiras): ");
                int total = Integer.parseInt(sc.nextLine().trim());

                ProcessoGenerico processo = service.criarProcesso(
                        algoritmo.equals("1") ? "RR" : "MF",
                        id, nome, prioridade, tipo, total, 0
                );

                service.addProcesso(processo);
                System.out.println("Processo criado e adicionado à fila de prontos: " + processo);

            } else if (cmd.equalsIgnoreCase("list")) {
                service.printReadyQueue();

            } else if (cmd.equalsIgnoreCase("start")) {
                if (algoritmo.equals("1")) {
                    rr.setQuantum(quantum);
                    rr.runSimulation();
                } else {
                    mf.setQuantum(quantum);
                    mf.runSimulation();
                }

            } else if (cmd.equalsIgnoreCase("done")) {
                break;

            } else {
                System.out.println("Comando desconhecido.");
            }
        }
    }
}


/*
public class SimulatorCLI {

    static void run(Scanner sc) throws InterruptedException {
        System.out.println("\n--- Simulador de Escalonamento (CLI) ---");
        System.out.println("Escolha o algoritmo: 1) Round Robin  2) Múltiplas Filas");
        System.out.print("Opção: ");
        String algoritmo = sc.nextLine().trim();

        // Usa Singleton
        ProcessoService service = ProcessoService.getProcessoService();

        System.out.print("Defina o quantum global (inteiro > 0, ex.: 3): ");
        int quantum = Integer.parseInt(sc.nextLine().trim());

        ProcessoRoundRobin rr = new ProcessoRoundRobin(service, quantum);
        ProcessoMultiplasFilas mf = new ProcessoMultiplasFilas(service, quantum);

        System.out.println("Crie processos. Digite 'start' para iniciar o escalonamento. Digite 'done' para encerrar e voltar ao menu.");

        while (true) {
            System.out.print("Comando (new/list/start/done): ");
            String cmd = sc.nextLine().trim();

            if (cmd.equalsIgnoreCase("new")) {
                System.out.print("ID (string): ");
                String id = sc.nextLine().trim();
                System.out.print("Nome: ");
                String nome = sc.nextLine().trim();
                System.out.print("Prioridade (inteiro, maior valor = maior prioridade): ");
                int prioridade = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Tipo (1 = CPU-bound / 2 = IO-bound): ");
                int t = Integer.parseInt(sc.nextLine().trim());
                Tipo tipo = (t == 2) ? Tipo.IO_BOUND : Tipo.CPU_BOUND;
                System.out.print("Tempo total de CPU (unidades inteiras): ");
                int total = Integer.parseInt(sc.nextLine().trim());

                ProcessoGenerico p1 = new ProcessoGenerico(id, nome, prioridade, tipo, total);
                service.addProcesso(p1);
                System.out.println("Processo criado e adicionado à fila de prontos: " + p1);

            } else if (cmd.equalsIgnoreCase("list")) {
                service.printReadyQueue();

            } else if (cmd.equalsIgnoreCase("start")) {
                if (algoritmo.equals("1")) {
                    rr.setQuantum(quantum);
                    rr.runSimulation();
                } else {
                    mf.setQuantum(quantum);
                    mf.runSimulation();
                }

            } else if (cmd.equalsIgnoreCase("done")) {
                break;

            } else {
                System.out.println("Comando desconhecido.");
            }
        }
    }
}


/*
public class SimulatorCLI {

    static void run(Scanner sc) throws InterruptedException {
        System.out.println("\n--- Simulador de Escalonamento (CLI) ---");
        System.out.println("Escolha o algoritmo: 1) Round Robin  2) Múltiplas Filas");
        System.out.print("Opção: ");
        String algoritmo = sc.nextLine().trim();

        ProcessoService service = new ProcessoService();
        System.out.print("Defina o quantum global (inteiro > 0, ex.: 3): ");
        int quantum = Integer.parseInt(sc.nextLine().trim());

        RoundRobin rr = new RoundRobin(service, quantum);
        services.MultiplasFilas mf = new services.MultiplasFilas(service, quantum);

        System.out.println("Crie processos. Digite 'start' para iniciar o escalonamento. Digite 'done' para encerrar e voltar ao menu.");

        while (true) {
            System.out.print("Comando (new/list/start/done): ");
            String cmd = sc.nextLine().trim();

            if (cmd.equalsIgnoreCase("new")) {
                System.out.print("ID (string): ");
                String id = sc.nextLine().trim();
                System.out.print("Nome: ");
                String nome = sc.nextLine().trim();
                System.out.print("Prioridade (inteiro, maior valor = maior prioridade): ");
                int prioridade = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Tipo (1 = CPU-bound / 2 = IO-bound): ");
                int t = Integer.parseInt(sc.nextLine().trim());
                Tipo tipo = (t == 2) ? Tipo.IO_BOUND : Tipo.CPU_BOUND;
                System.out.print("Tempo total de CPU (unidades inteiras): ");
                int total = Integer.parseInt(sc.nextLine().trim());

                ProcessoGenerico p1 = new ProcessoGenerico(id, nome, prioridade, tipo, total);
                service.addProcesso(p1);
                System.out.println("Processo criado e adicionado à fila de prontos: " + p1);

            } else if (cmd.equalsIgnoreCase("list")) {
                service.printReadyQueue();

            } else if (cmd.equalsIgnoreCase("start")) {
                if (algoritmo.equals("1")) {
                    rr.setQuantum(quantum);
                    rr.runSimulation();
                } else {
                    mf.setQuantum(quantum);
                    mf.runSimulation();
                }

            } else if (cmd.equalsIgnoreCase("done")) {
                break;

            } else {
                System.out.println("Comando desconhecido.");
            }
        }
    }
}

 */
