package com.example.customer.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.customer.order.streams.CustomerOrderStreams;
import com.example.util.service.EventPublisher;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableBinding(CustomerOrderStreams.class)
@EnableAutoConfiguration
@EnableScheduling
@EnableJpaAuditing
@Slf4j
public class CustomerOrderApplication {
	@Autowired
	CustomerOrderStreams customerOrderStreams;
	
	public static void main(String[] args) {
		SpringApplication.run(CustomerOrderApplication.class, args);
	}

	@PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));   // It will set UTC timezone
        System.out.println("Spring boot application running in UTC timezone :"+ LocalDateTime.now());   // It will print UTC timezone
    }
	
	@Bean
	public EventPublisher eventPublisher() {
		return new EventPublisher(customerOrderStreams.outboundCustomerOrders());
	}	
	
}
