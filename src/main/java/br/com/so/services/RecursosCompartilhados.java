package br.com.so.services;

import java.util.concurrent.locks.ReentrantLock;

public class RecursosCompartilhados {
    private static int recursoA = 0;
    private static int recursoB = 0;

    private static ReentrantLock lockA = new ReentrantLock();
    private static ReentrantLock lockB = new ReentrantLock();

    public static void demonstrar() throws InterruptedException {
        System.out.println("-> Primeiro: mostramos sem sincronização (corrida de dados) por alguns segundos");
        Thread[] semLock = new Thread[5];
        for (int i=0;i<5;i++) {
            final int id=i+1;
            semLock[i] = new Thread(() -> {
                for (int k=0;k<5;k++) {
                    int a = recursoA;
                    int b = recursoB;
                    int novaA = a + id;
                    int novaB = b + id*10;
                    try { Thread.sleep(50); } catch (InterruptedException e) {}
                    recursoA = novaA;
                    recursoB = novaB;
                    System.out.println("[SEM-LOCK] Thread " + id + " escreveu A=" + recursoA + " B=" + recursoB);
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                }
            });
            semLock[i].start();
        }
        for (Thread t: semLock) t.join();

        recursoA = 0; recursoB = 0;

        System.out.println("\n-> Agora: mostramos com exclusão mútua usando locks. Cada thread " +
                "tentará segurar ambos os recursos por 3 segundos (simulação)");

        Thread[] comLock = new Thread[5];
        for (int i=0;i<5;i++) {
            final int id=i+1;
            comLock[i] = new Thread(() -> {
                for (int k=0;k<2;k++) {
                    boolean acquired = false;
                    try {
                        lockA.lock();
                        System.out.println("[LOCK] Thread " + id + " adquiriu lockA");
                        try { Thread.sleep(20); } catch (InterruptedException e) {}
                        lockB.lock();
                        System.out.println("[LOCK] Thread " + id + " adquiriu lockB");
                        acquired = true;

                        recursoA += id;
                        recursoB += id*10;
                        System.out.println("[CRITICO] Thread " + id + " consumindo recurso A=" + recursoA + " B=" + recursoB + " por 3s");
                        try { Thread.sleep(3000); } catch (InterruptedException e) {}

                    } finally {
                        if (acquired) {
                            lockB.unlock();
                            System.out.println("[UNLOCK] Thread " + id + " liberou lockB");
                            lockA.unlock();
                            System.out.println("[UNLOCK] Thread " + id + " liberou lockA");
                        } else {
                            if (lockA.isHeldByCurrentThread()) {
                                lockA.unlock();
                                System.out.println("[UNLOCK] Thread " + id + " liberou lockA (sem adquirir B)");
                            }
                        }
                        try { Thread.sleep(200); } catch (InterruptedException e) {}
                    }
                }
            });
            comLock[i].start();
            Thread.sleep(100);
        }
        for (Thread t: comLock) t.join();

        System.out.println("Demonstração concluída.");
    }
}