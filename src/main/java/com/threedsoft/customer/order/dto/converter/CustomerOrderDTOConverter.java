package com.threedsoft.customer.order.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.threedsoft.customer.order.db.CustomerOrder;
import com.threedsoft.customer.order.db.CustomerOrderLine;
import com.threedsoft.customer.order.dto.requests.CustomerOrderCreationRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderLineCreationRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderSearchRequestDTO;
import com.threedsoft.customer.order.dto.requests.CustomerOrderUpdateRequestDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderLineResourceDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderResourceDTO;
import com.threedsoft.customer.order.util.OrderLineStatus;
import com.threedsoft.customer.order.util.OrderStatus;

@Component
public class CustomerOrderDTOConverter {

	@Autowired
	CustomerOrderLineDTOConverter orderLineConverter;

	public CustomerOrderResourceDTO getOrderDTO(CustomerOrder orderEntity) {
		List<CustomerOrderLineResourceDTO> orderLineDTOList = new ArrayList();
		if (orderEntity.getOrderLines() != null) {
			for (CustomerOrderLine orderLine : orderEntity.getOrderLines()) {
				CustomerOrderLineResourceDTO orderLineDTO = orderLineConverter.getOrderLineDTO(orderLine);
				orderLineDTOList.add(orderLineDTO);
			}
		}
		CustomerOrderResourceDTO orderDTO = new CustomerOrderResourceDTO(orderEntity.getId(), orderEntity.getBusName(),
				orderEntity.getLocnNbr(), orderEntity.getCompany(), orderEntity.getDivision(), orderEntity.getBusUnit(),
				orderEntity.getExternalBatchNbr(), orderEntity.getBatchNbr(), orderEntity.getOrderNbr(),
				orderEntity.getStatus(), orderEntity.getOrderDttm(), orderEntity.getShipByDttm(),
				orderEntity.getExpectedDeliveryDttm(), orderEntity.getDeliveryType(), orderEntity.getIsGift(),
				orderEntity.getGiftMsg(), orderEntity.getSource(), orderEntity.getTransactionName(),
				orderEntity.getRefField1(), orderEntity.getRefField2(), orderEntity.getUpdatedBy(),
				orderEntity.getArchived(), orderLineDTOList);
		return orderDTO;
	}

	public CustomerOrder getOrderEntity(CustomerOrderCreationRequestDTO orderCreationRequestDTO) {
		CustomerOrder orderEntity = new CustomerOrder(orderCreationRequestDTO.getBusName(),
				orderCreationRequestDTO.getLocnNbr(), orderCreationRequestDTO.getCompany(),
				orderCreationRequestDTO.getDivision(), orderCreationRequestDTO.getBusUnit(),
				orderCreationRequestDTO.getExternalBatchNbr(), orderCreationRequestDTO.getOrderNbr(),
				orderCreationRequestDTO.getOrderDttm(), orderCreationRequestDTO.getShipByDttm(),
				orderCreationRequestDTO.getExpectedDeliveryDttm(), orderCreationRequestDTO.getDeliveryType(),
				orderCreationRequestDTO.getIsGift(), orderCreationRequestDTO.getGiftMsg(),
				orderCreationRequestDTO.getSource(), orderCreationRequestDTO.getTransactionName(),
				orderCreationRequestDTO.getRefField1(), orderCreationRequestDTO.getRefField2(),
				orderCreationRequestDTO.getUserId());
		List<CustomerOrderLine> orderLineList = new ArrayList();
		orderEntity.setOrderLines(orderLineList);
		if(orderCreationRequestDTO
				.getOrderLines() != null) {
			for (CustomerOrderLineCreationRequestDTO orderLineCreationRequestDTO : orderCreationRequestDTO
					.getOrderLines()) {
				CustomerOrderLine orderLineEntity = orderLineConverter.getOrderLineEntity(orderLineCreationRequestDTO);
				orderLineEntity.setStatus(OrderLineStatus.READY.getStatus());
				orderEntity.addOrderLine(orderLineEntity);
				orderLineEntity.setOrder(orderEntity);
			}
		}
		orderEntity.setStatus(OrderStatus.READY.getStatus());
		return orderEntity;
	}

