package br.com.fiap.mscarrinho.domain.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
    @Column(name = "qtd_itens")
    private int quantidadeItens;
    @Column(name = "valor_total")
    private BigDecimal valorTotal;
    @OneToMany(mappedBy = "carrinho", fetch = FetchType.EAGER)
    private List<ItemEntity> listaItens;
    
    public CarrinhoEntity(Long idUsuario, int quantidadeItens, BigDecimal valorTotal,
            List<ItemEntity> listaItens) {
        this.idUsuario = idUsuario;
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
            this.quantidadeItens, 
            this.valorTotal, 
            this.toListDto()
        );
    }

    public List<ItemDtoResponse> toListDto(){
        return this.listaItens.stream().map(ItemEntity::toDto).collect(Collectors.toList());
    }

    @PrePersist
    public void prePersist() throws BusinessException {
        for (ItemEntity item : listaItens) {
            item.informarCarrinho(this);
        }
    }   
}