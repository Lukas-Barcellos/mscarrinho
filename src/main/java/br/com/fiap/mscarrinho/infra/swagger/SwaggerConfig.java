package br.com.fiap.mscarrinho.infra.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Value("${spring.profiles.active:Unknown}")
    String activeProfile;

    @Value("${spring.application.name:Unknown}")
    String applicationName;

    @Value("${server.port:Unknown}")
    String serverPort;

    @Bean
    OpenAPI OpenAPI() {
        io.swagger.v3.oas.models.OpenAPI info = new OpenAPI() 
            .info(new Info()
                    .title("APIs do Microsserviço Carrinho")
                    .version("v1")
                    .description("APIs do Microserviço Carrinho criada exclusivamente para o TechChallenge 5 da FIAP.")
            );
        if("docker".equals(activeProfile)){
            info.servers(Arrays.asList(getServer()));
        }
        return info;
    }

    private Server getServer() {
        Server server = new Server();
        server.setDescription("Gateway Docker");
        server.setUrl("http://"+applicationName+":"+serverPort);
        return server;
    }
}
