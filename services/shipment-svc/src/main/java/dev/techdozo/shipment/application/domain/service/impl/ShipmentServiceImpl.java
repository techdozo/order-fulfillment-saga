package dev.techdozo.shipment.application.domain.service.impl;

import dev.techdozo.common.error.ServiceException;
import dev.techdozo.shipment.application.domain.service.ShipmentService;
import dev.techdozo.shipment.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/** Mock implementation to represent call to external service to initiate shipping */
@Slf4j
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

  private final ApplicationProperties applicationProperties;

  @SneakyThrows
  @Override
  public String shipGoods(Double quantity) {
    // Simulate Error condition
    if (applicationProperties.isSimulateError() ) {
      log.error("Error occurred while shipping..");
      throw new ServiceException("Error executing Service");
    }
    UUID uuid = UUID.randomUUID();
    Thread.sleep(2000);
    return uuid.toString();
  }
}
