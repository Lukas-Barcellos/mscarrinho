package br.com.fiap.mscarrinho.domain.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.fiap.mscarrinho.domain.consumer.config.FeignConfig;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoPagamentoDto;
import br.com.fiap.mscarrinho.domain.dto.PagamentoDtoResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name = "pagamento", url = "${pagamento.url}", configuration = FeignConfig.class)
public interface PagamentoProducer {
    @PostMapping
    PagamentoDtoResponse mandarPagamento(@RequestBody CarrinhoPagamentoDto carrinho);
    
}
