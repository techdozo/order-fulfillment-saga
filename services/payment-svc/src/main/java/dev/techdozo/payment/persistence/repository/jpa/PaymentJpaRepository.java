package dev.techdozo.payment.persistence.repository.jpa;

import dev.techdozo.payment.persistence.repository.model.PaymentPersistable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentJpaRepository extends JpaRepository<PaymentPersistable, Long> {
  List<PaymentPersistable> findByOrderId(Long orderId);
}
