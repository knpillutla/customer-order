package com.threedsoft.customer.order.endpoint.rest;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.threedsoft.customer.order.dto.events.CustomerOrderLineCreationFailedEvent;
import com.threedsoft.customer.order.dto.events.CustomerOrderLineUpdateFailedEvent;
import com.threedsoft.customer.order.dto.requests.CustomerOrderLineCreationRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderLineUpdateRequestDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderLineResourceDTO;
import com.threedsoft.customer.order.service.CustomerOrderDtlService;
import com.threedsoft.customer.order.util.CustomerOrderConstants;
import com.threedsoft.util.dto.ErrorResourceDTO;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/customer-orders/v1")
@Api(value = "Customer Order Dtl Service", description = "Operations pertaining to Customer Order Details")
@RefreshScope
@Slf4j
public class CustomerOrderDtlRestEndPoint {

	@Autowired
	CustomerOrderDtlService orderDtlService;

	@Value("${wms.service.health.msg: Customer Order Detail Service - Config Server is not working..please check}")
	private String healthMsg;

	@Value("${wms.service.ready.msg: Customer Order Detail Service - Not ready yet}")
	private String readyMsg;

	@GetMapping("/{busName}/{locnNbr}/order/{id}/dtl/{dtlid}")
	public ResponseEntity getById(@PathVariable("busName") String busName, @PathVariable("locnNbr") Integer locnNbr,
			@PathVariable("id") Long id, @PathVariable("dtlid") Long dtlId) throws IOException {
		try {
			return ResponseEntity.ok(orderDtlService.findById(busName, locnNbr, id, dtlId));
		} catch (Exception e) {
			log.error("Error Occured for busName:" + busName + ", id:" + id + " : " + e.getMessage());
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured for getting order line for busName:" + busName + ",locnNbr:" + locnNbr
									+ ", id:" + id + ",dtlId:" + dtlId + " : " + e.getMessage()));
		}
	}

	@GetMapping("/{busName}/{locnNbr}/order/{id}/dtl")
	public ResponseEntity getOrderLineList(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr, @PathVariable("id") Long orderId) throws IOException {
		try {
			log.info("Received GET request for customer order dtls for order id:" + orderId);
			return ResponseEntity.ok(orderDtlService.findByBusNameAndLocnNbrAndOrderId(busName, locnNbr, orderId));
		} catch (Exception e) {
			log.error("Error Occured for busName:" + busName + ", locnNbr:" + locnNbr + " : " + e.getMessage());
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured for getting order line list for busName:" + busName + ",locnNbr:" + locnNbr
									+ ",orderId:" + orderId + " : " + e.getMessage()));
		}
	}

	@PostMapping("/{busName}/{locnNbr}/order/{id}/dtl/{dtlid}")
	public ResponseEntity updateOrderLine(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr, @PathVariable("id") Long orderId,
			@PathVariable("dtlid") Long orderDtlId, @RequestBody CustomerOrderLineUpdateRequestDTO orderLineUpdateReq)
			throws IOException {
		try {
			return ResponseEntity.ok(orderDtlService.updateOrderLine(orderLineUpdateReq));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured while updatine order line for busName:" + busName + ",locnNbr:" + locnNbr
							+ ",orderId:" + orderId + ",orderDtlId:" + orderDtlId + " : " + e.getMessage(), orderLineUpdateReq));
		}
	}

	@DeleteMapping("/{busName}/{locnNbr}/order/{id}/dtl/{dtlid}")
	public ResponseEntity deleteOrderLine(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr, @PathVariable("id") Long id, @PathVariable("dtlid") Long dtlId)
			throws IOException {
		CustomerOrderLineResourceDTO outpout = null;
		try {
			log.info("Received delete customer order line request for order id:" + id + ", orderLineId:" + dtlId);
			outpout = orderDtlService.deleteOrderLine(dtlId);
			log.info("Completed delete customer order line request for order id:" + id + ", orderLineId:" + dtlId);
		} catch (Exception ex) {
			log.error("Order line Delete Error:", ex);
			return ResponseEntity.badRequest().body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occured while deleting order line for busName:" + busName + ",locnNbr:" + locnNbr
					+ ",orderId:" + id + ",orderDtlId:" + dtlId + " : " + ex.getMessage()));
		}
		return ResponseEntity.ok(outpout);
	}

	@PostMapping("/{busName}/{locnNbr}/order/{id}/dtl")
	public ResponseEntity createOrderLine(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr, @PathVariable("id") Long id,
			@RequestBody CustomerOrderLineCreationRequestDTO orderLineCreationReq) throws IOException {
		long startTime = System.currentTimeMillis();
		log.info("Received Order Line Create request for : " + orderLineCreationReq.toString() + ": at :"
				+ LocalDateTime.now());
		ResponseEntity resEntity = null;
		try {
			resEntity = ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
					.body(orderDtlService.createOrderLine(orderLineCreationReq));
		} catch (Exception e) {
			e.printStackTrace();
			resEntity = ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured while creating order line for busName:" + busName + ",locnNbr:" + locnNbr
							+ ",orderId:" + id + " : " + e.getMessage(),orderLineCreationReq));
		}
		long endTime = System.currentTimeMillis();
		log.info("Completed Order Line Create request for : " + orderLineCreationReq.toString() + ": at :"
				+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		return resEntity;
	}
}
