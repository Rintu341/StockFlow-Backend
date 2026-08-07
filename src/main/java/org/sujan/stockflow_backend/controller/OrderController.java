package org.sujan.stockflow_backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.sujan.stockflow_backend.dto.CreateOrderRequest;
import org.sujan.stockflow_backend.dto.OrderResponse;
import org.sujan.stockflow_backend.entity.Order;
import org.sujan.stockflow_backend.service.OrderService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.ok(new OrderResponse(order));
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return new OrderResponse(orderService.getOrderById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}/approve")
    public OrderResponse approveOrder(@PathVariable Long id) {
        return new OrderResponse(orderService.approveOrder(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}/reject")
    public OrderResponse rejectOrder(@PathVariable Long id) {
        return new OrderResponse(orderService.rejectOrder(id));
    }

    @PutMapping("/{id}/pack")
    public OrderResponse markPacked(@PathVariable Long id) {
        return new OrderResponse(orderService.markPacked(id));
    }

    @PutMapping("/{id}/ship")
    public OrderResponse markShipped(@PathVariable Long id) {
        return new OrderResponse(orderService.markShipped(id));
    }
}