package br.com.fiap.mscarrinho.domain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.checkerframework.checker.units.qual.m;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.fiap.estrutura.exception.BusinessException;
import br.com.fiap.mscarrinho.domain.consumer.PedidoProducer;
import br.com.fiap.mscarrinho.domain.consumer.ProdutoConsumer;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoDtoRequest;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoDtoResponse;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoPedidoDto;
import br.com.fiap.mscarrinho.domain.dto.ItemDtoResponse;
import br.com.fiap.mscarrinho.domain.entity.CarrinhoEntity;
import br.com.fiap.mscarrinho.domain.entity.ItemEntity;
import br.com.fiap.mscarrinho.domain.repository.CarrinhoRepository;
import br.com.fiap.mscarrinho.domain.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class CarrinhoService {

    @Autowired
    private final CarrinhoRepository carrinhoRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final ProdutoConsumer produtoConsumer;

    @Autowired
    private final PedidoProducer pedidoProducer;

    @PersistenceContext
    private EntityManager entityManager;

    public CarrinhoService(CarrinhoRepository carrinhoRepository, ItemRepository itemRepository ,ProdutoConsumer produtoConsumer, PedidoProducer pedidoProducer) {
        this.carrinhoRepository = carrinhoRepository;
        this.itemRepository = itemRepository;
        this.produtoConsumer = produtoConsumer;
        this.pedidoProducer = pedidoProducer;
    }

    public CarrinhoDtoResponse adicionarAoCarrinho(CarrinhoDtoRequest carrinhoDtoRequest) throws BusinessException {
        CarrinhoEntity carrinhoExistente = carrinhoRepository.findByIdUsuario(carrinhoDtoRequest.idUsuario());

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

        Long quantidadeTotalItens = 0L;
        for (ItemEntity item : carrinhoExistente.getListaItens()) {
            quantidadeTotalItens += item.getQuantidade();
        }
        carrinhoExistente.setQuantidadeItens(quantidadeTotalItens);

        CarrinhoEntity carrinhoRetorno = carrinhoRepository.save(carrinhoExistente);
        return carrinhoRetorno.toDto();
    }

    private ItemEntity criarItem(Long idProduto, Long quantidadeProduto) throws BusinessException{
        ItemDtoResponse produto = this.produtoConsumer.obterProduto(idProduto);

                        if (produto == null) {
                            throw new BusinessException("Item " + idProduto + " não encontrado");
                        }
                        if (produto.preco() == 0) {
                            throw new BusinessException("Item " + idProduto + " não possui valor cadastrado");
                        }
            
                        ItemEntity itemNovo = new ItemEntity(0L, idProduto, quantidadeProduto, produto.preco(), calcularValorTotalItens(quantidadeProduto, produto.preco()), null);
                        
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

    private BigDecimal calcularValorTotalItens(Long quantidade, double valorUnitario) {
        return BigDecimal.valueOf(quantidade * valorUnitario);
    }

    public CarrinhoDtoResponse buscarCarrinho(Long id) throws BusinessException {
        CarrinhoEntity carrinho = buscarCarrinhoEntity(id);
        return carrinho.toDto();
    }

    public String enviarCarrinho(Long id) throws BusinessException {
        CarrinhoEntity carrinhoEntity = buscarCarrinhoEntity(id);

        CarrinhoPedidoDto carrinho = carrinhoEntity.toCarrinhoPedidoDto();
        
        ResponseEntity<String> enviarCarrinho = pedidoProducer.mandarPedido(carrinho);

        if(enviarCarrinho.getStatusCode().is2xxSuccessful()) {
            deletarCarrinho(carrinhoEntity.getIdCarrinho());
        } else {
            throw new BusinessException("Conexão com ms pedido não estabelecida");
        }

        String resposta = "Carrinho enviado com sucesso";
        return resposta;
    }

    public void deletarCarrinho(Long id) throws BusinessException {
        CarrinhoEntity carrinho = buscarCarrinhoEntity(id);
        carrinhoRepository.deleteById(id);
    }

    private CarrinhoEntity buscarCarrinhoEntity(Long id) throws BusinessException{
        CarrinhoEntity carrinho = carrinhoRepository.findById(id).orElse(null);
        if(carrinho ==  null){
            throw new BusinessException("Carrinho inexistente");
        }

        return carrinho;
    }

    @Transactional
    public String deletarItemCarrinho(Long idCarrinho,Long idProduto, Long quantidade) throws BusinessException{

        CarrinhoEntity carrinho = buscarCarrinhoEntity(idCarrinho);
        
        ItemEntity ItemCarrinho = carrinho.getListaItens().stream()
                .filter(item -> item.getIdProduto().equals(idProduto))
                .findFirst().orElse(null);

                if (ItemCarrinho == null) {
                    throw new BusinessException("Produto não encontrado no carrinho");
                }
        
               // ItemEntity item = optionalItem.get();
        
                if (quantidade > item.getQuantidade()) {
                    throw new BusinessException("Quantidade informada maior do que a existente no carrinho");
                }
        
                if (quantidade == item.getQuantidade()) {
                    List<ItemEntity> carrinhoSemItem = carrinho.getListaItens().stream()
                .filter(item -> !(item.getIdProduto().equals(idProduto)));
                    carrinho.setListaItens(carrinhoSemItem);
                    itemRepository.delete(item);
                } else {
                    item.setQuantidade(item.getQuantidade() - quantidade);
                    item.atualizarValorItens();
                    itemRepository.save(item);
                }
        
                atualizarCarrinho(carrinho);
        
                // if (carrinho.getQuantidadeItens() == 0) {
                //     deletarCarrinho(carrinho.getIdCarrinho());
                //     System.out.println("Carrinho excluído pois não há mais itens presentes");
                // }
        
                return "Item excluído com sucesso";
    }

    private void atualizarCarrinho(CarrinhoEntity carrinho) throws BusinessException {
        carrinho.setListaItens(itemRepository.findByCarrinho(carrinho));
    
        BigDecimal valorTotalCarrinho = BigDecimal.ZERO;
        Long quantidadeTotalCarrinho = 0L;
    
        for (ItemEntity item : carrinho.getListaItens()) {
            valorTotalCarrinho = valorTotalCarrinho.add(item.getValorItens());
            quantidadeTotalCarrinho += item.getQuantidade();
        }
    
        carrinho.setValorTotal(valorTotalCarrinho);
        carrinho.setQuantidadeItens(quantidadeTotalCarrinho);
    
        carrinhoRepository.save(carrinho);
        entityManager.flush();
        entityManager.clear();
    }
}
