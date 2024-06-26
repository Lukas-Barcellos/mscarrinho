package br.com.fiap.mscarrinho.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.fiap.mscarrinho.domain.consumer.AuthConsumer;
import br.com.fiap.mscarrinho.domain.dto.AuthTokenDtoResponse;
import br.com.fiap.mscarrinho.domain.dto.LoginDtoRequest;

@Service
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
        AuthTokenDtoResponse obterToken = this.authConsumer.obterToken(login);
        return obterToken.token();
    }
}
