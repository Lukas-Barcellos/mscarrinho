package br.com.fiap.mscarrinho.domain.entity;

import java.util.ArrayList;
import java.util.List;

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
    private double valorTotal;
    @OneToMany(mappedBy = "itens", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemEntity> listaItens;
    
    public CarrinhoEntity(Long idUsuario, FormaPagamentoEnum formaPagamento, int quantidadeItens, double valorTotal,
            List<ItemEntity> listaItens) {
        this.idUsuario = idUsuario;
        this.formaPagamento = formaPagamento;
        this.quantidadeItens = quantidadeItens;
        this.valorTotal = valorTotal;
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

    
}
