package br.com.fiap.mscarrinho.domain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.estrutura.swagger.annotations.ApiResponseSwaggerCreate;
import br.com.fiap.estrutura.swagger.annotations.ApiResponseSwaggerOk;
import br.com.fiap.estrutura.utils.SpringControllerUtils;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoDtoRequest;
import br.com.fiap.mscarrinho.domain.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @PostMapping
    @Operation(summary = "Adiciona Produtos a um carrinho")
    @ApiResponseSwaggerCreate
    public ResponseEntity<?> criarCarrinho(@RequestBody CarrinhoDtoRequest carrinho) {
        return SpringControllerUtils.response(HttpStatus.CREATED,
                () -> carrinhoService.adicionarAoCarrinho(carrinho));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retorna dados do carrinho")
    @ApiResponseSwaggerOk
    public ResponseEntity<?> getCarrinho(@RequestParam Long id) {
        return SpringControllerUtils.response(HttpStatus.OK,
                () -> carrinhoService.buscarCarrinho(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um carrinho")
    @ApiResponseSwaggerOk
    public ResponseEntity<?> deleteCarrinho(@RequestParam Long id) {
        return SpringControllerUtils.response(HttpStatus.OK, 
                () -> carrinhoService.deletarCarrinho(id));
    }

}
