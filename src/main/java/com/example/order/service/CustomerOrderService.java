package com.example.order.service;

import com.example.order.dto.requests.CustomerOrderCreationRequestDTO;
import com.example.order.dto.requests.CustomerOrderLineStatusUpdateRequestDTO;
import com.example.order.dto.requests.CustomerOrderUpdateRequestDTO;
import com.example.order.dto.responses.CustomerOrderDTO;

public interface CustomerOrderService {
	public CustomerOrderDTO findById(String busName, Integer locnNbr, Long id) throws Exception;
	public CustomerOrderDTO createOrder(CustomerOrderCreationRequestDTO orderCreationReq) throws Exception;
	public CustomerOrderDTO updateOrder(CustomerOrderUpdateRequestDTO orderUpdRequest) throws Exception;
	public CustomerOrderDTO updateOrderLineStatusToReserved(CustomerOrderLineStatusUpdateRequestDTO orderStatusUpdReq) throws Exception;
}