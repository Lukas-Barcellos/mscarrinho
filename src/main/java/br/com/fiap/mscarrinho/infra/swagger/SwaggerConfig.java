package br.com.fiap.mscarrinho.infra.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI OpenAPI() {
        return new OpenAPI() 
            .info(new Info()
                    .title("APIs do Microsserviço Carrinho")
                    .version("v1")
                    .description("APIs do Microserviço Carrinho criada exclusivamente para o TechChallenge 5 da FIAP.")
            );
    }
}
