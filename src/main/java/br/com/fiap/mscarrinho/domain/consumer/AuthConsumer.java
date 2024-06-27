package br.com.fiap.mscarrinho.domain.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.fiap.mscarrinho.domain.dto.AuthTokenDtoResponse;
import br.com.fiap.mscarrinho.domain.dto.LoginDtoRequest;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name = "auth", url = "${auth.url}")
public interface AuthConsumer {
    @PostMapping(value = "/auth/login")
    AuthTokenDtoResponse obterToken(@RequestBody LoginDtoRequest login);
    
}
