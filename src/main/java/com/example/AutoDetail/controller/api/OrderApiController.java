package com.example.AutoDetail.controller.api;

import com.example.AutoDetail.dto.OrderDto;
import com.example.AutoDetail.entity.Order;
import com.example.AutoDetail.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API для управления заказами
 * Базовый URL: /api/v1/orders
 */
// @RestController
// @RequestMapping("/api/v1/orders")
public class OrderApiController {

    private final ManagerService managerService;

    public OrderApiController(ManagerService managerService) {
        this.managerService = managerService;
    }

    // @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        try {
            List<OrderDto> orders = managerService.getAllOrders().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        try {
            return managerService.getOrderById(id)
                    .map(this::convertToDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        try {
            Order order = convertToEntity(orderDto);
            Order savedOrder = managerService.saveOrder(order);
            return ResponseEntity.ok(convertToDto(savedOrder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        try {
            orderDto.setId(id);
            Order order = convertToEntity(orderDto);
            Order updatedOrder = managerService.saveOrder(order);
            return ResponseEntity.ok(convertToDto(updatedOrder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            managerService.deleteOrder(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // @GetMapping("/client/{clientId}")
    public ResponseEntity<List<OrderDto>> getOrdersByClient(@PathVariable Long clientId) {
        try {
            List<OrderDto> orders = managerService.getOrdersByClientId(clientId).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // @GetMapping("/status/{statusId}")
    public ResponseEntity<List<OrderDto>> getOrdersByStatus(@PathVariable Long statusId) {
        try {
            List<OrderDto> orders = managerService.getOrdersByStatus(statusId).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private OrderDto convertToDto(Order order) {
        // Здесь нужно добавить логику для получения имен клиента, пользователя и статуса
        return new OrderDto(
                order.getId(),
                order.getClientId(),
                "Client Name", // Нужно получить из сервиса
                order.getUserId(),
                "User Name", // Нужно получить из сервиса
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getStatusId(),
                "Status Name" // Нужно получить из сервиса
        );
    }

    private Order convertToEntity(OrderDto orderDto) {
        Order order = new Order();
        order.setId(orderDto.getId());
        order.setClientId(orderDto.getClientId());
        order.setUserId(orderDto.getUserId());
        order.setTotalAmount(orderDto.getTotalAmount());
        order.setCreatedAt(orderDto.getCreatedAt());
        order.setStatusId(orderDto.getStatusId());
        return order;
    }
}