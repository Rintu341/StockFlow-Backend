package org.sujan.stockflow_backend.dto;

import org.sujan.stockflow_backend.entity.OrderStatus;
import java.time.Instant;

public record RecentOrderDto(
        Long id,
        String customerName,
        int itemsCount,
        Instant createdAt,
        OrderStatus status
) {}