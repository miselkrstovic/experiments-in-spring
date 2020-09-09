package com.github.gateway.service;

import java.util.List;
import java.util.UUID;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gateway.configuration.RabbitConfig;
import com.github.gateway.domain.Search;
import com.github.gateway.domain.User;

@Component
public class UserManagementService {

	@Autowired
	RabbitTemplate template;

	public String performAdd(User user) {
		String id = template.convertSendAndReceiveAsType(
				RabbitConfig.directExchangeName, 
				"user.create", 
				user,
				new ParameterizedTypeReference<String>() {
				});
		return id;
	}

	public void performUpdate(User user) {
		template.convertSendAndReceiveAsType(
				RabbitConfig.directExchangeName, 
				"user.update",
				user, 
				new ParameterizedTypeReference<Void>() {});
	}

	public User performRetrieve(String userId) {
		User user = template.convertSendAndReceiveAsType(
				RabbitConfig.directExchangeName, 
				"user.retrieve",
				userId, 
				new ParameterizedTypeReference<User>() {});
		return user;
	}

	public void performDelete(String userId) {
		template.convertSendAndReceiveAsType(
				RabbitConfig.directExchangeName, 
				"user.delete",
				userId, 
				new ParameterizedTypeReference<Boolean>() {});
	}

	public List<User> performSearch(String keyword, String sort, String order) {
		List<User> result = template.convertSendAndReceiveAsType(
				RabbitConfig.directExchangeName, 
				"user.search",
				new Search(keyword, sort, order), 
				new ParameterizedTypeReference<List<User>>() {});
		return result;
	}

	public String performStatus() {
		String token = UUID.randomUUID().toString();
		String result = template.convertSendAndReceiveAsType(
				RabbitConfig.directExchangeName, 
				"user.status",
				token, 
				new ParameterizedTypeReference<String>() {});
		return result;
	}

}
