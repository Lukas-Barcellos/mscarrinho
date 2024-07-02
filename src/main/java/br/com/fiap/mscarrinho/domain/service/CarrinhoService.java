package br.com.fiap.mscarrinho.domain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.fiap.estrutura.exception.BusinessException;
import br.com.fiap.mscarrinho.domain.consumer.PagamentoProducer;
import br.com.fiap.mscarrinho.domain.consumer.ProdutoConsumer;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoDtoRequest;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoDtoResponse;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoPagamentoDto;
import br.com.fiap.mscarrinho.domain.dto.ItemDtoResponse;
import br.com.fiap.mscarrinho.domain.dto.PagamentoDtoResponse;
import br.com.fiap.mscarrinho.domain.entity.CarrinhoEntity;
import br.com.fiap.mscarrinho.domain.entity.FormaPagamento;
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
    private final PagamentoProducer pagamentoProducer;

    @PersistenceContext
    private EntityManager entityManager;

    public CarrinhoService(CarrinhoRepository carrinhoRepository, ItemRepository itemRepository,
            ProdutoConsumer produtoConsumer, PagamentoProducer pedidoProducer) {
        this.carrinhoRepository = carrinhoRepository;
        this.itemRepository = itemRepository;
        this.produtoConsumer = produtoConsumer;
        this.pagamentoProducer = pedidoProducer;
    }

    public CarrinhoDtoResponse adicionarAoCarrinho(Long idUsuario, CarrinhoDtoRequest carrinhoDtoRequest) throws BusinessException {
        CarrinhoEntity carrinhoExistente = carrinhoRepository.findByIdUsuario(idUsuario);

        if (carrinhoExistente != null) {

            List<ItemEntity> entityListItem = carrinhoDtoRequest.toEntityListItem();

            for (ItemEntity produtoRequest : entityListItem) {

                try {
                    ItemEntity produtoEncontrado = carrinhoExistente.getListaItens().stream()
                            .filter(p -> p.getIdProduto() == produtoRequest.getIdProduto()).findFirst().orElse(null);

                    if (produtoEncontrado == null) {

                        ItemEntity itemNovo = criarItem(produtoRequest.getIdProduto(), produtoRequest.getQuantidade());

                        itemNovo.setCarrinho(carrinhoExistente);

                        List<ItemEntity> novaLista = new ArrayList<>();
                        novaLista.addAll(carrinhoExistente.getListaItens());
                        novaLista.add(itemNovo);

                        carrinhoExistente.setListaItens(novaLista);
                    } else {
                        produtoEncontrado
                                .setQuantidade(produtoEncontrado.getQuantidade() + produtoRequest.getQuantidade());
                        produtoEncontrado.setValorItens(calcularValorTotalItens(produtoEncontrado.getQuantidade(),
                                produtoEncontrado.getPreco()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            List<ItemEntity> novosItens = new ArrayList<>();
            for (ItemEntity item : carrinhoDtoRequest.toEntityListItem()) {
                novosItens.add(criarItem(item.getIdProduto(), item.getQuantidade()));
            }

            carrinhoExistente = new CarrinhoEntity(idUsuario, novosItens);
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

    private ItemEntity criarItem(Long idProduto, Long quantidadeProduto) throws BusinessException {
        ItemDtoResponse produto = this.produtoConsumer.obterProduto(idProduto);

        if (produto == null) {
            throw new BusinessException("Item " + idProduto + " não encontrado");
        }
        if (produto.preco() == 0) {
            throw new BusinessException("Item " + idProduto + " não possui valor cadastrado");
        }

        ItemEntity itemNovo = new ItemEntity(0L, idProduto, quantidadeProduto, produto.preco(),
                calcularValorTotalItens(quantidadeProduto, produto.preco()), null);

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

    public PagamentoDtoResponse enviarCarrinho(Long id) throws BusinessException {
        CarrinhoEntity carrinhoEntity = buscarCarrinhoEntity(id);

        CarrinhoPagamentoDto carrinho = carrinhoEntity.toCarrinhoPedidoDto();
        FormaPagamento formaPagamento = FormaPagamento.PIX;
        carrinho.setFormaPagamento(formaPagamento);

        PagamentoDtoResponse enviarCarrinho = pagamentoProducer.mandarPagamento(carrinho);

        if (enviarCarrinho != null) {
            deletarCarrinho(carrinhoEntity.getIdCarrinho());
        } else {
            throw new BusinessException("Conexão com ms pedido não estabelecida");
        }
        
        return enviarCarrinho;
    }

    public void deletarCarrinho(Long id) throws BusinessException {
        buscarCarrinhoEntity(id);
        carrinhoRepository.deleteById(id);
    }

    private CarrinhoEntity buscarCarrinhoEntity(Long id) throws BusinessException {
        CarrinhoEntity carrinho = carrinhoRepository.findById(id).orElse(null);
        if (carrinho == null) {
            throw new BusinessException("Carrinho inexistente");
        }

        return carrinho;
    }

    @Transactional
    public String deletarItemCarrinho(Long idCarrinho, Long idProduto, Long quantidade) throws BusinessException {

        CarrinhoEntity carrinho = buscarCarrinhoEntity(idCarrinho);

        ItemEntity ItemCarrinho = carrinho.getListaItens().stream()
                .filter(item -> item.getIdProduto().equals(idProduto))
                .findFirst().orElse(null);

        if (ItemCarrinho == null) {
            throw new BusinessException("Produto não encontrado no carrinho");
        }

        if (quantidade == 0) {
            throw new BusinessException("Quantidade deve ser maior que zero");
        }

        if (quantidade > ItemCarrinho.getQuantidade()) {
            throw new BusinessException("Quantidade informada maior do que a existente no carrinho");
        }

        if (quantidade == ItemCarrinho.getQuantidade()) {
            List<ItemEntity> carrinhoSemItem = carrinho.getListaItens().stream()
                    .filter(item -> !(ItemCarrinho.getIdProduto().equals(idProduto))).toList();

            carrinho.setListaItens(carrinhoSemItem);
            itemRepository.delete(ItemCarrinho);

        } else {
            ItemCarrinho.setQuantidade(ItemCarrinho.getQuantidade() - quantidade);
            ItemCarrinho.atualizarValorItens();
            itemRepository.save(ItemCarrinho);
        }

        atualizarCarrinho(carrinho);

        if (carrinho.getQuantidadeItens() == 0) {
            deletarCarrinho(carrinho.getIdCarrinho());
        }

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
