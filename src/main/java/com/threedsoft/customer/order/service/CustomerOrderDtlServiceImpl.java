package com.threedsoft.customer.order.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.threedsoft.customer.order.db.CustomerOrder;
import com.threedsoft.customer.order.db.CustomerOrderLine;
import com.threedsoft.customer.order.db.CustomerOrderLineRepository;
import com.threedsoft.customer.order.db.CustomerOrderRepository;
import com.threedsoft.customer.order.dto.converter.CustomerOrderLineDTOConverter;
import com.threedsoft.customer.order.dto.events.CustomerOrderLineCreatedEvent;
import com.threedsoft.customer.order.dto.events.CustomerOrderLineCreationFailedEvent;
import com.threedsoft.customer.order.dto.events.CustomerOrderLineUpdateFailedEvent;
import com.threedsoft.customer.order.dto.requests.CustomerOrderLineCreationRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderLineUpdateRequestDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderLineResourceDTO;
import com.threedsoft.customer.order.util.CustomerOrderConstants;
import com.threedsoft.util.service.EventPublisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerOrderDtlServiceImpl implements CustomerOrderDtlService {
	@Autowired
	CustomerOrderLineRepository orderLineDAO;

	@Autowired
	CustomerOrderRepository orderDAO;

	@Autowired
	EventPublisher eventPublisher;

	@Autowired
	CustomerOrderLineDTOConverter orderLineDTOConverter;

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	@Transactional
	public CustomerOrderLineResourceDTO createOrderLine(CustomerOrderLineCreationRequestDTO orderLineCreationRequestDTO) throws Exception {
		CustomerOrderLineResourceDTO orderLineResponseDTO = null;
		try {
			CustomerOrderLine orderLine = orderLineDTOConverter.getOrderLineEntity(orderLineCreationRequestDTO);
			CustomerOrder order = orderDAO.findById(orderLineCreationRequestDTO.getBusName(), orderLineCreationRequestDTO.getLocnNbr(), orderLineCreationRequestDTO.getOrderId());
			if(order == null) throw new Exception("Customer Order not found:" + orderLineCreationRequestDTO.getOrderId());
			orderLine.setOrder(order);
			CustomerOrderLine savedOrderLineObj = orderLineDAO.save(orderLine);
			orderLineResponseDTO = orderLineDTOConverter.getOrderLineDTO(savedOrderLineObj);
			eventPublisher.publish(new CustomerOrderLineCreatedEvent(orderLineResponseDTO, CustomerOrderConstants.CUSTOMER_ORDER_SERVICE_NAME));
		} catch (Exception ex) {
			log.error("Created Order Error:" + ex.getMessage(), ex);
			eventPublisher.publish(
					new CustomerOrderLineCreationFailedEvent(orderLineCreationRequestDTO, CustomerOrderConstants.CUSTOMER_ORDER_SERVICE_NAME, "Create Order Line Error:" + ex.getMessage()));
			throw ex;
		}
		return orderLineResponseDTO;
	}

	@Override
	public CustomerOrderLineResourceDTO findById(String busName, Integer locnNbr, Long orderId, Long id) throws Exception {
		Optional<CustomerOrderLine> orderLineEntityOptional = orderLineDAO.findById(id);
		if(orderLineEntityOptional.isPresent()) {
			CustomerOrderLine orderLine = orderLineEntityOptional.get();
			return orderLineDTOConverter.getOrderLineDTO(orderLine);
		}
		throw new Exception("Customer Order Line not found for id:" + id);
	}
	
	@Override
	@Transactional
	public CustomerOrderLineResourceDTO deleteOrderLine(Long id) throws Exception{
		Optional<CustomerOrderLine> optionalOrderLineEntity = orderLineDAO.findById(id);
		if(optionalOrderLineEntity.isPresent()) {
			CustomerOrderLine orderLineEntity = optionalOrderLineEntity.get();
			orderLineEntity.setArchived(1);
			CustomerOrderLine archivedOrderLineEntity = orderLineDAO.save(orderLineEntity);
			return orderLineDTOConverter.getOrderLineDTO(archivedOrderLineEntity);
		}
		return null;
	}
	
	@Override
	public List<CustomerOrderLineResourceDTO> findByBusNameAndLocnNbrAndOrderId(String busName, Integer locnNbr, Long orderId) throws Exception {
		//List<CustomerOrderLine> orderLineEntityList = orderLineDAO.findByBusNameAndLocnNbrAndOrderId(busName, locnNbr, orderId);
		List<CustomerOrderLineResourceDTO> orderLineDTOList = new ArrayList();
		Optional<CustomerOrder> customerOrderOptional = orderDAO.findById(orderId);
		if(customerOrderOptional.isPresent()) {
			List<CustomerOrderLine> orderLineEntityList = customerOrderOptional.get().getOrderLines();
			for(CustomerOrderLine orderEntity : orderLineEntityList) {
				orderLineDTOList.add(orderLineDTOConverter.getOrderLineDTO(orderEntity));
			}
		}
		return orderLineDTOList;
	}	
	
	@Override
	@Transactional
	public CustomerOrderLineResourceDTO updateOrderLine(CustomerOrderLineUpdateRequestDTO orderLineUpdateRequestDTO) throws Exception {
		CustomerOrderLineResourceDTO orderLineDTO = null;
		try {
			Optional<CustomerOrderLine> orderLineOptional = orderLineDAO.findById(orderLineUpdateRequestDTO.getId());
			if (!orderLineOptional.isPresent()) {
				throw new Exception("Customer Order Line Update Failed. Order Line Not found to update");
			}
			CustomerOrderLine orderLineEntity = orderLineOptional.get();
			orderLineDTOConverter.updateOrderLineEntity(orderLineEntity, orderLineUpdateRequestDTO);
			orderLineDTO = orderLineDTOConverter.getOrderLineDTO(orderLineDAO.save(orderLineEntity));
		} catch (Exception ex) {
			log.error("Update Customer Order Line Error:" + ex.getMessage(), ex);
			eventPublisher.publish(
					new CustomerOrderLineUpdateFailedEvent(orderLineUpdateRequestDTO, CustomerOrderConstants.CUSTOMER_ORDER_SERVICE_NAME, "Customer Order Line Update Error:" + ex.getMessage()));
			throw ex;
		}
		return orderLineDTO;
	}

/*	
	@Override
	public CustomerOrderResourceDTO updateOrderLineStatusToReserved(CustomerOrderLineUpdateRequestDTO orderLineStatusUpdReq)
			throws Exception {
		CustomerOrderResourceDTO orderResponseDTO = null;
		try {
			CustomerOrder orderEntity = orderDAO.findByBusNameAndLocnNbrAndOrderNbr(orderLineStatusUpdReq.getBusName(),
					orderLineStatusUpdReq.getLocnNbr(), orderLineStatusUpdReq.getOrderId());
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
	
	
*/
}
