package br.com.so.model;

public enum EnumStatusProcesso {
    criado("Criado"),
    pronto("Pronto"),
    executando("Em execução"),
    espera("Em espera"),
    encerrado("Encerrado");

    private final String nome;

    EnumStatusProcesso(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
