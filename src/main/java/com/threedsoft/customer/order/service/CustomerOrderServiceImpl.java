package com.threedsoft.customer.order.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.threedsoft.customer.order.db.CustomerOrder;
import com.threedsoft.customer.order.db.CustomerOrderLine;
import com.threedsoft.customer.order.db.CustomerOrderLineRepository;
import com.threedsoft.customer.order.db.CustomerOrderRepository;
import com.threedsoft.customer.order.dto.converter.CustomerOrderDTOConverter;
import com.threedsoft.customer.order.dto.events.CustomerOrderAllocatedEvent;
import com.threedsoft.customer.order.dto.events.CustomerOrderCreatedEvent;
import com.threedsoft.customer.order.dto.events.CustomerOrderCreationFailedEvent;
import com.threedsoft.customer.order.dto.events.CustomerOrderLineAllocationFailedEvent;
import com.threedsoft.customer.order.dto.events.CustomerOrderUpdateFailedEvent;
import com.threedsoft.customer.order.dto.requests.CustomerOrderCreationRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderLineStatusUpdateRequestDTO;
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
			log.error("Created Order Error:" + ex.getMessage(), ex);
			eventPublisher.publish(
					new CustomerOrderCreationFailedEvent(orderCreationRequestDTO, CustomerOrderConstants.CUSTOMER_ORDER_SERVICE_NAME, "Created Order Error:" + ex.getMessage()));
			throw ex;
		}
		return orderResponseDTO;
	}

	@Override
	public CustomerOrderResourceDTO findById(String busName, Integer locnNbr, Long id) throws Exception {
		CustomerOrder orderEntity = orderDAO.findById(busName, locnNbr, id);
		return orderDTOConverter.getOrderDTO(orderEntity);
	}

	@Override
	public CustomerOrderResourceDTO updateOrderLineStatusToReserved(CustomerOrderLineStatusUpdateRequestDTO orderLineStatusUpdReq)
			throws Exception {
		CustomerOrderResourceDTO orderResponseDTO = null;
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
				eventPublisher.publish(new CustomerOrderAllocatedEvent(orderDTOConverter.getOrderDTO(orderEntity), CustomerOrderConstants.CUSTOMER_ORDER_SERVICE_NAME));
			}
		} catch (Exception ex) {
			log.error("Order Line Allocation Failed Error:" + ex.getMessage(), ex);
			eventPublisher.publish(new CustomerOrderLineAllocationFailedEvent(orderLineStatusUpdReq,CustomerOrderConstants.CUSTOMER_ORDER_SERVICE_NAME,
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
