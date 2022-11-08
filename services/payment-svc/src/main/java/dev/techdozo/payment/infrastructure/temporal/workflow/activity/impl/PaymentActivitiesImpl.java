package dev.techdozo.payment.infrastructure.temporal.workflow.activity.impl;

import dev.techdozo.common.activities.PaymentActivities;
import dev.techdozo.common.error.ResourceNotFoundException;
import dev.techdozo.common.model.OrderDTO;
import dev.techdozo.payment.application.domain.model.Payment;
import dev.techdozo.payment.application.domain.model.PaymentStatus;
import dev.techdozo.payment.application.domain.repository.PaymentRepository;
import dev.techdozo.payment.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PaymentActivitiesImpl implements PaymentActivities {

  private final PaymentService paymentService;
  private final PaymentRepository paymentRepository;

  @Override
  public void debitPayment(OrderDTO orderDTO) {
    log.info("Processing payment for order {}", orderDTO.getOrderId());
    double amount = orderDTO.getQuantity() * orderDTO.getPrice();
    // Call external Payment service such as Stripe
    var externalPaymentId = paymentService.debit(amount);
    // Create domain object
    var payment =
        Payment.builder()
            .externalId(externalPaymentId)
            .orderId(orderDTO.getOrderId())
            .productId(orderDTO.getProductId())
            .amount(amount)
            .paymentStatus(PaymentStatus.ACTIVE)
            .build();
    paymentRepository.save(payment);
  }

  @Override
  public void reversePayment(OrderDTO orderDTO) {
    log.info("Reversing payment for order {}", orderDTO.getOrderId());
    var payment =
        paymentRepository
            .getByOrderId(orderDTO.getOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Order id not found"));
    payment.setPaymentStatus(PaymentStatus.REVERSED);
    paymentRepository.save(payment);
  }
}
