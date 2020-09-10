package com.github.service.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.github.service.domain.Search;
import com.github.service.domain.User;
import com.github.service.repository.UserRepository;

@Service
public class UserManagementService {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private MongoTemplate template;
	
	@RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "user.create.request"), exchange = @Exchange("experiments.services"), key="user.create")})
	public String add(User user) {
		if (user != null) {
			try {
				UUID id = UUID.randomUUID();
				user.setId(id);
				user.setCreationDate(new Date());
				repository.insert(user);
				return id.toString();
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}

	@RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "user.update.request"), exchange = @Exchange("experiments.services"), key="user.update")})
	public void update(User user) {
		if (user != null) {
			if (repository.existsById(user.getId())) {
				User current = repository.findById(user.getId()).get();
				current.setFirstName(user.getFirstName());
				current.setSurname(user.getSurname());
				current.setDateOfBirth(user.getDateOfBirth());
				repository.save(current);
			}
		}
	}

	@RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "user.retrieve.request"), exchange = @Exchange("experiments.services"), key="user.retrieve")})
	public User retrieve(String userId) {
		if (userId != null && !userId.isEmpty()) {
			UUID uuid = UUID.fromString(userId);
			if (repository.existsById(uuid)) {
				return repository.findById(uuid).get();
			}
		}
		return null;
	}

	@RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "user.delete.request"), exchange = @Exchange("experiments.services"), key="user.delete")})
	public void delete(String userId) {
		if (userId != null && !userId.isEmpty()) {
			UUID uuid = UUID.fromString(userId);
			if (repository.existsById(uuid)) {
				repository.deleteById(uuid);
			}
		}
	}

	private Direction decodeDirection(String order) {
		if (order != null && !order.isEmpty()) {
			try {
				return Direction.valueOf(order.toUpperCase());
			} catch (Exception ex) {
				return Direction.ASC;	
			}
		} else {
			return Direction.ASC;
		}
	}
	
	private String[] decodeProperties(String sort) {
		List<String> properties = new ArrayList<>();
		if (sort != null && !sort.isEmpty()) {
			String[] allowedColumns = {"firstName", "surname", "dateOfBirth", "creationDate"};
			String[] columns = sort.split(",");
			for (String column : columns) {
				if (Arrays.asList(allowedColumns).contains(column)) {
					properties.add(column);
				}
			}
		}
		
		if (properties.size() == 0) {
			properties.add("creationDate");
		}
		
		String[] result = new String[properties.size()];
		result = properties.toArray(result);
		
		return result;
	}
	
	@RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "user.search.request"), exchange = @Exchange("experiments.services"), key="user.search")})
	public List<User> search(Search search) {
		if (search == null) {
			search = new Search();
		}
		
		Query query = new Query();
		
		if (search.getKeyword() != null && !search.getKeyword().isEmpty()) {
			Criteria criteria1 = Criteria.where("firstName").regex("^" + search.getKeyword(), "i");
			Criteria criteria2 = Criteria.where("surname").regex("^" + search.getKeyword(), "i");
			
			query.addCriteria(new Criteria().orOperator(criteria1, criteria2));
		}
		
		query.with(Sort.by(
						decodeDirection(search.getOrder()), 
						decodeProperties(search.getSort())
						)
		);
		
		
		return template.find(query, User.class);
	}
	
	@RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "user.status.request"), exchange = @Exchange("experiments.services"), key="user.status")})
	public String status(String token) {
		return token;
	}

}
