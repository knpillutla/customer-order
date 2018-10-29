package com.threedsoft.customer.order.service;

import java.util.List;

import com.threedsoft.customer.order.dto.requests.CustomerOrderCreationRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderLineCreationRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderLineUpdateRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderUpdateRequestDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderLineResourceDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderResourceDTO;

public interface CustomerOrderDtlService {
	public CustomerOrderLineResourceDTO findById(String busName, Integer locnNbr, Long orderId, Long id) throws Exception;
	public CustomerOrderLineResourceDTO createOrderLine(CustomerOrderLineCreationRequestDTO orderLineCreationReq) throws Exception;
	public CustomerOrderLineResourceDTO updateOrderLine(CustomerOrderLineUpdateRequestDTO orderLineUpdRequest) throws Exception;
	public CustomerOrderLineResourceDTO deleteOrderLine(Long id) throws Exception;
	public List<CustomerOrderLineResourceDTO> findByBusNameAndLocnNbrAndOrderId(String busName, Integer locnNbr, Long orderId) throws Exception;
	//public CustomerOrderResourceDTO updateOrderLineStatusToReserved(CustomerOrderLineUpdateRequestDTO orderStatusUpdReq) throws Exception;
}