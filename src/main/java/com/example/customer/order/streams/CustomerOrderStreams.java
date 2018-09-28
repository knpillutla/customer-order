package com.example.customer.order.streams;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface CustomerOrderStreams {
    public String CUSTOMER_ORDERS_INPUT = "customer-orders-in";
    public String CUSTOMER_ORDERS_OUTPUT = "customer-orders-out";
    //public String INVENTORY_OUTPUT="inventory-out";
    
    @Input(CUSTOMER_ORDERS_INPUT)
    public SubscribableChannel inboundCustomerOrders();
    
    //@Input(INVENTORY_OUTPUT)
    //public SubscribableChannel outboundInventory();

    @Output(CUSTOMER_ORDERS_OUTPUT)
    public MessageChannel outboundCustomerOrders();
}