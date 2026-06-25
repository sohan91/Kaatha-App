package com.kaatha.item.service;

import com.kaatha.item.dto.request.ItemRequest;
import com.kaatha.item.dto.request.StockUpdateRequest;
import com.kaatha.item.dto.response.ItemResponse;

import java.util.List;

public interface ItemService {
    ItemResponse createItem(ItemRequest request);
    List<ItemResponse> getItemsByShopkeeper(Long shopkeeperId);
    ItemResponse getItemById(Long id);
    ItemResponse updateItem(Long id, ItemRequest request);
    void updateStock(List<StockUpdateRequest> requests);
    List<ItemResponse> getItemsByIds(List<Long> ids);
}
