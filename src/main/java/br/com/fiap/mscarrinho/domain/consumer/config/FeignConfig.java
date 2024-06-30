package br.com.fiap.mscarrinho.domain.consumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.fiap.mscarrinho.domain.service.TokenService;
import feign.RequestInterceptor;
import feign.RequestTemplate;

@Configuration
public class FeignConfig {
    
    private final TokenService tokenService;

    public FeignConfig(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {

        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                template.header("Authorization", "Bearer " + getJWT());
            }
        };
    }

    private String getJWT(){
        return tokenService.generateToken();
    }
}
