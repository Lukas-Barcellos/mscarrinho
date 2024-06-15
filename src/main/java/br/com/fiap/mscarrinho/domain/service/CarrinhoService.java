package br.com.fiap.mscarrinho.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.mscarrinho.domain.consumer.ProdutoConsumer;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoDtoRequest;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoDtoResponse;
import br.com.fiap.mscarrinho.domain.dto.ItemDtoRequest;
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

    private CarrinhoDtoResponse criarCarrinho (CarrinhoDtoRequest carrinhoDtoRequest){

        final CarrinhoEntity carrinho = new CarrinhoEntity(
            carrinhoDtoRequest.idUsuario(),
            carrinhoDtoRequest.formaPagamento(),
            carrinhoDtoRequest.quantidadeItens(),
            carrinhoDtoRequest.valorTotal(),
            carrinhoDtoRequest.itens()    
        );

        CarrinhoEntity carinhoRetorno = carrinhoRepository.save(carrinho);
        return carinhoRetorno.toDto();
    }

    private ItemDtoResponse adicionarCarrinho(ItemDtoRequest itemDtoRequest) {
        final ItemEntity item = new ItemEntity(
            itemDtoRequest.id(),
            itemDtoRequest.quantidade(),
            itemDtoRequest.preco()
        );

        ItemEntity itemRetorno = itemRepository.save(item);
        return itemRetorno.toDto();
    }

}
