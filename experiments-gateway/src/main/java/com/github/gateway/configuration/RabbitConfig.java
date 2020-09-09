package com.github.gateway.configuration;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	public static final String directExchangeName = "experiments.services";

	@Bean
	DirectExchange exchange() {
		return new DirectExchange(directExchangeName);
	}

	@Bean
	public MessageConverter jackson2MessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

}
