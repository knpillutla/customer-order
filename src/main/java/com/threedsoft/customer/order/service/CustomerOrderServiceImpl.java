package com.threedsoft.customer.order.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.threedsoft.customer.order.db.CustomerOrder;
import com.threedsoft.customer.order.db.CustomerOrderLine;
import com.threedsoft.customer.order.db.CustomerOrderLineRepository;
import com.threedsoft.customer.order.db.CustomerOrderRepository;
import com.threedsoft.customer.order.dto.converter.CustomerOrderDTOConverter;
import com.threedsoft.customer.order.dto.events.CustomerOrderCreatedEvent;
import com.threedsoft.customer.order.dto.events.CustomerOrderCreationFailedEvent;
import com.threedsoft.customer.order.dto.events.CustomerOrderUpdateFailedEvent;
import com.threedsoft.customer.order.dto.requests.CustomerOrderCreationRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderSearchRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderUpdateRequestDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderResourceDTO;
import com.threedsoft.customer.order.util.CustomerOrderConstants;
import com.threedsoft.util.service.EventPublisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {
	@Autowired
	CustomerOrderRepository orderDAO;

	@Autowired
	EventPublisher eventPublisher;

	@Autowired
	CustomerOrderDTOConverter orderDTOConverter;

	@Autowired
	CustomerOrderDtlService orderDtlService;

	@Override
	@Transactional
	public CustomerOrderResourceDTO updateOrder(CustomerOrderUpdateRequestDTO orderUpdateRequestDTO) throws Exception {
		CustomerOrderResourceDTO orderDTO = null;
		try {
			Optional<CustomerOrder> orderOptional = orderDAO.findById(orderUpdateRequestDTO.getId());
			if (!orderOptional.isPresent()) {
				throw new Exception("Order Update Failed. Order Not found to update");
			}
			CustomerOrder orderEntity = orderOptional.get();
			orderDTOConverter.updateOrderEntity(orderEntity, orderUpdateRequestDTO);
			orderDTO = orderDTOConverter.getOrderDTO(orderDAO.save(orderEntity));
		} catch (Exception ex) {
			log.error("Created Order Error:" + ex.getMessage(), ex);
			eventPublisher.publish(
					new CustomerOrderUpdateFailedEvent(orderUpdateRequestDTO, CustomerOrderConstants.CUSTOMER_ORDER_SERVICE_NAME, "Update Order Error:" + ex.getMessage()));
			throw ex;
		}
		return orderDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	@Transactional
	public CustomerOrderResourceDTO createOrder(CustomerOrderCreationRequestDTO orderCreationRequestDTO) throws Exception {
		CustomerOrderResourceDTO orderResponseDTO = null;
		try {
			CustomerOrder order = orderDTOConverter.getOrderEntity(orderCreationRequestDTO);
			CustomerOrder savedOrderObj = orderDAO.save(order);
			orderResponseDTO = orderDTOConverter.getOrderDTO(savedOrderObj);
			eventPublisher.publish(new CustomerOrderCreatedEvent(orderResponseDTO, CustomerOrderConstants.CUSTOMER_ORDER_SERVICE_NAME));
		} catch (Exception ex) {
			log.error("Customer Order Creation Error:" + ex.getMessage(), ex);
			eventPublisher.publish(
					new CustomerOrderCreationFailedEvent(orderCreationRequestDTO, CustomerOrderConstants.CUSTOMER_ORDER_SERVICE_NAME, "Customer Order Creation Error:" + ex.getMessage()));
			throw ex;
		}
		return orderResponseDTO;
	}

	@Override
	public CustomerOrderResourceDTO findById(String busName, Integer locnNbr, Long id, Boolean isRetrieveDetails) throws Exception {
		CustomerOrder orderEntity = orderDAO.findById(busName, locnNbr, id);
		if(isRetrieveDetails) orderEntity.getOrderLines();
		return orderDTOConverter.getOrderDTO(orderEntity);
	}

	@Override
	@Transactional
	public CustomerOrderResourceDTO deleteOrder(Long id) throws Exception{
		Optional<CustomerOrder> optionaOrderEntity = orderDAO.findById(id);
		if(optionaOrderEntity.isPresent()) {
			CustomerOrder orderEntity = optionaOrderEntity.get();
			orderEntity.setArchived(1);
			for(CustomerOrderLine orderLine : orderEntity.getOrderLines()) {
				orderLine.setArchived(1);
			}
			CustomerOrder archivedOrderEntity = orderDAO.save(orderEntity);
			return orderDTOConverter.getOrderDTO(archivedOrderEntity);
		}
		return null;
	}
	
	@Override
	public List<CustomerOrderResourceDTO> findByBusNameAndLocnNbr(String busName, Integer locnNbr) throws Exception {
		PageRequest pageRequest = new PageRequest(0, 20);
		List<CustomerOrder> orderEntityList = orderDAO.findByBusNameAndLocnNbrNoDtls(busName, locnNbr, pageRequest);
		List<CustomerOrderResourceDTO> orderDTOList = new ArrayList();
		for(CustomerOrder orderEntity : orderEntityList) {
			orderDTOList.add(orderDTOConverter.getOrderDTO(orderEntity));
		}
		return orderDTOList;
	}

	@Override
	public List<CustomerOrderResourceDTO> searchOrder(CustomerOrderSearchRequestDTO orderSearchRequestDTO) {
		PageRequest pageRequest = new PageRequest(0, 20);
		
		CustomerOrder customerOrderByExample = orderDTOConverter.getOrderEntityForSearch(orderSearchRequestDTO);
		List<CustomerOrder> orderEntityList = orderDAO.findAll(Example.of(customerOrderByExample));	
		
		List<CustomerOrderResourceDTO> orderDTOList = new ArrayList();
		for(CustomerOrder orderEntity : orderEntityList) {
			orderDTOList.add(orderDTOConverter.getOrderDTO(orderEntity));
		}
		return orderDTOList;
	}	
}
