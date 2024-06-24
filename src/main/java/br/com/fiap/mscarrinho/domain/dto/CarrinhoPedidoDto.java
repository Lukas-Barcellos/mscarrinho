package br.com.fiap.mscarrinho.domain.dto;

import java.math.BigDecimal;
import java.util.List;

public record CarrinhoPedidoDto(
    Long idUsuario,
    Long quantidadeItens,
    BigDecimal valorTotal,
    List<ItemDtoResponse> itens
) {}
