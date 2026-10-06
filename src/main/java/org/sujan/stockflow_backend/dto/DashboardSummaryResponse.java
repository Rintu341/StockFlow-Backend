package org.sujan.stockflow_backend.dto;

import java.util.List;

public record DashboardSummaryResponse(
        long totalProducts,
        long pendingOrders,
        Long teamMembers,
        List<RecentOrderDto> recentOrders
) {}