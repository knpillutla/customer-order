package com.threedsoft.customer.order.service;

import com.threedsoft.customer.order.dto.requests.CustomerOrderCreationRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderLineStatusUpdateRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderUpdateRequestDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderResourceDTO;

public interface CustomerOrderService {
	public CustomerOrderResourceDTO findById(String busName, Integer locnNbr, Long id) throws Exception;
	public CustomerOrderResourceDTO createOrder(CustomerOrderCreationRequestDTO orderCreationReq) throws Exception;
	public CustomerOrderResourceDTO updateOrder(CustomerOrderUpdateRequestDTO orderUpdRequest) throws Exception;
	public CustomerOrderResourceDTO updateOrderLineStatusToReserved(CustomerOrderLineStatusUpdateRequestDTO orderStatusUpdReq) throws Exception;
}