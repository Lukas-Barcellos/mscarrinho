package br.com.fiap.mscarrinho.domain.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.fiap.mscarrinho.domain.consumer.config.FeignConfig;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoPagamentoDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name = "pagamento", url = "${pagamento.url}", configuration = FeignConfig.class)
public interface PagamentoProducer {
    @PostMapping
    ResponseEntity<String> mandarPagamento(@RequestBody CarrinhoPagamentoDto carrinho);
    
}
