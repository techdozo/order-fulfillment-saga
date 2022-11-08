package dev.techdozo.payment.persistence.repository;

import dev.techdozo.payment.application.domain.model.Payment;
import dev.techdozo.payment.application.domain.repository.PaymentRepository;
import dev.techdozo.payment.persistence.mapper.PaymentPersistableMapper;
import dev.techdozo.payment.persistence.repository.jpa.PaymentJpaRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class PaymentRepositoryImpl implements PaymentRepository {

  private final PaymentJpaRepository paymentJpaRepository;

  public PaymentRepositoryImpl(PaymentJpaRepository paymentJpaRepository) {
    this.paymentJpaRepository = paymentJpaRepository;
  }

  @Override
  public Payment save(Payment payment) {
    log.info("Saving Payment");
    var paymentPersistable = PaymentPersistableMapper.MAPPER.map(payment);
    paymentPersistable = paymentJpaRepository.save(paymentPersistable);

    log.info("Payment saved, id {}", paymentPersistable.getId());
    return PaymentPersistableMapper.MAPPER.map(paymentPersistable);
  }

  @Override
  public List<Payment> getAll() {
    log.info("Getting all payments from DB..");
    var paymentPersistables = paymentJpaRepository.findAll();
    return paymentPersistables.stream().map(PaymentPersistableMapper.MAPPER::map).toList();
  }

  @Override
  public Optional<Payment> getByOrderId(Long orderId) {
    return paymentJpaRepository.findByOrderId(orderId).stream()
        .map(PaymentPersistableMapper.MAPPER::map)
        .findFirst();
  }
}
