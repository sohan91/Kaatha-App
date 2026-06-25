package com.kaatha.item.service.impl;

import com.kaatha.item.dto.request.ItemRequest;
import com.kaatha.item.dto.request.StockUpdateRequest;
import com.kaatha.item.dto.response.ItemResponse;
import com.kaatha.item.entity.Item;
import com.kaatha.item.exception.InsufficientStockException;
import com.kaatha.item.exception.ItemNotFoundException;
import com.kaatha.item.mapper.ItemMapper;
import com.kaatha.item.repository.ItemRepository;
import com.kaatha.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemResponse createItem(ItemRequest request) {
        Item item = itemMapper.toEntity(request);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toResponse(savedItem);
    }

    @Override
    public List<ItemResponse> getItemsByShopkeeper(Long shopkeeperId) {
        return itemRepository.findByShopkeeperIdAndActiveTrue(shopkeeperId)
                .stream()
                .map(itemMapper::toResponse)
                .toList();
    }

    @Override
    public ItemResponse getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with ID: " + id));
        return itemMapper.toResponse(item);
    }

    @Override
    @Transactional
    public ItemResponse updateItem(Long id, ItemRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with ID: " + id));

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setSku(request.getSku());
        item.setStockQuantity(request.getStockQuantity());

        Item updatedItem = itemRepository.save(item);
        return itemMapper.toResponse(updatedItem);
    }

    @Override
    @Transactional
    public void updateStock(List<StockUpdateRequest> requests) {
        for (StockUpdateRequest request : requests) {
            Item item = itemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new ItemNotFoundException("Item not found with ID: " + request.getItemId()));

            int newStock = item.getStockQuantity() + request.getQuantityChange();
            if (newStock < 0) {
                throw new InsufficientStockException("Insufficient stock for item: " + item.getName() +
                        ". Requested change: " + request.getQuantityChange() + ", Available: " + item.getStockQuantity());
            }

            item.setStockQuantity(newStock);
            itemRepository.save(item);
        }
    }

    @Override
    public List<ItemResponse> getItemsByIds(List<Long> ids) {
        return itemRepository.findByIdIn(ids)
                .stream()
                .map(itemMapper::toResponse)
                .toList();
    }
}
