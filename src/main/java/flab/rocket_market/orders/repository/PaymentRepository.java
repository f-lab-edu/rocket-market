package flab.rocket_market.orders.repository;

import flab.rocket_market.orders.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
