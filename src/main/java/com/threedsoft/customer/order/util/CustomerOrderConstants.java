package com.threedsoft.customer.order.util;

import org.springframework.beans.factory.annotation.Value;

public class CustomerOrderConstants {
	@Value("${spring.application.name}")
	public static String CUSTOMER_ORDER_SERVICE_NAME;
}
