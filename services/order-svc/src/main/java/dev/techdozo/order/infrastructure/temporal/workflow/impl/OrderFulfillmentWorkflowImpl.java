package dev.techdozo.order.infrastructure.temporal.workflow.impl;

import dev.techdozo.common.TaskQueue;
import dev.techdozo.common.activities.OrderActivities;
import dev.techdozo.common.activities.PaymentActivities;
import dev.techdozo.common.activities.InventoryActivities;
import dev.techdozo.common.activities.ShippingActivities;
import dev.techdozo.common.model.OrderDTO;
import dev.techdozo.order.infrastructure.temporal.workflow.OrderFulfillmentWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.activity.LocalActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class OrderFulfillmentWorkflowImpl implements OrderFulfillmentWorkflow {

  private final Logger logger = Workflow.getLogger(this.getClass().getName());

  private final ActivityOptions shippingActivityOptions =
      ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofMinutes(1))
          .setTaskQueue(TaskQueue.SHIPPING_ACTIVITY_TASK_QUEUE.name())
          .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
          .build();

  private final ActivityOptions paymentActivityOptions =
      ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofMinutes(1))
          .setTaskQueue(TaskQueue.PAYMENT_ACTIVITY_TASK_QUEUE.name())
          .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
          .build();
  private final ActivityOptions inventoryActivityOptions =
      ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofMinutes(1))
          .setTaskQueue(TaskQueue.INVENTORY_ACTIVITY_TASK_QUEUE.name())
          .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
          .build();
  private final LocalActivityOptions localActivityOptions =
      LocalActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofMinutes(1))
          .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(10).build())
          .build();

  private final OrderActivities orderActivities =
      Workflow.newLocalActivityStub(OrderActivities.class, localActivityOptions);

  private final PaymentActivities paymentActivities =
      Workflow.newActivityStub(PaymentActivities.class, paymentActivityOptions);

  private final InventoryActivities inventoryActivities =
      Workflow.newActivityStub(InventoryActivities.class, inventoryActivityOptions);

  private final ShippingActivities shippingActivities =
      Workflow.newActivityStub(ShippingActivities.class, shippingActivityOptions);

  @Override
  public void createOrder(OrderDTO orderDTO) {
    // Configure SAGA to run compensation activities in parallel
    Saga.Options sagaOptions = new Saga.Options.Builder().setParallelCompensation(true).build();
    Saga saga = new Saga(sagaOptions);
    try {
      paymentActivities.debitPayment(orderDTO);
      saga.addCompensation(paymentActivities::reversePayment, orderDTO);
      //Inventory
      inventoryActivities.reserveInventory(orderDTO);
      saga.addCompensation(inventoryActivities::releaseInventory, orderDTO);
      //Shipping
      shippingActivities.shipGoods(orderDTO);
      saga.addCompensation(shippingActivities::cancelShipment, orderDTO);
      //Order
      orderActivities.completeOrder(orderDTO);
      saga.addCompensation(orderActivities::failOrder, orderDTO);
    } catch (ActivityFailure cause) {
      saga.compensate();
      throw cause;
    }
  }
}
