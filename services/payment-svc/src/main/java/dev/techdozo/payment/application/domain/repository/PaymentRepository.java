package dev.techdozo.payment.application.domain.repository;

import dev.techdozo.payment.application.domain.model.Payment;

import java.util.List;
import java.util.Optional;

/** Domain repository for the Payment. */
public interface PaymentRepository {
  Payment save(Payment payment);

  List<Payment> getAll();

  Optional<Payment> getByOrderId(Long orderId);
}
