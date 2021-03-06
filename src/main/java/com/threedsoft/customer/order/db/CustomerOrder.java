package com.threedsoft.customer.order.db;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Entity
@Data
@Table(name="CUSTOMER_ORDERS")
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class CustomerOrder  implements Serializable{
	@Column(name="ID")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order")
	List<CustomerOrderLine> orderLines;
	
	@Column(name="BUS_NAME")
	String busName;

	@Column(name="LOCN_NBR")
	Integer locnNbr;

	@Column(name="COMPANY")
	String company;

	@Column(name="DIVISION")
	String division;

	@Column(name="BUS_UNIT")
	String busUnit;

	@Column(name="EXT_BATCH_NBR")
	String externalBatchNbr;

	@Column(name="BATCH_NBR")
	String batchNbr;

	@Column(name="ORDER_NBR")
	String orderNbr;

	@Column(name="STATUS")
	String status;

	@Column(name="ORDER_DTTM")
	LocalDateTime orderDttm;

	@Column(name="SHIP_BY_DTTM")
	LocalDateTime shipByDttm;

	@Column(name="EXPECTED_DELIVERY_DTTM")
	LocalDateTime expectedDeliveryDttm;

	@Column(name="DELIVERY_TYPE")
	String deliveryType;

	@Column(name="IS_GIFT")
//	@Convert(converter=BooleanTFConverter.class)
	String isGift;

	@Column(name="GIFT_MSG")
	String giftMsg;

	@Column(name="SOURCE")
	String source;

	@Column(name="TRANSACTION_NAME")
	String transactionName;

	@Column(name="REF_FIELD_1")
	String refField1;

	@Column(name="REF_FIELD_2")
	String refField2;

	@Column(name="HOST_NAME")
	String hostName;

    @CreatedDate
	@Column(name="CREATED_DTTM", nullable = false, updatable = false)
    LocalDateTime createdDttm;
	
    @Column(name = "UPDATED_DTTM", nullable = false)
    @LastModifiedDate
	LocalDateTime updatedDttm;
	
	@Column(name="CREATED_BY")
	String createdBy;

 	@Column(name="UPDATED_BY")
	String updatedBy;

 	@Version
 	@Column(name="VERSION")
	Integer version;

 	@Column(name="ARCHIVED")
	Integer archived;

 	public void addOrderLine(CustomerOrderLine orderLine) {
    	orderLines.add(orderLine);
    	//orderLine.setOrder(this);
    }
 
    public void removeOrderLine(CustomerOrderLine orderLine) {
    	orderLines.remove(orderLine);
    	//orderLine.setOrder(null);
    }

	public CustomerOrder(String busName, Integer locnNbr, String company, String division, String busUnit,
			String externalBatchNbr, String orderNbr, LocalDateTime orderDttm, LocalDateTime shipByDttm, LocalDateTime expectedDeliveryDttm,
			String deliveryType, String isGift, String giftMsg, String source, String transactionName,
			String refField1, String refField2, String userId) {
		this.busName = busName;
		this.locnNbr = locnNbr;
		this.company = company;
		this.division = division;
		this.busUnit = busUnit;
		this.externalBatchNbr = externalBatchNbr;
		this.orderNbr = orderNbr;
		this.orderDttm = orderDttm;
		this.shipByDttm = shipByDttm;
		this.expectedDeliveryDttm = expectedDeliveryDttm;
		this.deliveryType = deliveryType;
		this.isGift = isGift;
		this.giftMsg = giftMsg;
		this.source = source;
		this.transactionName = transactionName;
		this.refField1 = refField1;
		this.refField2 = refField2;
		this.createdBy = userId;
		this.updatedBy = userId;
	}
}
