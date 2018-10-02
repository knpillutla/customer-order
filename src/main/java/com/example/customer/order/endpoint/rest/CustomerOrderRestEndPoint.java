package com.example.customer.order.endpoint.rest;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.customer.order.dto.events.CustomerOrderCreationFailedEvent;
import com.example.customer.order.dto.events.CustomerOrderUpdateFailedEvent;
import com.example.customer.order.dto.requests.CustomerOrderCreationRequestDTO;
import com.example.customer.order.dto.requests.CustomerOrderUpdateRequestDTO;
import com.example.customer.order.service.CustomerOrderService;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/customer-orders/v1")
@Api(value="Customer Order Service", description="Operations pertaining to Customer Orders")
@RefreshScope
@Slf4j
public class CustomerOrderRestEndPoint {

    @Autowired
    CustomerOrderService orderService;
	
    @Value("${wms.service.health.msg: Customer Order Service - Config Server is not working..please check}")
    private String healthMsg;
    
    @Value("${wms.service.ready.msg: Customer Order Service - Not ready yet}")
    private String readyMsg;

	@GetMapping("/ready")
	public ResponseEntity ready() throws Exception {
		return ResponseEntity.ok(readyMsg);
	}
	
	@GetMapping("/health")
	public ResponseEntity health() throws Exception {
		return ResponseEntity.ok(healthMsg);
	}

	@GetMapping("/{busName}/{locnNbr}/order/{id}")
	public ResponseEntity getById(@PathVariable("busName") String busName, @PathVariable("locnNbr") Integer locnNbr, @PathVariable("id") Long id) throws IOException {
		try {
			return ResponseEntity.ok(orderService.findById(busName, locnNbr, id));
		} catch (Exception e) {
			log.error("Error Occured for busName:" + busName + ", id:" + id + " : " + e.getMessage());
			return ResponseEntity.badRequest().body(new ErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error Occured for GET request busName:" + busName + ", id:" + id + " : " + e.getMessage()));
		}
	}

	@PostMapping("/{busName}/{locnNbr}/order/{id}")
	public ResponseEntity updateOrder(@PathVariable("busName") String busName, @PathVariable("locnNbr") Integer locnNbr, @RequestBody CustomerOrderUpdateRequestDTO orderUpdateReq) throws IOException {
		try {
			return ResponseEntity.ok(orderService.updateOrder(orderUpdateReq));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new CustomerOrderUpdateFailedEvent(orderUpdateReq, "Error Occured while processing request:" + e.getMessage()));
		}
	}	

	@PutMapping("/{busName}/{locnNbr}/order")
	public ResponseEntity createOrder(@PathVariable("busName") String busName, @PathVariable("locnNbr") Integer locnNbr, @RequestBody CustomerOrderCreationRequestDTO orderCreationReq) throws IOException {
		long startTime = System.currentTimeMillis();
		log.info("Received Order Create request for : " + orderCreationReq.toString() + ": at :" + LocalDateTime.now());
		ResponseEntity resEntity = null;
		try {
			resEntity = ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(orderService.createOrder(orderCreationReq));
		} catch (Exception e) {
			e.printStackTrace();
			resEntity = ResponseEntity.badRequest().body(new CustomerOrderCreationFailedEvent(orderCreationReq, "Error Occured while processing Inventory Create request:" + e.getMessage()));
		}
		long endTime = System.currentTimeMillis();
		log.info("Completed Order Create request for : " + orderCreationReq.toString() + ": at :" + LocalDateTime.now() + " : total time:" + (endTime-startTime)/1000.00 + " secs");
		return resEntity;
	}	
}
