package br.com.fiap.mscarrinho.domain.dto;

import java.util.List;

import br.com.fiap.mscarrinho.domain.entity.FormaPagamentoEnum;
import br.com.fiap.mscarrinho.domain.entity.ItemEntity;

public record CarrinhoDtoRequest(
    Long idUsuario,
    List<ItemEntity> itens,
    FormaPagamentoEnum formaPagamento,
    int quantidadeItens,
    double valorTotal
) {}
