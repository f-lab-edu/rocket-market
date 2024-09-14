package flab.rocket_market.orders.repository;

import flab.rocket_market.orders.entity.Inventory;
import flab.rocket_market.products.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Inventory findByProducts(Products products);
}
