package br.com.so.model;

public enum EnumTipo {
    iobound("I/O bound"),
    cpubound("CPU bound");

    private final String nome;

    EnumTipo(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
