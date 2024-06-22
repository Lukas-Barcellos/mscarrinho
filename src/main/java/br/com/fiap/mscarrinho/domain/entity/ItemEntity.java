package br.com.fiap.mscarrinho.domain.entity;

import java.math.BigDecimal;

import br.com.fiap.estrutura.exception.BusinessException;
import br.com.fiap.mscarrinho.domain.dto.ItemDtoResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrinho")
    private CarrinhoEntity carrinho;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (idProduto ^ (idProduto >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItemEntity other = (ItemEntity) obj;
        if (idProduto != other.idProduto)
            return false;
        return true;
    }


    public ItemEntity(long idProduto, int quantidade) throws BusinessException {
        if(idProduto == 0) {
            throw new BusinessException("Produto não informado");
        }
        if (quantidade <= 0 ) {
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
    
    


