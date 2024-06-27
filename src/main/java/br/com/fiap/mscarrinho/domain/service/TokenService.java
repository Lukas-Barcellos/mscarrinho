package br.com.fiap.mscarrinho.domain.service;

import org.springframework.beans.factory.annotation.Value;

import br.com.fiap.mscarrinho.domain.consumer.AuthConsumer;
import br.com.fiap.mscarrinho.domain.dto.LoginDtoRequest;

public class TokenService {
    private final AuthConsumer authConsumer;

    @Value("${auth.login}")
    private String authLogin;

    @Value("${auth.password}")
    private String authPassword;

    public TokenService(AuthConsumer authConsumer) {
        this.authConsumer = authConsumer;
    }

    public String generateToken(){
        LoginDtoRequest login = new LoginDtoRequest(this.authLogin,this.authPassword);
        return this.authConsumer.obterToken(login).token();
    }
}
