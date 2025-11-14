//package com.example.AutoDetail.controller.api;
//
//import com.example.AutoDetail.dto.ItemDto;
//import com.example.AutoDetail.entity.Item;
//import com.example.AutoDetail.service.AdminService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * REST API для управления товарами
// * Базовый URL: /api/v1/items
// */
//// @RestController
//// @RequestMapping("/api/v1/items")
//public class ItemApiController {
//
//    private final AdminService adminService;
//
//    public ItemApiController(AdminService adminService) {
//        this.adminService = adminService;
//    }
//
//    // @GetMapping
//    public ResponseEntity<List<ItemDto>> getAllItems() {
//        try {
//            List<ItemDto> items = adminService.getAllItems().stream()
//                    .map(this::convertToDto)
//                    .collect(Collectors.toList());
//            return ResponseEntity.ok(items);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    // @GetMapping("/{id}")
//    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id) {
//        try {
//            return adminService.getItemById(id)
//                    .map(this::convertToDto)
//                    .map(ResponseEntity::ok)
//                    .orElse(ResponseEntity.notFound().build());
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    // @PostMapping
//    public ResponseEntity<ItemDto> createItem(@RequestBody ItemDto itemDto) {
//        try {
//            Item item = convertToEntity(itemDto);
//            Item savedItem = adminService.saveItem(item, itemDto.getSupplierId());
//            return ResponseEntity.ok(convertToDto(savedItem));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    // @PutMapping("/{id}")
//    public ResponseEntity<ItemDto> updateItem(@PathVariable Long id, @RequestBody ItemDto itemDto) {
//        try {
//            itemDto.setId(id);
//            Item item = convertToEntity(itemDto);
//            Item updatedItem = adminService.saveItem(item, itemDto.getSupplierId());
//            return ResponseEntity.ok(convertToDto(updatedItem));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    // @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
//        try {
//            adminService.deleteItem(id);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    // @GetMapping("/search")
//    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String name) {
//        try {
//            List<ItemDto> items = adminService.searchItemsByName(name).stream()
//                    .map(this::convertToDto)
//                    .collect(Collectors.toList());
//            return ResponseEntity.ok(items);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    private ItemDto convertToDto(Item item) {
//        return new ItemDto(
//                item.getId(),
//                item.getArctical(),
//                item.getName(),
//                java.math.BigDecimal.valueOf(item.getPrice()),
//                item.getQuantity(),
//                item.getImage(),
//                item.getSupplier() != null ? item.getSupplier().getId() : null,
//                item.getSupplier() != null ? item.getSupplier().getName() : null
//        );
//    }
//
//    private Item convertToEntity(ItemDto itemDto) {
//        Item item = new Item();
//        item.setId(itemDto.getId());
//        item.setArctical(itemDto.getArctical());
//        item.setName(itemDto.getName());
//        item.setPrice(itemDto.getPrice().doubleValue());
//        item.setQuantity(itemDto.getQuantity());
//        item.setImage(itemDto.getImage());
//        return item;
//    }
//}