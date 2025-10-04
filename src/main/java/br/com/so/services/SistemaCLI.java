package br.com.so.services;

import br.com.so.model.*;
import java.util.Scanner;

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
            System.out.println("--- Menu do Simulador ---");
            System.out.println("1 - Selecionar algoritmo (RR ou MF)");
            System.out.println("2 - Criar processo");
            System.out.println("3 - Iniciar execução");
            System.out.println("0 - Voltar");
            System.out.print("> ");
            String opt = sc.nextLine();
            if (opt.equals("0")) break;
            if (opt.equals("1")) escolherAlgoritmo();
            else if (opt.equals("2")) criarProcesso();
            else if (opt.equals("3")) iniciarExecucao();
        }
    }

    private void escolherAlgoritmo() {
        System.out.print("Digite RR para Round Robin ou MF para Multiplas Filas: ");
        String a = sc.nextLine().trim().toUpperCase();
        if (a.equals("RR")) {
            System.out.print("Digite quantum desejado (int): ");
            int q = Integer.parseInt(sc.nextLine());
            escalonador = new RoundRobin(ProcessoService.getProcessoService(), q);
            System.out.println("Round Robin selecionado com quantum=" + q);
        } else if (a.equals("MF")) {
            escalonador = new MultiplasFilas(ProcessoService.getProcessoService(), 0);
            System.out.println("Multiplas Filas selecionado (3 níveis)");
        } else {
            System.out.println("Opção inválida");
        }
    }

    private void criarProcesso() {
        if (escalonador == null) {
            System.out.println("Escolha um algoritmo antes de criar processos.");
            return;
        }
        System.out.print("ID (int): ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Prioridade (0-2) - 0 = alta: ");
        int pr = Integer.parseInt(sc.nextLine());
        System.out.print("Tipo (1- CPU_BOUND, 2- IO_BOUND): ");
        int tp = Integer.parseInt(sc.nextLine());
        Tipo tipo = tp == 2 ? Tipo.IO_BOUND : Tipo.CPU_BOUND;
        System.out.print("Tempo total de CPU (ticks int): ");
        int t = Integer.parseInt(sc.nextLine());
        chegadaCounter++;

        ProcessoGenerico p = ProcessoService.criarProcesso(
                escalonador instanceof RoundRobin ? "RR" : "MF",
                id, nome, pr, tipo, t, chegadaCounter
        );

        escalonador.adicionar(p);
    }

    private void iniciarExecucao() {
        if (escalonador == null) {
            System.out.println("Selecione algoritmo primeiro");
            return;
        }
        escalonador.runSimulation();
    }
}
