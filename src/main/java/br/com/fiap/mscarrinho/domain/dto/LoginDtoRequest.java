package br.com.fiap.mscarrinho.domain.dto;

public record LoginDtoRequest(
    String email,
    String password
) {}
