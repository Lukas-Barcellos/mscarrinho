package br.com.fiap.mscarrinho.domain.entity;

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
    private double valorItens = quantidade * preco;
    @ManyToOne
    @JoinColumn(name = "id_carrinho")
    private CarrinhoEntity carrinho;


    public ItemEntity(long idProduto, int quantidade, double preco) {
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.preco = preco;
    }


    public ItemDtoResponse toDto() {
        return new ItemDtoResponse(
            this.idProduto, 
            this.quantidade, 
            this.preco); 
    }

}
    
    


