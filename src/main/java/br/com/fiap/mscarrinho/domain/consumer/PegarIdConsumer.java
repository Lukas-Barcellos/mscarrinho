package br.com.fiap.mscarrinho.domain.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.fiap.mscarrinho.domain.consumer.config.FeignConfig;

@FeignClient(name ="tokenUser", url ="${auth.url}", configuration = FeignConfig.class)
public interface PegarIdConsumer {
    @PostMapping("/auth/currentUser")
    String obterId(@RequestParam String token);
    
}
