package dev.techdozo.inventory.application.domain.repository;

import dev.techdozo.inventory.application.domain.model.Inventory;

import java.util.List;
import java.util.Optional;

/** Domain repository for the Inventory. */
public interface InventoryRepository {
  Inventory save(Inventory inventory);

  List<Inventory> getAll();

  Optional<Inventory> getByOrderId(Long orderId);
}
