package br.com.so;

import br.com.so.model.*;
import br.com.so.services.*;
import br.com.so.*;
import br.com.so.concurrency.ConcurrencyDemo;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Simulador de SO - Round Robin e Multiplas Filas ===");
        while (true) {
            System.out.println();
            System.out.println("Escolha uma opção:");
            System.out.println("1 - Executar exemplo: 5 threads disputando 2 recursos (mostra corrida e exclusão mútua)");
            System.out.println("2 - Iniciar simulador de escalonamento (linha de comando)");
            System.out.println("0 - Sair");
            System.out.print("> ");
            int opt = Integer.parseInt(sc.nextLine());
            if (opt == 0) break;
            if (opt == 1) {
                System.out.println("Executando demonstração de recursos compartilhados...");
                RecursosCompartilhados.demonstrar();
            } else if (opt == 2) {
                SistemaCLI cli = new SistemaCLI(sc);
                cli.run();
            }
        }
        System.out.println("Fim.");
        sc.close();
    }
}





/*
import br.com.so.model.EnumStatus;
import br.com.so.model.EnumTipo;
import br.com.so.services.ProcessoService;
import java.util.Scanner;
import br.com.so.model.*;
import br.com.so.services.*;
import java.util.Scanner;

/*
public class Main {
    public static void main(String[] args) {

        System.out.println("---------------SIMULADOR DE PROCESSOS--------------");
        System.out.println();
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        boolean criandoProcessos = true;
        while (criandoProcessos) {
            System.out.println("\n─── CRIAR PROCESSO ───");

            System.out.print("ID: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Nome: ");
            String nome = scanner.nextLine();

            System.out.print("Prioridade (0-5, onde 5 é maior): ");
            int prioridade = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Tempo de CPU necessário (ms): ");
            int tempo = scanner.nextInt();

            int tipo;
            EnumTipo tipoProcesso = null;
            do {
                scanner.nextLine();
                System.out.print("Tipo (CPU/IO) — (0 - CPU Bound | 1 - I/O Bound): ");
                tipo = scanner.nextInt();
                if (tipo == 0) {
                    tipoProcesso = EnumTipo.cpubound;
                } else if (tipo == 1) {
                    tipoProcesso = EnumTipo.iobound;
                } else {
                    System.err.println("Erro! Digite qual será o tipo do processo. 0 para CPU bound e 1 para I/O bound.\nTente novamente.\n");
                }
            } while (tipo < 0 || tipo > 1);

            System.out.print("Tempo de chegada (ms) [0 para imediato]: ");
            int chegada = scanner.nextInt();
            scanner.nextLine();

            //rascunho daniel
            int tempoCpu = 0;
            EnumStatus status = EnumStatus.criado;
            ProcessoService.getProcessoService().criarProcessoRoundRobin(id, nome, tempoCpu, status, tipoProcesso);

            ProcessoService.getProcessoService().listarProcessos();

        }

     }
}


public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static ProcessoService processoService = new ProcessoService();
    private static int algoritmoEscolhido = 0;

    public static void main(String[] args) {
        System.out.println("SIMULADOR DE ESCALONAMENTO DE PROCESSOS\n");

        escolherAlgoritmo();
        configurarQuantum();

        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            int opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    criarNovoProcesso();
                    break;
                case 2:
                    processoService.mostrarFilaProntos();
                    break;
                case 3:
                    executarEscalonamento();
                    break;
                case 4:
                    processoService.mostrarEstatisticas();
                    break;
                case 5:
                    configurarQuantum();
                    break;
                case 6:
                    reiniciarSimulacao();
                    break;
                case 0:
                    continuar = false;
                    System.out.println("\nEncerrando simulador...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }

        scanner.close();
    }

    private static void escolherAlgoritmo() {
        System.out.println("ESCOLHA O ALGORITMO DE ESCALONAMENTO:");
        System.out.println("1 - Round Robin");
        System.out.println("2 - Múltiplas Filas com Feedback");

        while (algoritmoEscolhido != 1 && algoritmoEscolhido != 2) {
            algoritmoEscolhido = lerInteiro("Digite sua escolha (1 ou 2): ");
            if (algoritmoEscolhido != 1 && algoritmoEscolhido != 2) {
                System.out.println("Opção inválida! Digite 1 ou 2.");
            }
        }

        String nomeAlgoritmo = algoritmoEscolhido == 1 ? "Round Robin" : "Múltiplas Filas com Feedback";
        System.out.println("Algoritmo selecionado: "+nomeAlgoritmo+"\n");
    }

    private static void mostrarMenu() {
        System.out.println("\nMENU PRINCIPAL\n");
        System.out.println("1 - Criar novo processo");
        System.out.println("2 - Mostrar fila de prontos");
        System.out.println("3 - Iniciar escalonamento");
        System.out.println("4 - Mostrar estatísticas");
        System.out.println("5 - Configurar quantum");
        System.out.println("6 - Reiniciar simulação");
        System.out.println("0 - Sair\n");

    }

    private static void criarNovoProcesso() {
        System.out.println("\nCRIAR NOVO PROCESSO");
        System.out.println("───────────────────────────────────────────────────────────");

        String nome = lerString("Nome do processo: ");

        System.out.println("\nTipo do processo:");
        System.out.println("1 - CPU Bound");
        System.out.println("2 - I/O Bound");
        int tipoOpcao = lerInteiro("Escolha: ");
        EnumTipo tipo = (tipoOpcao == 1) ? EnumTipo.cpubound : EnumTipo.iobound;

        int tempoCpu = lerInteiro("\nTempo de CPU (1-100 ms): ", 1, 100);

        if (algoritmoEscolhido == 1) {
            //round robin
            processoService.criarProcessoRoundRobin(nome, tempoCpu, tipo);
        } else {
            //múltiplas Filas
            int prioridade = lerInteiro("\nPrioridade (1-10, onde 10 é maior): ", 1, 10);
            processoService.criarProcessoMultiplasFilas(nome, prioridade, tempoCpu, tipo);
        }
    }

    private static void executarEscalonamento() {
        if (processoService.getFilaProntos().isEmpty()) {
            System.out.println("\n  Não há processos na fila de prontos!");
            return;
        }

        System.out.println("\n INICIANDO ESCALONAMENTO...");
        pausar();

        if (algoritmoEscolhido == 1) {
            RoundRobinService rrService = new RoundRobinService(processoService);
            rrService.executar();
        } else {
            MultiplasFilasService mfService = new MultiplasFilasService(processoService);
            mfService.configurarQuantumPersonalizado(processoService.getQuantum());
            mfService.executar();
        }

        processoService.mostrarEstatisticas();
    }

    private static void configurarQuantum() {
        System.out.println("\n  CONFIGURAR QUANTUM");
        int quantum = lerInteiro("Digite o valor do quantum (1-50 ms): ", 1, 50);
        processoService.setQuantum(quantum);
    }

    private static void reiniciarSimulacao() {
        System.out.println("\n  Tem certeza que deseja reiniciar a simulação?");
        System.out.println("Isso removerá todos os processos e estatísticas.");
        String confirmacao = lerString("Digite 'sim' para confirmar: ");

        if (confirmacao.equalsIgnoreCase("sim")) {
            processoService.limpar();
            System.out.println(" Simulação reiniciada!");
            escolherAlgoritmo();
            configurarQuantum();
        } else {
            System.out.println(" Operação cancelada.");
        }
    }

    private static int lerInteiro(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido!");
            }
        }
    }

    private static int lerInteiro(String mensagem, int min, int max) {
        while (true) {
            int valor = lerInteiro(mensagem);
            if (valor >= min && valor <= max) {
                return valor;
            }
            System.out.printf("O valor deve estar entre %d e %d!\n", min, max);
        }
    }

    private static String lerString(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    private static void pausar() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}*/

