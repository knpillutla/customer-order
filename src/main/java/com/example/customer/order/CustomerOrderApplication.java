package com.example.customer.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.customer.order.streams.CustomerOrderStreams;
import com.example.util.service.EventPublisher;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableBinding(CustomerOrderStreams.class)
@EnableAutoConfiguration
@EnableScheduling
@Slf4j
public class CustomerOrderApplication {
	@Autowired
	CustomerOrderStreams customerOrderStreams;
	
	public static void main(String[] args) {
		SpringApplication.run(CustomerOrderApplication.class, args);
	}
	@Bean
	public EventPublisher eventPublisher() {
		return new EventPublisher(customerOrderStreams.outboundCustomerOrders());
	}	
	
}
