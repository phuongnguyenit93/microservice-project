package com.phuong.inventoryservice.service;

import com.phuong.inventoryservice.dto.InventoryResponse;
import com.phuong.inventoryservice.model.Inventory;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.phuong.inventoryservice.repository.InventoryRepository;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List <String> skuCode) {
        Thread.sleep(5000);
        return inventoryRepository.findBySkuCodeIn(skuCode)
                .stream()
                .map(inventory ->
                        InventoryResponse.builder()
                            .skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity() > 0)
                            .build())
                .toList();
    }
}
