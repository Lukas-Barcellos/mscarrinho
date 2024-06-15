package br.com.fiap.mscarrinho.domain.dto;

import java.util.List;

import br.com.fiap.mscarrinho.domain.entity.FormaPagamentoEnum;

public record CarrinhoDtoResponse(
    Long idCarrinho,
    Long idUsuario,
    FormaPagamentoEnum formaPagamento,
    int quantidadeItens,
    double valorTotal,
    List<ItemDtoResponse> itens
) {}
