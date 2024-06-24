package br.com.fiap.mscarrinho.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.fiap.mscarrinho.domain.entity.CarrinhoEntity;
import br.com.fiap.mscarrinho.domain.entity.ItemEntity;
import java.util.List;
import java.util.Optional;


@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findByCarrinho(CarrinhoEntity carrinho);
    ItemEntity findByIdProduto(Long idProduto);
}