	public CustomerOrder getOrderEntityForSearch(CustomerOrderSearchRequestDTO orderSearchRequestDTO) {
		CustomerOrder orderEntity = new CustomerOrder(orderSearchRequestDTO.getBusName(),
				orderSearchRequestDTO.getLocnNbr(), orderSearchRequestDTO.getCompany(),
				orderSearchRequestDTO.getDivision(), orderSearchRequestDTO.getBusUnit(),
				orderSearchRequestDTO.getExternalBatchNbr(), orderSearchRequestDTO.getOrderNbr(),
				orderSearchRequestDTO.getOrderDttm(), orderSearchRequestDTO.getShipByDttm(),
				orderSearchRequestDTO.getExpectedDeliveryDttm(), orderSearchRequestDTO.getDeliveryType(),
				orderSearchRequestDTO.getIsGift(), null, null, null, orderSearchRequestDTO.getRefField1(),
				orderSearchRequestDTO.getRefField2(), null);
		orderEntity.setStatus(orderSearchRequestDTO.getStatus());
		return orderEntity;
	}

	public CustomerOrder updateOrderEntity(CustomerOrder orderEntity, CustomerOrderUpdateRequestDTO orderUpdateReqDTO) {
		orderEntity.setExpectedDeliveryDttm(orderUpdateReqDTO.getExpectedDeliveryDttm());
		orderEntity.setDeliveryType(orderUpdateReqDTO.getDeliveryType());
		orderEntity.setIsGift(orderUpdateReqDTO.getIsGift());
		orderEntity.setGiftMsg(orderUpdateReqDTO.getGiftMsg());
		orderEntity.setShipByDttm(orderUpdateReqDTO.getShipByDttm());
		orderEntity.setTransactionName(orderUpdateReqDTO.getTransactionName());
		orderEntity.setUpdatedBy(orderUpdateReqDTO.getUserId());
		orderEntity.setRefField1(orderUpdateReqDTO.getRefField1());
		orderEntity.setRefField2(orderUpdateReqDTO.getRefField2());
		orderEntity.setSource(orderUpdateReqDTO.getSource());
		orderEntity.setExternalBatchNbr(orderUpdateReqDTO.getExternalBatchNbr());
		return orderEntity;
	}

	/*
	 * public CustomerOrderLineResourceDTO getOrderLineDTO(CustomerOrderLine
	 * orderLine) { CustomerOrder order = orderLine.getOrder();
	 * CustomerOrderLineResourceDTO orderLineDTO = new
	 * CustomerOrderLineResourceDTO(orderLine.getId(), orderLine.getOrder().getId(),
	 * orderLine.getOrderLineNbr(), order.getOrderNbr(), order.getBusName(),
	 * order.getLocnNbr(), order.getCompany(), order.getDivision(),
	 * order.getBusUnit(), orderLine.getItemBrcd(), orderLine.getOrigOrderQty(),
	 * orderLine.getOrderQty(), orderLine.getCancelledQty(),
	 * orderLine.getShortQty(), orderLine.getPickedQty(), orderLine.getPackedQty(),
	 * orderLine.getShippedQty(), orderLine.getStatus(), orderLine.getOlpn(),
	 * orderLine.getSource(), orderLine.getTransactionName(),
	 * orderLine.getRefField1(), orderLine.getRefField2(),
	 * orderLine.getUpdatedDttm(), orderLine.getUpdatedBy()); return orderLineDTO; }
	 * 
	 * public CustomerOrderLine
	 * getOrderLineEntity(CustomerOrderLineCreationRequestDTO
	 * orderLineCreationRequestDTO, CustomerOrderCreationRequestDTO
	 * orderCreationRequestDTO) { CustomerOrderLine orderLine = new
	 * CustomerOrderLine(orderLineCreationRequestDTO.getOrderLineNbr(),
	 * orderLineCreationRequestDTO.getItemBrcd(),
	 * orderLineCreationRequestDTO.getOrigOrderQty(),
	 * orderLineCreationRequestDTO.getOrderQty(),
	 * orderCreationRequestDTO.getSource(),
	 * orderCreationRequestDTO.getTransactionName(),
	 * orderLineCreationRequestDTO.getRefField1(),
	 * orderLineCreationRequestDTO.getRefField2(),
	 * orderCreationRequestDTO.getUserId()); return orderLine; }
	 */
}
