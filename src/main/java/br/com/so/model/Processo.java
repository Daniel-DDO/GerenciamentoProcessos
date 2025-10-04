package br.com.so.model;

import br.com.so.services.ProcessoService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Processo {

    private Integer pid; //process ID
    private String nameProcess;
    private Integer priorityProcess;
    private Integer tempoCpu; //em ms (milisegundos)
    private EnumStatusProcesso statusProcesso;

    public Processo(Integer pid, String nameProcess, Integer priorityProcess, Integer tempoCpu, EnumStatusProcesso statusProcesso) {
        this.pid = pid;
        this.nameProcess = nameProcess;
        this.priorityProcess = priorityProcess;
        this.tempoCpu = tempoCpu;
        this.statusProcesso = statusProcesso;
    }

    public Processo() {}

    @Override
    public String toString() {
        return "Processo: "+nameProcess
                +"\nPID: "+pid
                +"\nPrioridade: "+priorityProcess
                +"\nTempo CPU: "+tempoCpu
                +"\nStatus processo: "+statusProcesso;
    }

    //rascunho
    public void met() {
        Integer pid = 23;
        String name = "P1";
        ProcessoService.getProcessoService().criarProcesso(pid, name);
    }
}
