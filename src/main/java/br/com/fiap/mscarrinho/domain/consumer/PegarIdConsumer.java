package br.com.fiap.mscarrinho.domain.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.fiap.mscarrinho.domain.consumer.config.FeignConfig;
import br.com.fiap.mscarrinho.domain.dto.UsuarioId;

@FeignClient(name ="tokenUser", url ="${auth.url}", configuration = FeignConfig.class)
public interface PegarIdConsumer {
    @PostMapping("/auth/currentUser")
    UsuarioId obterId(@RequestParam String token);
    
}
