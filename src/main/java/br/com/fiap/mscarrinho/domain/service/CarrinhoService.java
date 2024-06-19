package br.com.fiap.mscarrinho.domain.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.estrutura.exception.BusinessException;
import br.com.fiap.mscarrinho.domain.consumer.ProdutoConsumer;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoDtoRequest;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoDtoResponse;
import br.com.fiap.mscarrinho.domain.dto.ItemDtoResponse;
import br.com.fiap.mscarrinho.domain.entity.CarrinhoEntity;
import br.com.fiap.mscarrinho.domain.entity.ItemEntity;
import br.com.fiap.mscarrinho.domain.repository.CarrinhoRepository;
import br.com.fiap.mscarrinho.domain.repository.ItemRepository;

@Service
public class CarrinhoService {
    
    @Autowired
    private final CarrinhoRepository carrinhoRepository;

    @Autowired
    private final ItemRepository itemRepository;

    private final ProdutoConsumer produtoConsumer;

    public CarrinhoService(CarrinhoRepository carrinhoRepository, ItemRepository itemRepository, ProdutoConsumer produtoConsumer) {
        this.carrinhoRepository = carrinhoRepository;
        this.itemRepository = itemRepository;
        this.produtoConsumer = produtoConsumer;
    }

    public CarrinhoDtoResponse adicionarAoCarrinho (CarrinhoDtoRequest carrinhoDtoRequest) throws BusinessException{
        
        Optional<CarrinhoEntity> carrinhoExistente = carrinhoRepository.findById(carrinhoDtoRequest.idUsuario());

        CarrinhoEntity carrinho;
        if(carrinhoExistente.isPresent()) {
            carrinho = carrinhoExistente.get();
        } else {
            carrinho = new CarrinhoEntity(carrinhoDtoRequest.toEntityListItem());
        }        
        this.calvularValorTotalCarrinho(carrinho);
        
        CarrinhoEntity carrinhoRetorno = carrinhoRepository.save(carrinho);
        return carrinhoRetorno.toDto();
    }

    // public CarrinhoDtoResponse adicionarAoCarrinho(CarrinhoDtoRequest carrinhoDtoRequest) throws BusinessException {
       
        
    //     CarrinhoEntity carrinho;
    //     if (carrinhoExistente.isPresent()) {
    //         carrinho = carrinhoExistente.get();
    //         // Atualiza o carrinho existente com os novos itens
    //     } else {
    //         carrinho = new CarrinhoEntity(carrinhoDtoRequest.toEntityListItem());
    //         // Cria um novo carrinho
    //     }
        
    //     CarrinhoEntity carrinhoRetorno = carrinhoRepository.save(carrinho);
    //     return carrinhoRetorno.toDto();
    // }

    private CarrinhoEntity buscarCarrinhoEntity(Long id) throws BusinessException {
        CarrinhoEntity carrinho = carrinhoRepository.findById(id).orElse(null);
        if(carrinho == null){
            throw new BusinessException("Produto não encontrado");
        }
        return carrinho;
    }

    // public CarrinhoDtoResponse criarCarrinho (CarrinhoDtoRequest carrinhoDtoRequest) throws BusinessException{
        
    //     final CarrinhoEntity carrinho = new CarrinhoEntity(
    //         carrinhoDtoRequest.idUsuario(),
    //         carrinhoDtoRequest.formaPagamento(),
    //         carrinhoDtoRequest.quantidadeItens(),
    //         carrinhoDtoRequest.valorTotal(),
    //         carrinhoDtoRequest.toEntityListItem()   
    //     );
    //     this.calvularValorTotalCarrinho(carrinho);

    //     CarrinhoEntity carinhoRetorno = carrinhoRepository.save(carrinho);
    //     return carinhoRetorno.toDto();
    // }

    private void calvularValorTotalCarrinho(CarrinhoEntity carrinho)throws BusinessException{
        for(ItemEntity item : carrinho.getListaItens()) {
            ItemDtoResponse itemDtoResponse =  this.produtoConsumer.obterProduto(item.getIdProduto());
            if(itemDtoResponse == null) {
                throw new BusinessException("Item " + item.getIdProduto() + " não encontrado");
            }
            if(itemDtoResponse.preco() == 0) {
                throw new BusinessException("Item " + item.getIdProduto() + " não possui valor cadastrado");
            }

            item.setValorItens(this.calcularValorTotalItens(item.getQuantidade(), itemDtoResponse.preco()));
            carrinho.calvularValorTotalCarrinho(item.getValorItens());
        }
     }

    private BigDecimal calcularValorTotalItens(int quantidade, double valorUnitario){
        return BigDecimal.valueOf(quantidade * valorUnitario);
    }
}