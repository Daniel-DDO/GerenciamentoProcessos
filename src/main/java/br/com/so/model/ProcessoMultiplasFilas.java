package br.com.so.model;

public class ProcessoMultiplasFilas extends ProcessoGenerico {

    public ProcessoMultiplasFilas(Integer pid, String nome, Integer prioridade, Integer tempoCpu, EnumStatus status, EnumTipo tipo) {
        super (pid, nome, prioridade, tempoCpu, status, tipo);
    }
}
