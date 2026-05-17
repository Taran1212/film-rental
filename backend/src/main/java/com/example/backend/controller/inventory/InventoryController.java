package com.example.backend.controller.inventory;

import com.example.backend.dto.InventoryDto;
import com.example.backend.service.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('STAFF')")
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/api/movies/inventory")
    public Page<InventoryDto> getStoreInventory(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return inventoryService.getStoreInventory(search, PageRequest.of(page, size));
    }

    @GetMapping("/api/movies/{id}/inventory")
    public InventoryDto getInventory(@PathVariable Integer id) {
        return inventoryService.getInventory(id);
    }

    @GetMapping("/api/movies/{id}/inventory/next-available")
    public Map<String, Object> getNextAvailableInventory(@PathVariable Integer id) {
        Integer invId = inventoryService.findFirstAvailableInventoryId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("inventoryId", invId);
        result.put("available", invId != null);
        return result;
    }
}
