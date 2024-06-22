package br.com.fiap.mscarrinho.domain.dto;

import java.util.ArrayList;
import java.util.List;

import br.com.fiap.estrutura.exception.BusinessException;
import br.com.fiap.mscarrinho.domain.entity.ItemEntity;

public record CarrinhoDtoRequest(
    Long idUsuario,
    List<ItemDtoRequest> itens
) {
    public List<ItemEntity> toEntityListItem() throws BusinessException {
        List<ItemEntity> itemEntityList = new ArrayList<>();
        itens.forEach(item -> {
            try {
                itemEntityList.add(new ItemEntity(item.idProduto(), item.quantidade()));
            } catch (BusinessException e) {
                throw new RuntimeException(e);
            }
        });
        return itemEntityList;
    }
}