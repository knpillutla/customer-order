package com.threedsoft.customer.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.threedsoft.customer.order.streams.CustomerOrderStreams;
import com.threedsoft.util.service.EventPublisher;

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
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC")); // It will set UTC timezone
		System.out.println("Spring boot application running in UTC timezone :" + LocalDateTime.now()); // It will print
																										// UTC timezone
	}

	@Bean
	public EventPublisher eventPublisher() {
		return new EventPublisher(customerOrderStreams.outboundCustomerOrders());
	}
	
	@Bean
	public CorsFilter corsFilter() {

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowCredentials(true); 
	    config.addAllowedOrigin("http://*the3dsoft.com");
	    config.addAllowedOrigin("http://localhost");
	    config.addAllowedOrigin("https://localhost:5000");
	    config.addAllowedHeader("*");
	    config.addAllowedMethod("*");
	    source.registerCorsConfiguration("/**", config);
	    return new CorsFilter(source);
	}

	/*	@Bean
	@Primary
	public ObjectMapper serializingObjectMapper() {
	    ObjectMapper objectMapper = new ObjectMapper();
	    JavaTimeModule javaTimeModule = new JavaTimeModule();
//	    javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
	//    javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
	    objectMapper.registerModule(javaTimeModule);
	    return objectMapper;
	}	*/
/*
	@Bean
	@Primary
	public Jackson2ObjectMapperBuilder objectMapperBuilder() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		return builder.modulesToInstall(new JavaTimeModule());
	}
*/	/*
	 * @Bean
	 * 
	 * @Primary public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder
	 * builder) { ObjectMapper objectMapper = builder.build();
	 * objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
	 * false); //ISODate // ISO 8601 for UTC/OffsetTime/ZonedTime/LocalDateTime
	 * return objectMapper; }
	 */
	/*
	 * @Bean(name = "OBJECT_MAPPER_BEAN") public ObjectMapper jsonObjectMapper() {
	 * return Jackson2ObjectMapperBuilder.json()
	 * .serializationInclusion(JsonInclude.Include.NON_NULL) // Don’t include null
	 * values .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
	 * //ISODate // ISO 8601 for UTC/OffsetTime/ZonedTime/LocalDateTime .modules(new
	 * JSR310Module()) .build(); }
	 */
}
