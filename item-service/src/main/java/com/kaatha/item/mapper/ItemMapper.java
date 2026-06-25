package com.kaatha.item.mapper;

import com.kaatha.item.dto.request.ItemRequest;
import com.kaatha.item.dto.response.ItemResponse;
import com.kaatha.item.entity.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public Item toEntity(ItemRequest request) {
        return Item.builder()
                .shopkeeperId(request.getShopkeeperId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sku(request.getSku())
                .stockQuantity(request.getStockQuantity())
                .active(true)
                .build();
    }

    public ItemResponse toResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .shopkeeperId(item.getShopkeeperId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .sku(item.getSku())
                .stockQuantity(item.getStockQuantity())
                .active(item.isActive())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
