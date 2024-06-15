package br.com.fiap.mscarrinho.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.mscarrinho.domain.entity.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

}
