package br.com.so.services;

import br.com.so.model.EnumStatus;
import br.com.so.model.ProcessoRoundRobin;

import java.util.LinkedList;
import java.util.Queue;

public class RoundRobin {

    public void executar(int quantum) {
        ProcessoService service = ProcessoService.getProcessoService();
        Queue<ProcessoRoundRobin> fila = new LinkedList<>(service.getProcesso());

        System.out.println("\n--- Iniciando Round Robin ---\n");

        while (!fila.isEmpty()) {
            ProcessoRoundRobin processo = fila.poll(); // retira o primeiro da fila
            System.out.println("Executando processo: " + processo.getNome());

            processo.setStatus(EnumStatus.executando);

            // Verifica se o processo finaliza dentro do quantum
            if (processo.getTempoCpu() <= quantum) {
                System.out.println("Processo " + processo.getNome() + " terminou.");
                processo.setTempoCpu(0);
                processo.setStatus(EnumStatus.encerrado);
            } else {
                // Se não terminou, diminui o tempo e volta pra fila
                processo.setTempoCpu(processo.getTempoCpu() - quantum);
                processo.setStatus(EnumStatus.pronto);
                System.out.println("Processo " + processo.getNome() + " não terminou, tempo restante: " + processo.getTempoCpu());
                fila.add(processo); // retorna para o final da fila
            }

            System.out.println();
        }

        System.out.println("\n--- Todos os processos foram finalizados ---");
    }
}
