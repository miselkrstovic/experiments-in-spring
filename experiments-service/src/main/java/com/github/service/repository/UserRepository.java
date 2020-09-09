package com.github.service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.service.domain.User;

public interface UserRepository extends MongoRepository<User, UUID> {

	public List<User> findByFirstName(String firstName);
	public List<User> findBySurname(String surname);
	
}
