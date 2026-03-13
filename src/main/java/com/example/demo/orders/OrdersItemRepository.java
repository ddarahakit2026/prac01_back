package com.example.demo.orders;

import com.example.demo.orders.model.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersItemRepository  extends JpaRepository<OrdersItem, Long> {
}
