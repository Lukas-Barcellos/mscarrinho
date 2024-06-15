package br.com.fiap.mscarrinho.domain.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.fiap.mscarrinho.domain.dto.ItemDtoResponse;

@FeignClient(name = "produto", url = "${produto.url}")
public interface ProdutoConsumer {
    @GetMapping(value = "/{id}")
    ItemDtoResponse obterProduto(@PathVariable("id") Long id);
}
