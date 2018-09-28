package com.example.customer.order.endpoint.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import com.example.customer.order.dto.events.CustomerOrderDownloadEvent;
import com.example.customer.order.service.CustomerOrderService;
import com.example.customer.order.streams.CustomerOrderStreams;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomerOrderListener {
	@Autowired
	CustomerOrderService orderService;

	@StreamListener(target=CustomerOrderStreams.CUSTOMER_ORDERS_INPUT, condition = "headers['eventName']=='CustomerOrderDownloadEvent'")
	public void handleNewCustomerOrder(CustomerOrderDownloadEvent orderDownloadEvent) { // OrderCreationRequestDTO
																					// orderCreationRequestDTO) {
		log.info("Received CustomerOrderDownloadEvent Msg: {}" + ": at :" + new java.util.Date(), orderDownloadEvent);
		long startTime = System.currentTimeMillis();
		try {
			orderService.createOrder(orderDownloadEvent.getCustomerOrderCreationRequestDTO());
			long endTime = System.currentTimeMillis();
			log.info("Completed CustomerOrderDownloadEvent for : " + orderDownloadEvent + ": at :" + new java.util.Date()
					+ " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing CustomerOrderDownloadEvent for : " + orderDownloadEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}

/*	@StreamListener(target = CustomerOrderStreams.INVENTORY_OUTPUT, 
			condition = "headers['eventName']=='InventoryAllocatedEvent'")
	public void handleAllocatedInventoryEvent(InventoryAllocatedEvent inventoryAllocatedEvent) { 
		log.info("Received InventoryAllocatedEvent for: {}" + ": at :" + new java.util.Date(), inventoryAllocatedEvent);
		long startTime = System.currentTimeMillis();
		try {
			orderService.updateOrderLineStatusToReserved(
					CustomerOrderLineStatusUpdateDTOConverter.getOrderLineStatusUpdateDTO(inventoryAllocatedEvent));
			long endTime = System.currentTimeMillis();
			log.info("Completed InventoryAllocatedEvent for: " + inventoryAllocatedEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing InventoryAllocatedEvent for: " + inventoryAllocatedEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}*/

}
