package com.example.customer.order.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.customer.order.db.CustomerOrder;
import com.example.customer.order.db.CustomerOrderLine;
import com.example.customer.order.db.CustomerOrderLineRepository;
import com.example.customer.order.db.CustomerOrderRepository;
import com.example.customer.order.dto.converter.CustomerOrderDTOConverter;
import com.example.customer.order.dto.events.CustomerOrderAllocatedEvent;
import com.example.customer.order.dto.events.CustomerOrderCreatedEvent;
import com.example.customer.order.dto.events.CustomerOrderCreationFailedEvent;
import com.example.customer.order.dto.events.CustomerOrderLineAllocationFailedEvent;
import com.example.customer.order.dto.events.CustomerOrderUpdateFailedEvent;
import com.example.customer.order.dto.requests.CustomerOrderCreationRequestDTO;
import com.example.customer.order.dto.requests.CustomerOrderLineStatusUpdateRequestDTO;
import com.example.customer.order.dto.requests.CustomerOrderUpdateRequestDTO;
import com.example.customer.order.dto.responses.CustomerOrderDTO;
import com.example.util.service.EventPublisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {
	@Autowired
	CustomerOrderRepository orderDAO;

	@Autowired
	CustomerOrderLineRepository orderLineDAO;

	@Autowired
	EventPublisher eventPublisher;

	@Autowired
	CustomerOrderDTOConverter orderDTOConverter;

	public enum OrderStatus {
		CREATED(100), READY(110), ALLOCATED(120), PARTIALLY_ALLOCATED(121), PICKED(130), PACKED(140), SHIPPED(150),
		SHORTED(160), CANCELLED(199);
		OrderStatus(Integer statCode) {
			this.statCode = statCode;
		}

		private Integer statCode;

		public Integer getStatCode() {
			return statCode;
		}
	}

	public enum OrderLineStatus {
		CREATED(100), READY(110), ALLOCATED(120), PICKED(130), PACKED(140), SHIPPED(150), SHORTED(160), CANCELLED(199);
		OrderLineStatus(Integer statCode) {
			this.statCode = statCode;
		}

		private Integer statCode;

		public Integer getStatCode() {
			return statCode;
		}
	}

	@Override
	@Transactional
	public CustomerOrderDTO updateOrder(CustomerOrderUpdateRequestDTO orderUpdateRequestDTO) throws Exception {
		CustomerOrderDTO orderDTO = null;
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
					new CustomerOrderUpdateFailedEvent(orderUpdateRequestDTO, "Update Order Error:" + ex.getMessage()));
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
	public CustomerOrderDTO createOrder(CustomerOrderCreationRequestDTO orderCreationRequestDTO) throws Exception {
		CustomerOrderDTO orderResponseDTO = null;
		try {
			CustomerOrder order = orderDTOConverter.getOrderEntity(orderCreationRequestDTO);
			CustomerOrder savedOrderObj = orderDAO.save(order);
			orderResponseDTO = orderDTOConverter.getOrderDTO(savedOrderObj);
			eventPublisher.publish(new CustomerOrderCreatedEvent(orderResponseDTO));
		} catch (Exception ex) {
			log.error("Created Order Error:" + ex.getMessage(), ex);
			eventPublisher.publish(
					new CustomerOrderCreationFailedEvent(orderCreationRequestDTO, "Created Order Error:" + ex.getMessage()));
			throw ex;
		}
		return orderResponseDTO;
	}

	@Override
	public CustomerOrderDTO findById(String busName, Integer locnNbr, Long id) throws Exception {
		CustomerOrder orderEntity = orderDAO.findById(busName, locnNbr, id);
		return orderDTOConverter.getOrderDTO(orderEntity);
	}

	@Override
	public CustomerOrderDTO updateOrderLineStatusToReserved(CustomerOrderLineStatusUpdateRequestDTO orderLineStatusUpdReq)
			throws Exception {
		CustomerOrderDTO orderResponseDTO = null;
		try {
			CustomerOrder orderEntity = orderDAO.findByBusNameAndLocnNbrAndOrderNbr(orderLineStatusUpdReq.getBusName(),
					orderLineStatusUpdReq.getLocnNbr(), orderLineStatusUpdReq.getOrderNbr());
			CustomerOrderLine orderLine = this.getOrderLine(orderEntity, orderLineStatusUpdReq.getId());
			orderLine.setStatCode(OrderLineStatus.ALLOCATED.getStatCode());
			orderEntity.setStatCode(OrderStatus.PARTIALLY_ALLOCATED.getStatCode());
			orderEntity = orderDAO.save(orderEntity);
			
			boolean isEntireOrderReservedForInventory = areAllOrderLinesSameStatus(orderEntity, OrderLineStatus.ALLOCATED.getStatCode());

			if (isEntireOrderReservedForInventory) {
				orderEntity.setStatCode(OrderStatus.ALLOCATED.getStatCode());
				orderEntity = orderDAO.save(orderEntity);
				eventPublisher.publish(new CustomerOrderAllocatedEvent(orderDTOConverter.getOrderDTO(orderEntity)));
			}
		} catch (Exception ex) {
			log.error("Order Line Allocation Failed Error:" + ex.getMessage(), ex);
			eventPublisher.publish(new CustomerOrderLineAllocationFailedEvent(orderLineStatusUpdReq,
					"Order Line Allocation Failed Error:" + ex.getMessage()));
			throw ex;
		}
		return orderResponseDTO;
	}
	
	public CustomerOrderLine getOrderLine(CustomerOrder orderEntity, Long orderDtlId) {
		for (CustomerOrderLine orderLine : orderEntity.getOrderLines()) {
			if (orderLine.getId() == orderDtlId) {
				return orderLine;
			}
		}
		return null;
	}

	public boolean areAllOrderLinesSameStatus(CustomerOrder orderEntity, Integer statCode) {
		for (CustomerOrderLine orderLine : orderEntity.getOrderLines()) {
			if (!(orderLine.getStatCode()==statCode)) {
				return false;
			}
		}
		return true;
	}
}
