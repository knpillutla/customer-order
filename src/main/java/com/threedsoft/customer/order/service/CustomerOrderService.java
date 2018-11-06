package com.threedsoft.customer.order.service;

import java.util.List;

import com.threedsoft.customer.order.dto.requests.CustomerOrderCreationRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderLineUpdateRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderSearchRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderUpdateRequestDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderResourceDTO;

public interface CustomerOrderService {
	public CustomerOrderResourceDTO createOrder(CustomerOrderCreationRequestDTO orderCreationReq) throws Exception;

	public CustomerOrderResourceDTO updateOrder(CustomerOrderUpdateRequestDTO orderUpdRequest) throws Exception;

	public CustomerOrderResourceDTO deleteOrder(Long id) throws Exception;

	List<CustomerOrderResourceDTO> findByBusNameAndLocnNbr(String busName, Integer locnNbr) throws Exception;

	List<CustomerOrderResourceDTO> searchOrder(CustomerOrderSearchRequestDTO orderSearchRequestDTO);

	CustomerOrderResourceDTO findById(String busName, Integer locnNbr, Long id, Boolean isRetrieveDetails)
			throws Exception;
}