package br.com.fiap.mscarrinho.domain.dto;

import java.math.BigDecimal;
import java.util.List;

public record CarrinhoDtoResponse(
    Long idCarrinho,
    Long idUsuario,
    int quantidadeItens,
    BigDecimal valorTotal,
    List<ItemDtoResponse> itens
) {}
