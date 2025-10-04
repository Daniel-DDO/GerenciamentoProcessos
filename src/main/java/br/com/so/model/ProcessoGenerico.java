package br.com.so.model;

import br.com.so.model.EnumStatus;
import br.com.so.model.EnumTipo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ProcessoGenerico {
    private Integer pid;
    private String nome;
    private Integer prioridade;
    private Integer tempoCpu; // tempo total em ms
    private EnumStatus status;
    private EnumTipo tipo;
    private int nivelFila;
    private int tempoRestante;
    private int tempoEspera;
    private int tempoInicio;
    private int tempoTermino;
    private int tempoChegada;

    // Construtor para Múltiplas Filas (com prioridade)
    public ProcessoGenerico(Integer pid, String nome, Integer prioridade, Integer tempoCpu, EnumStatus status, EnumTipo tipo) {
        this.pid = pid;
        this.nome = nome;
        this.prioridade = prioridade;
        this.tempoCpu = tempoCpu;
        this.tempoRestante = tempoCpu;
        this.status = status;
        this.tipo = tipo;
        this.tempoEspera = 0;
        this.tempoInicio = -1;
        this.tempoTermino = -1;
        this.nivelFila = 0;
        this.tempoChegada = 0;
    }

    // Construtor para Round Robin (sem prioridade)
    public ProcessoGenerico(Integer pid, String nome, Integer tempoCpu, EnumStatus status, EnumTipo tipo) {
        this(pid, nome, 0, tempoCpu, status, tipo);
    }

    // Calcula o tempo de turnaround
    public int getTurnaround() {
        if (tempoTermino != -1 && tempoChegada >= 0) {
            return tempoTermino - tempoChegada;
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PID: ").append(pid)
                .append(" | Nome: ").append(nome)
                .append(" | Tipo: ").append(tipo.getDescricao())
                .append(" | Tempo CPU: ").append(tempoCpu).append("ms")
                .append(" | Tempo Restante: ").append(tempoRestante).append("ms")
                .append(" | Status: ").append(status.getDescricao());

        if (prioridade != null && prioridade > 0) {
            sb.append(" | Prioridade: ").append(prioridade);
        }

        return sb.toString();
    }
}
