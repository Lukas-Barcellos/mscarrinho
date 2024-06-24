package br.com.fiap.mscarrinho.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.fiap.mscarrinho.domain.entity.CarrinhoEntity;


@Repository
public interface CarrinhoRepository extends JpaRepository<CarrinhoEntity, Long> {
    CarrinhoEntity findByIdUsuario(Long idUsuario);
    
}
