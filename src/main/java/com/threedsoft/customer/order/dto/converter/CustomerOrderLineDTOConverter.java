package com.threedsoft.customer.order.dto.converter;

import org.springframework.stereotype.Component;

import com.threedsoft.customer.order.db.CustomerOrder;
import com.threedsoft.customer.order.db.CustomerOrderLine;
import com.threedsoft.customer.order.dto.requests.CustomerOrderLineCreationRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderLineUpdateRequestDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderLineResourceDTO;

@Component
public class CustomerOrderLineDTOConverter {

	public CustomerOrderLineResourceDTO getOrderLineDTO(CustomerOrderLine orderLine) {
		CustomerOrder order = orderLine.getOrder();
		CustomerOrderLineResourceDTO orderLineDTO = new CustomerOrderLineResourceDTO(orderLine.getId(),
				orderLine.getOrder().getId(), orderLine.getOrderLineNbr(), order.getOrderNbr(), order.getBusName(), order.getLocnNbr(),
				order.getCompany(), order.getDivision(), order.getBusUnit(), orderLine.getItemBrcd(),
				orderLine.getOrigOrderQty(), orderLine.getOrderQty(), orderLine.getCancelledQty(),
				orderLine.getShortQty(), orderLine.getPickedQty(), orderLine.getPackedQty(), orderLine.getShippedQty(),
				orderLine.getStatus(), orderLine.getOlpn(), orderLine.getSource(), orderLine.getTransactionName(),
				orderLine.getRefField1(), orderLine.getRefField2(), orderLine.getUpdatedDttm(),
				orderLine.getUpdatedBy(), orderLine.getArchived());
		return orderLineDTO;
	}

	public CustomerOrderLine getOrderLineEntity(CustomerOrderLineCreationRequestDTO orderLineCreationRequestDTO) {
		CustomerOrderLine orderLine = new CustomerOrderLine(orderLineCreationRequestDTO.getOrderLineNbr(),
				orderLineCreationRequestDTO.getItemBrcd(), orderLineCreationRequestDTO.getOrigOrderQty(),
				orderLineCreationRequestDTO.getOrderQty(), orderLineCreationRequestDTO.getSource(),
				orderLineCreationRequestDTO.getTransactionName(), orderLineCreationRequestDTO.getRefField1(),
				orderLineCreationRequestDTO.getRefField2(), orderLineCreationRequestDTO.getUserId());
		return orderLine;
	}

	public void updateOrderLineEntity(CustomerOrderLine orderLineEntity,
			CustomerOrderLineUpdateRequestDTO orderLineUpdateRequestDTO) {
		orderLineEntity.setOrigOrderQty(orderLineUpdateRequestDTO.getOrigOrderQty());
		orderLineEntity.setOrderQty(orderLineUpdateRequestDTO.getOrderQty());
		orderLineEntity.setRefField1(orderLineUpdateRequestDTO.getRefField1());
		orderLineEntity.setRefField2(orderLineUpdateRequestDTO.getRefField2());
		orderLineEntity.setSource(orderLineUpdateRequestDTO.getSource());
		orderLineEntity.setTransactionName(orderLineUpdateRequestDTO.getTransactionName());
		orderLineEntity.setUpdatedBy(orderLineUpdateRequestDTO.getUserId());
		orderLineEntity.setItemBrcd(orderLineUpdateRequestDTO.getItemBrcd());
		orderLineEntity.setOrderLineNbr(orderLineUpdateRequestDTO.getOrderLineNbr());
	}

}
