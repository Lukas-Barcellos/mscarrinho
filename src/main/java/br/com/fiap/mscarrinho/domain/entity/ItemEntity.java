package br.com.fiap.mscarrinho.domain.entity;

import java.math.BigDecimal;

import br.com.fiap.estrutura.exception.BusinessException;
import br.com.fiap.mscarrinho.domain.dto.ItemDtoResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_item")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_item_carrinho", unique = true)
    private Long idItem;
    @Column(name = "cd_produto")
    private long idProduto;
    @Column(name = "nu_quantidade")
    private int quantidade;
    @Column(name = "preco_un")
    private double preco;
    @Column(name = "valor_total")
    private BigDecimal valorItens;
    @ManyToOne
    @JoinColumn(name = "id_carrinho")
    private CarrinhoEntity carrinho;


    public ItemEntity(long idProduto, int quantidade) throws BusinessException {
        if(idProduto == 0) {
            throw new BusinessException("Produto não informado");
        }
        if (quantidade == 0 || quantidade < 0) {
            throw new BusinessException("Quantidade informada deve ser maior que zero");
        }
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.valorItens = BigDecimal.ZERO;
    }


    public ItemDtoResponse toDto() {
        return new ItemDtoResponse(
            this.idProduto, 
            this.quantidade, 
            this.preco); 
    }

    public void setValorItens(BigDecimal valorTotalItens) throws BusinessException {
        if(valorTotalItens.compareTo(BigDecimal.ZERO) == 0){
            throw new BusinessException("Valor total não pode ser igual a zero.");
        }
        this.valorItens = valorTotalItens;
    }

    public void informarCarrinho(CarrinhoEntity carrinho) throws BusinessException {
        if (carrinho == null){
            throw new BusinessException("Carrinho deve ser informado");
        }
        this.carrinho = carrinho;
    }

}
    
    


