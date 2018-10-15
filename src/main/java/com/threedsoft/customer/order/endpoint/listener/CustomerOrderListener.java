package com.threedsoft.customer.order.endpoint.listener;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import com.threedsoft.customer.order.dto.events.CustomerOrderDownloadEvent;
import com.threedsoft.customer.order.dto.requests.CustomerOrderCreationRequestDTO;
import com.threedsoft.customer.order.service.CustomerOrderService;
import com.threedsoft.customer.order.streams.CustomerOrderStreams;
import com.threedsoft.util.dto.events.EventResourceConverter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomerOrderListener {
	@Autowired
	CustomerOrderService orderService;

	@StreamListener(target = CustomerOrderStreams.CUSTOMER_ORDERS_INPUT, condition = "headers['eventName']=='CustomerOrderDownloadEvent'")
	public void handleNewCustomerOrder(CustomerOrderDownloadEvent orderDownloadEvent) { // OrderCreationRequestDTO
		// orderCreationRequestDTO) {
		log.info("Received CustomerOrderDownloadEvent Msg: {}" + ": at :" + LocalDateTime.now(), orderDownloadEvent);
		long startTime = System.currentTimeMillis();
		try {

			orderService.createOrder((CustomerOrderCreationRequestDTO) EventResourceConverter
					.getObject(orderDownloadEvent.getEventResource(), orderDownloadEvent.getEventResourceClassName()));
			long endTime = System.currentTimeMillis();
			log.info("Completed CustomerOrderDownloadEvent for : " + orderDownloadEvent + ": at :" + LocalDateTime.now()
					+ " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing CustomerOrderDownloadEvent for : " + orderDownloadEvent + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}

	/*
	 * @StreamListener(target = CustomerOrderStreams.INVENTORY_OUTPUT, condition =
	 * "headers['eventName']=='InventoryAllocatedEvent'") public void
	 * handleAllocatedInventoryEvent(InventoryAllocatedEvent
	 * inventoryAllocatedEvent) {
	 * log.info("Received InventoryAllocatedEvent for: {}" + ": at :" +
	 * LocalDateTime.now(), inventoryAllocatedEvent); long startTime =
	 * System.currentTimeMillis(); try {
	 * orderService.updateOrderLineStatusToReserved(
	 * CustomerOrderLineStatusUpdateDTOConverter.getOrderLineStatusUpdateDTO(
	 * inventoryAllocatedEvent)); long endTime = System.currentTimeMillis();
	 * log.info("Completed InventoryAllocatedEvent for: " + inventoryAllocatedEvent
	 * + ": at :" + LocalDateTime.now() + " : total time:" + (endTime - startTime) /
	 * 1000.00 + " secs"); } catch (Exception e) { e.printStackTrace(); long endTime
	 * = System.currentTimeMillis();
	 * log.error("Error Completing InventoryAllocatedEvent for: " +
	 * inventoryAllocatedEvent + ": at :" + LocalDateTime.now() + " : total time:" +
	 * (endTime - startTime) / 1000.00 + " secs", e); } }
	 */

}
