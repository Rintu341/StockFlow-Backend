package org.sujan.stockflow_backend.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sujan.stockflow_backend.dto.DashboardSummaryResponse;
import org.sujan.stockflow_backend.entity.OrderStatus;
import org.sujan.stockflow_backend.entity.Role;
import org.sujan.stockflow_backend.repository.OrderRepository;
import org.sujan.stockflow_backend.repository.ProductRepository;
import org.sujan.stockflow_backend.repository.UserRepository;
import org.sujan.stockflow_backend.security.UserPrincipal;

@Service
public class DashboardService {

    private static final int RECENT_ORDERS_LIMIT = 5;

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public DashboardService(ProductRepository productRepository,
                            OrderRepository orderRepository,
                            UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long tenantId = principal.getTenantId();
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;

        return new DashboardSummaryResponse(
                productRepository.countByTenantId(tenantId),
                orderRepository.countByTenantIdAndStatus(tenantId, OrderStatus.PENDING),
                isAdmin ? userRepository.countByTenantId(tenantId) : null,
                orderRepository.findRecent(tenantId, PageRequest.of(0, RECENT_ORDERS_LIMIT))
        );
    }
}