package br.com.fiap.mscarrinho.domain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.estrutura.swagger.annotations.ApiResponseSwaggerCreate;
import br.com.fiap.estrutura.swagger.annotations.ApiResponseSwaggerOk;
import br.com.fiap.estrutura.utils.SpringControllerUtils;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoDtoRequest;
import br.com.fiap.mscarrinho.domain.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/carrinhos")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @PostMapping("/criar")
    @Operation(summary = "Adiciona Produtos a um carrinho")
    @ApiResponseSwaggerCreate
    public ResponseEntity<?> criarCarrinho(@RequestBody CarrinhoDtoRequest carrinho) {
        return SpringControllerUtils.response(HttpStatus.CREATED,
                () -> carrinhoService.adicionarAoCarrinho(carrinho));
    }

    @GetMapping("/buscar/{id}")
    @Operation(summary = "Retorna dados do carrinho")
    @ApiResponseSwaggerOk
    public ResponseEntity<?> getCarrinho(@RequestParam Long id) {
        return SpringControllerUtils.response(HttpStatus.OK,
                () -> carrinhoService.buscarCarrinho(id));
    }

    @PostMapping("/pedido/{id}")
    @Operation(summary = "Envia carrinho para MS Pedido")
    public ResponseEntity<?> postPedido(@RequestParam Long id) {
        return SpringControllerUtils.response(HttpStatus.OK,
                () -> carrinhoService.enviarCarrinho(id));
    }

    @DeleteMapping("/{idProduto}")
    @Operation(summary = "Exclui um produto do carrinho")
    @ApiResponseSwaggerOk
    public ResponseEntity<?> excluirItemCarrinho(@RequestParam Long idCarrinho, Long idProduto, Long quantidade) {
        return SpringControllerUtils.response(HttpStatus.OK,
                () -> carrinhoService.deletarItemCarrinho(idCarrinho, idProduto, quantidade));
    }

}
