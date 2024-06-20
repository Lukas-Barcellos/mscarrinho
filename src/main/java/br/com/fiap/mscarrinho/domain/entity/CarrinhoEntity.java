package br.com.fiap.mscarrinho.domain.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.fiap.estrutura.exception.BusinessException;
import br.com.fiap.mscarrinho.domain.dto.CarrinhoDtoResponse;
import br.com.fiap.mscarrinho.domain.dto.ItemDtoResponse;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="tb_carrinho")
public class CarrinhoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_carrinho", unique = true)
    private Long idCarrinho;
    @Column(name = "cd_usuario")
    private Long idUsuario;
    @Column(name = "forma_pagamento")
    private FormaPagamentoEnum formaPagamento;
    @Column(name = "qtd_itens")
    private int quantidadeItens;
    @Column(name = "valor_total")
    private BigDecimal valorTotal;
    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemEntity> listaItens;
    
    public CarrinhoEntity(Long idUsuario, FormaPagamentoEnum formaPagamento, int quantidadeItens, BigDecimal valorTotal,
            List<ItemEntity> listaItens) {
        this.idUsuario = idUsuario;
        this.formaPagamento = formaPagamento;
        this.quantidadeItens = quantidadeItens;
        this.valorTotal = valorTotal;
        this.listaItens = listaItens;
    }

    public CarrinhoEntity(Long idUsuario ,List<ItemEntity> listaItens) {
        this.idUsuario = idUsuario;
        this.listaItens = listaItens;
    }

    public CarrinhoDtoResponse toDto() {
        return new CarrinhoDtoResponse(
            this.idCarrinho, 
            this.idUsuario, 
            this.formaPagamento, 
            this.quantidadeItens, 
            this.valorTotal, 
            this.toListDto()
        );
    }

    public List<ItemDtoResponse> toListDto(){
        List<ItemDtoResponse> itens = new ArrayList<>();
        if(this.listaItens != null && this.listaItens.size() > 0) {
            this.listaItens.forEach(item -> {
                itens.add(new ItemDtoResponse(item.getIdProduto(), item.getQuantidade(), item.getPreco()));
            });
        }
        return itens;
    }

    public void calvularValorTotalCarrinho(BigDecimal valorTotal) throws BusinessException{
        if(valorTotal.compareTo(BigDecimal.ZERO) == 0 || valorTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Valor total dos itens não pode ser igual ou menor que zero");
        }
        this.valorTotal = this.valorTotal.add(valorTotal);
    }

    @PrePersist
    public void prePersist() throws BusinessException {
        for (ItemEntity item : listaItens) {
            item.informarCarrinho(this);
        }
    }

    
}
