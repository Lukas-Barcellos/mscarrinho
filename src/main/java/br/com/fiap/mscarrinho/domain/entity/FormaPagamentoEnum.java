package br.com.fiap.mscarrinho.domain.entity;

public enum FormaPagamentoEnum {
    CARTAO_DE_CREDITO(1),
    CARTAO_DE_DEBITO(2),
    PIX(3);

    private final int descricao;

    FormaPagamentoEnum(int descricao) {
        this.descricao = descricao;
    }

    public int getDescricao() {
        return descricao;
    }

    

}
