package br.com.fiap.mscarrinho.domain.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.fiap.mscarrinho.domain.dto.CarrinhoPedidoDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name = "pedido", url = "${pedido.url}")
public interface PedidoProducer {
    @PostMapping
    ResponseEntity<String> mandarPedido(@RequestBody CarrinhoPedidoDto carrinho);
    
}
