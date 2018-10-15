package com.threedsoft.customer.order.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.threedsoft.customer.order.db.CustomerOrder;
import com.threedsoft.customer.order.db.CustomerOrderLine;
import com.threedsoft.customer.order.dto.requests.CustomerOrderCreationRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderLineCreationRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderUpdateRequestDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderLineResourceDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderResourceDTO;
import com.threedsoft.customer.order.service.CustomerOrderServiceImpl.OrderLineStatus;
import com.threedsoft.customer.order.service.CustomerOrderServiceImpl.OrderStatus;

@Component
public class CustomerOrderDTOConverter {

	public CustomerOrderResourceDTO getOrderDTO(CustomerOrder orderEntity) {
		List<CustomerOrderLineResourceDTO> orderLineDTOList = new ArrayList();
		for(CustomerOrderLine orderLine : orderEntity.getOrderLines()) {
			CustomerOrderLineResourceDTO orderLineDTO = this.getOrderLineDTO(orderLine);
			orderLineDTOList.add(orderLineDTO);
		}
		CustomerOrderResourceDTO orderDTO = new CustomerOrderResourceDTO(orderEntity.getId(), orderEntity.getBusName(), orderEntity.getLocnNbr(),
				orderEntity.getCompany(), orderEntity.getDivision(), orderEntity.getBusUnit(),
				orderEntity.getExternalBatchNbr(), orderEntity.getBatchNbr(), orderEntity.getOrderNbr(),
				orderEntity.getStatCode(), orderEntity.getOrderDttm(), orderEntity.getShipByDttm(),
				orderEntity.getExpectedDeliveryDttm(), orderEntity.getDeliveryType(), orderEntity.getIsGift(),
				orderEntity.getGiftMsg(), orderEntity.getSource(), orderEntity.getTransactionName(),
				orderEntity.getRefField1(), orderEntity.getRefField2(),
				orderEntity.getUpdatedBy(), orderLineDTOList);
		return orderDTO;
	}

	public CustomerOrder getOrderEntity(CustomerOrderCreationRequestDTO orderCreationRequestDTO) {
		CustomerOrder orderEntity = new CustomerOrder(orderCreationRequestDTO.getBusName(), orderCreationRequestDTO.getLocnNbr(), orderCreationRequestDTO.getCompany(),
				orderCreationRequestDTO.getDivision(), orderCreationRequestDTO.getBusUnit(), orderCreationRequestDTO.getExternalBatchNbr(), orderCreationRequestDTO.getOrderNbr(),
				orderCreationRequestDTO.getOrderDttm(), orderCreationRequestDTO.getShipByDttm(), orderCreationRequestDTO.getExpectedDeliveryDttm(),
				orderCreationRequestDTO.getDeliveryType(), orderCreationRequestDTO.isGift(), orderCreationRequestDTO.getGiftMsg(), orderCreationRequestDTO.getSource(),
				orderCreationRequestDTO.getTransactionName(), orderCreationRequestDTO.getRefField1(), orderCreationRequestDTO.getRefField2(), orderCreationRequestDTO.getUserId());
		List<CustomerOrderLine> orderLineList = new ArrayList();
		for (CustomerOrderLineCreationRequestDTO orderLineCreationRequestDTO : orderCreationRequestDTO.getOrderLines()) {
			CustomerOrderLine orderLineEntity = getOrderLineEntity(orderLineCreationRequestDTO, orderCreationRequestDTO);
			orderLineEntity.setStatCode(OrderLineStatus.READY.getStatCode());
			orderEntity.addOrderLine(orderLineEntity);
			orderLineEntity.setOrder(orderEntity);
		}
		orderEntity.setStatCode(OrderStatus.READY.getStatCode());
		return orderEntity;
	}

	public CustomerOrder updateOrderEntity(CustomerOrder orderEntity, CustomerOrderUpdateRequestDTO orderUpdateReqDTO) {
		orderEntity.setExpectedDeliveryDttm(orderUpdateReqDTO.getExpectedDeliveryDttm());
		orderEntity.setDeliveryType(orderUpdateReqDTO.getDeliveryType());
		orderEntity.setIsGift(orderUpdateReqDTO.isGift());
		orderEntity.setGiftMsg(orderUpdateReqDTO.getGiftMsg());
		orderEntity.setShipByDttm(orderUpdateReqDTO.getShipByDttm());
		orderEntity.setTransactionName(orderUpdateReqDTO.getTransactionName());
		orderEntity.setUpdatedBy(orderUpdateReqDTO.getUserId());
		orderEntity.setRefField1(orderUpdateReqDTO.getRefField1());
		orderEntity.setRefField2(orderUpdateReqDTO.getRefField2());
		orderEntity.setSource(orderUpdateReqDTO.getSource());
		return orderEntity;
	}

	public CustomerOrderLineResourceDTO getOrderLineDTO(CustomerOrderLine orderLine) {
		CustomerOrderLineResourceDTO orderLineDTO = new CustomerOrderLineResourceDTO(orderLine.getId(), orderLine.getLocnNbr(), orderLine.getOrder().getId(),
				orderLine.getOrderLineNbr(), orderLine.getItemBrcd(), orderLine.getOrigOrderQty(), orderLine.getOrderQty(),
				orderLine.getCancelledQty(), orderLine.getShortQty(), orderLine.getPickedQty(),
				orderLine.getPackedQty(), orderLine.getShippedQty(), orderLine.getStatCode(), orderLine.getOlpn(),
				orderLine.getSource(), orderLine.getTransactionName(), orderLine.getRefField1(),
				orderLine.getRefField2(), orderLine.getUpdatedDttm(), orderLine.getUpdatedBy());
		return orderLineDTO;
	}

	public CustomerOrderLine getOrderLineEntity(CustomerOrderLineCreationRequestDTO orderLineCreationRequestDTO,  CustomerOrderCreationRequestDTO orderCreationRequestDTO) {
		CustomerOrderLine orderLine = new CustomerOrderLine(orderCreationRequestDTO.getLocnNbr(), orderLineCreationRequestDTO.getOrderLineNbr(), orderLineCreationRequestDTO.getItemBrcd(),
				orderLineCreationRequestDTO.getOrigOrderQty(), orderLineCreationRequestDTO.getOrderQty(), orderCreationRequestDTO.getSource(),
				orderCreationRequestDTO.getTransactionName(), orderLineCreationRequestDTO.getRefField1(), orderLineCreationRequestDTO.getRefField2(),
				orderCreationRequestDTO.getUserId());
		return orderLine;
	}

}
