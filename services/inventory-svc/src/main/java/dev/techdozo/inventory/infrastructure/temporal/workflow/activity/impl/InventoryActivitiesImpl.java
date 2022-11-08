package dev.techdozo.inventory.infrastructure.temporal.workflow.activity.impl;

import dev.techdozo.common.activities.InventoryActivities;
import dev.techdozo.common.error.ResourceNotFoundException;
import dev.techdozo.common.model.OrderDTO;
import dev.techdozo.inventory.application.domain.model.Inventory;
import dev.techdozo.inventory.application.domain.model.InventoryStatus;
import dev.techdozo.inventory.application.domain.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class InventoryActivitiesImpl implements InventoryActivities {

  private final InventoryRepository inventoryRepository;

  @Override
  public void reserveInventory(OrderDTO orderDTO) {

    log.info("Processing inventory for order {}", orderDTO.getOrderId());

    var inventory =
        Inventory.builder()
            .orderId(orderDTO.getOrderId())
            .productId(orderDTO.getProductId())
            .quantity(orderDTO.getQuantity())
            .inventoryStatus(InventoryStatus.RESERVED)
            .build();
    inventoryRepository.save(inventory);

    log.info("Finished processing inventory for order {}", orderDTO.getOrderId());
  }

  @Override
  public void releaseInventory(OrderDTO orderDTO) {
    log.info("Releasing inventory for order {}", orderDTO.getOrderId());
    var inventory =
            inventoryRepository
                    .getByOrderId(orderDTO.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order id not found"));
    inventory.setInventoryStatus(InventoryStatus.RELEASED);
    inventoryRepository.save(inventory);
  }
}
