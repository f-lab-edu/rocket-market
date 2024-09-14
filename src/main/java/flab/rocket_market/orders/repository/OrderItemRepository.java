package flab.rocket_market.orders.repository;

import flab.rocket_market.orders.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItems, Long> {
}
