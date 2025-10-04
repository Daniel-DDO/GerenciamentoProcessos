package br.com.so.services;

import br.com.so.model.ProcessoGenerico;

public interface Escalonador {
    void adicionar(ProcessoGenerico p);
    void runSimulation();
}

