package br.com.fiap.mscarrinho.domain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

    @Autowired
    private final ProdutoConsumer produtoConsumer;

    public CarrinhoService(CarrinhoRepository carrinhoRepository, ItemRepository itemRepository ,ProdutoConsumer produtoConsumer) {
        this.carrinhoRepository = carrinhoRepository;
        this.itemRepository = itemRepository;
        this.produtoConsumer = produtoConsumer;
    }

    public CarrinhoDtoResponse adicionarAoCarrinho(CarrinhoDtoRequest carrinhoDtoRequest) throws BusinessException {
        CarrinhoEntity carrinhoExistente = carrinhoRepository.findById(carrinhoDtoRequest.idUsuario()).orElse(null);

        if (carrinhoExistente != null) {

            List<ItemEntity> entityListItem = carrinhoDtoRequest.toEntityListItem();

            for (ItemEntity produtoRequest : entityListItem) {

                try {
                    ItemEntity produtoEncontrado = carrinhoExistente.getListaItens().stream()
                    .filter(p -> p.getIdProduto() == produtoRequest.getIdProduto()).findFirst().orElse(null);
                    
                    if(produtoEncontrado == null){

                        ItemEntity itemNovo =  criarItem(produtoRequest.getIdProduto(), produtoRequest.getQuantidade());

                        itemNovo.setCarrinho(carrinhoExistente);

                        List<ItemEntity> novaLista = new ArrayList<>();
                        novaLista.addAll(carrinhoExistente.getListaItens());
                        novaLista.add(itemNovo);

                        carrinhoExistente.setListaItens(novaLista);
                    } else{
                        produtoEncontrado.setQuantidade(produtoEncontrado.getQuantidade() + produtoRequest.getQuantidade());
                        produtoEncontrado.setValorItens(calcularValorTotalItens(produtoEncontrado.getQuantidade(), produtoEncontrado.getPreco()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            List<ItemEntity> novosItens = new ArrayList<>();
            for(ItemEntity item : carrinhoDtoRequest.toEntityListItem()) {
                novosItens.add(criarItem(item.getIdProduto(), item.getQuantidade()));
            }

            carrinhoExistente = new CarrinhoEntity(carrinhoDtoRequest.idUsuario(), novosItens);
        }

        this.calcularValorTotalCarrinho(carrinhoExistente);

        int quantidadeTotalItens = 0;
        for (ItemEntity item : carrinhoExistente.getListaItens()) {
            quantidadeTotalItens += item.getQuantidade();
        }
        carrinhoExistente.setQuantidadeItens(quantidadeTotalItens);

        CarrinhoEntity carrinhoRetorno = carrinhoRepository.save(carrinhoExistente);
        return carrinhoRetorno.toDto();
    }

    private ItemEntity criarItem(Long idProduto, int quantidadeProduto) throws BusinessException{
        ItemDtoResponse produto = this.produtoConsumer.obterProduto(idProduto);

                        if (produto == null) {
                            throw new BusinessException("Item " + idProduto + " não encontrado");
                        }
                        if (produto.preco() == 0) {
                            throw new BusinessException("Item " + idProduto + " não possui valor cadastrado");
                        }
            
                        ItemEntity itemNovo = new ItemEntity(0L, produto.id(), quantidadeProduto, produto.preco(), calcularValorTotalItens(quantidadeProduto, produto.preco()), null);
                        
            ItemEntity itemRetorno = itemRepository.save(itemNovo);
            return itemRetorno;
    }

    private void calcularValorTotalCarrinho(CarrinhoEntity carrinho) throws BusinessException {

        BigDecimal valorTotalCarrinho = BigDecimal.ZERO;

        for (ItemEntity item : carrinho.getListaItens()) {

            valorTotalCarrinho = valorTotalCarrinho.add(item.getValorItens());
        }
        carrinho.setValorTotal(valorTotalCarrinho);
    }

    private BigDecimal calcularValorTotalItens(int quantidade, double valorUnitario) {
        return BigDecimal.valueOf(quantidade * valorUnitario);
    }

    public CarrinhoDtoResponse buscarCarrinho(Long id) throws BusinessException {
        CarrinhoEntity carrinho = carrinhoRepository.findById(id).orElse(null);
        if (carrinho == null) {
            throw new BusinessException("Carrinho não encontrado");
        }
        return carrinho.toDto();
    }
}

//deletar 1 quantidade do item
// procurar na lista de itens do carrinho o id e diminuir a quantdade em 1

//deletar o item todo
// fazer um getcarrinho e pegar a lista de itens, com essa lista de itens procurar o produto que quero excluir pegar o valor total de itens e diminuir o valor total do carrinho e pegar a quantidade total dos itens e diminuir a quantidade total do carrinho

// a quantidade é maior que a quantidade itens no carrinho daquele item? diminui a quantidade, se não, tira o item todo


//deletar carrinho
// deletar pelo id do carrinho