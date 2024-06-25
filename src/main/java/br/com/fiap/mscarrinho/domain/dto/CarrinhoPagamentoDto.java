package br.com.fiap.mscarrinho.domain.dto;

import java.math.BigDecimal;
import java.util.List;

import br.com.fiap.mscarrinho.domain.entity.FormaPagamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CarrinhoPagamentoDto {
    Long idUsuario;
    Long quantidadeItens;
    BigDecimal valorTotal;
    FormaPagamento formaPagamento;
    List<ItemDtoResponse> itens;
}
