package dev.techdozo.order.infrastructure.temporal.workflow.activity.impl;

import dev.techdozo.common.activities.OrderActivities;
import dev.techdozo.common.model.OrderDTO;
import dev.techdozo.order.application.domain.model.Order;
import dev.techdozo.order.application.domain.model.OrderStatus;
import dev.techdozo.order.application.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OrderActivitiesImpl implements OrderActivities {

  private final OrderRepository orderRepository;

  @Override
  public void completeOrder(OrderDTO orderDTO) {
    log.info("Marking order as completed, order id {}", orderDTO.getOrderId());
    var order = map(orderDTO);
    order.setOrderStatus(OrderStatus.COMPLETED);
    var completedOrder = orderRepository.save(order);
    log.info("Order completed, {}", completedOrder);
  }

  @Override
  public void failOrder(OrderDTO orderDTO) {
    var order = orderRepository.get(orderDTO.getOrderId());
    order.setOrderStatus(OrderStatus.FAILED);
    orderRepository.save(order);
  }

  private Order map(OrderDTO orderDTO) {
    var order = new Order();
    order.setOrderId(orderDTO.getOrderId());
    order.setProductId(orderDTO.getProductId());
    order.setPrice(orderDTO.getPrice());
    order.setQuantity(orderDTO.getQuantity());
    return order;
  }
}
