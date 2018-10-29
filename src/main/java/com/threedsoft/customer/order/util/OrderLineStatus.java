package com.threedsoft.customer.order.util;

public enum OrderLineStatus {
	CREATED("Created"), READY("Ready"), ALLOCATED("Allocated"), PARTIALLY_ALLOCATED("PartiallyAllocated"),
	PICKED("Picked"), SORTED("Sorted"), PACKED("Packed"), SHIPPED("Shipped"), SHORTED("Shorted"),
	CANCELLED("Cancelled");
	OrderLineStatus(String status) {
		this.status = status;
	}

	private String status;

	public String getStatus() {
		return status;
	}
}