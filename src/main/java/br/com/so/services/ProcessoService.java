package br.com.so.services;

import br.com.so.model.Processo;

import java.util.ArrayList;
import java.util.List;

public class ProcessoService {

    private static ProcessoService processoService;

    public static ProcessoService getProcessoService() {
        if (processoService == null) {
            processoService = new ProcessoService();
        }
        return processoService;
    }

    private List<Processo> processos = carregarProcessos();

    private List<Processo> carregarProcessos() {
        if (processos == null) {
            processos = new ArrayList<>();
        }
        return processos;
    }

    public void criarProcesso(Integer pid, String nameProcess) {
        Processo processo = new Processo();
        processo.setPid(pid);
        processo.setNameProcess(nameProcess);

        processos.add(processo);
    }
}
