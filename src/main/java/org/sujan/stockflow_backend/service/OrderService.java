package org.sujan.stockflow_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.sujan.stockflow_backend.dto.CreateOrderRequest;
import org.sujan.stockflow_backend.dto.OrderItemRequest;
import org.sujan.stockflow_backend.entity.*;
import org.sujan.stockflow_backend.exception.InvalidOrderStateException;
import org.sujan.stockflow_backend.exception.OrderNotFoundException;
import org.sujan.stockflow_backend.exception.ProductNotFoundException;
import org.sujan.stockflow_backend.repository.OrderRepository;
import org.sujan.stockflow_backend.repository.ProductRepository;
import org.sujan.stockflow_backend.security.UserPrincipal;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private Long getCurrentTenantId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return principal.getTenantId();
    }

    public Order createOrder(CreateOrderRequest request) {
        Long tenantId = getCurrentTenantId();
        Order order = new Order(tenantId);

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(
                            "Product not found with id: " + itemRequest.getProductId()));
            if (!product.getTenantId().equals(tenantId)) {
                throw new ProductNotFoundException(
                        "Product not found with id: " + itemRequest.getProductId());
            }
            OrderItem item = new OrderItem(order, product, itemRequest.getQuantity());
            order.getItems().add(item);
        }
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByTenantId(getCurrentTenantId());
    }

    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        if (!order.getTenantId().equals(getCurrentTenantId())) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
        return order;
    }

    public Order approveOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Only pending orders can be approved");
        }

        // Deduct stock at approval time
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new InvalidOrderStateException(
                        "Insufficient stock for product: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.APPROVED);
        return orderRepository.save(order);
    }

    public Order rejectOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Only pending orders can be rejected");
        }
        order.setStatus(OrderStatus.REJECTED);
        return orderRepository.save(order);
    }

    public Order markPacked(Long id) {
        System.out.println("id: "+ id);
        Order order = getOrderById(id);
        if (order.getStatus() != OrderStatus.APPROVED) {
            throw new InvalidOrderStateException("Only approved orders can be packed");
        }
        order.setStatus(OrderStatus.PACKED);
        return orderRepository.save(order);
    }

    public Order markShipped(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() != OrderStatus.PACKED) {
            throw new InvalidOrderStateException("Only packed orders can be shipped");
        }
        order.setStatus(OrderStatus.SHIPPED);
        return orderRepository.save(order);
    }
}