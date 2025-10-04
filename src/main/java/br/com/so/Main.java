package br.com.so;

import java.util.Scanner;
import br.com.so.services.SistemaCLI;
import br.com.so.services.RecursosCompartilhados;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.err.println("Álvaro Ribeiro e Daniel Dionísio\n");
            System.out.println("=== Simulador de SO - Round Robin e Múltiplas Filas ===");

            while (true) {
                System.out.println();
                System.out.println("Escolha uma opção:");
                System.out.println("1 - Executar exemplo: 5 threads disputando 2 recursos (demonstra corrida e exclusão mútua)");
                System.out.println("2 - Iniciar simulador de escalonamento (linha de comando)");
                System.out.println("0 - Sair");
                System.out.print("> ");

                String opt = sc.nextLine().trim();
                switch (opt) {
                    case "0" -> {
                        System.out.println("Encerrando sistema...");
                        return;
                    }
                    case "1" -> {
                        System.out.println("Executando demonstração de recursos compartilhados...");
                        try {
                            RecursosCompartilhados.demonstrar();
                        } catch (Exception e) {
                            System.out.println("Erro ao executar demonstração: " + e.getMessage());
                        }
                    }
                    case "2" -> {
                        SistemaCLI cli = new SistemaCLI(sc);
                        cli.run();
                    }
                    default -> System.out.println("Opção inválida. Digite 0, 1 ou 2.");
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Reinicie o programa e tente novamente.");
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Fim do programa.");
        }
    }
}

