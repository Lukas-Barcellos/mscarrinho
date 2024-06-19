package br.com.fiap.mscarrinho.domain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.estrutura.swagger.annotations.ApiResponseSwaggerCreate;
import br.com.fiap.estrutura.utils.SpringControllerUtils;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoDtoRequest;
import br.com.fiap.mscarrinho.domain.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;
    
    @PostMapping
    @Operation(summary = "Adiciona itens a um carrinho")
    @ApiResponseSwaggerCreate
    public ResponseEntity<?> criarCarrinho(@RequestBody CarrinhoDtoRequest carrinho){
        return SpringControllerUtils.response(HttpStatus.CREATED, 
                () -> carrinhoService.adicionarAoCarrinho(carrinho)
        );
    }
}
