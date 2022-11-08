package dev.techdozo.common.activities;

import dev.techdozo.common.model.OrderDTO;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface PaymentActivities {
  void debitPayment(OrderDTO orderDTO);
  void reversePayment(OrderDTO orderDTO);
}
