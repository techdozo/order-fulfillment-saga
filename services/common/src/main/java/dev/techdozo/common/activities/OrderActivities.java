package dev.techdozo.common.activities;

import dev.techdozo.common.model.OrderDTO;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface OrderActivities {
  void completeOrder(OrderDTO order);
  void failOrder(OrderDTO orderDTO);
}
