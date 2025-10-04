package br.com.so.services;

import br.com.so.model.ProcessoGenerico;
import java.util.List;

public interface Escalonador {
    void adicionar(ProcessoGenerico p);
    void runSimulation();
    List<ProcessoGenerico> getProcessos();
}
