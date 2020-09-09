package com.github.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.github.service.domain.User;
import com.github.service.repository.UserRepository;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class UserRepositoryTests {

	@Autowired
	UserRepository repository;
	
	@Autowired
	MongoTemplate template;

	User moe, larry, curly;


    @BeforeAll
    public void setup() throws Exception {
    	repository.deleteAll();
    }
    
    @AfterAll
    public void tearDown() throws Exception {
    	repository.deleteAll();
    }
    
	@BeforeEach
	public void setupData() throws ParseException {
		repository.deleteAll();

		moe = repository.save(new User("Moe", "Howard", new SimpleDateFormat("yyyy-MM-dd").parse(("1897-19-06"))));
		larry = repository.save(new User("Larry", "Fine", new SimpleDateFormat("yyyy-MM-dd").parse(("1902-10-05"))));
		curly = repository.save(new User("Curly", "Howard", new SimpleDateFormat("yyyy-MM-dd").parse(("1903-10-22"))));
	}

	@Test
	public void testSetIdOnSave() throws ParseException {
		// when
		User moe = repository.save(new User("Moe", "Howard", new SimpleDateFormat("yyyy-MM-dd").parse(("1897-19-06"))));

		// then
		assertThat(moe.getId()).isNotNull();
	}

	@Test
	public void testFindsByLastName() {
		// when
		List<User> result = repository.findBySurname("Fine");

		// then
		assertThat(result).hasSize(1).extracting("firstName").contains("Larry");
	}

//	@Test
//	public void testFindsByExample() {
//		// given
//		User probe = new User(null, "Howard", null);
//
//		// when
//		List<User> result = repository.findAll(Example.of(probe));
//
//		// then
//		assertThat(result).hasSize(2).extracting("firstName").contains("Moe", "Curly");
//	}

	@Test
	public void testFindAll() {
		// given
		
		// when
		List<User> result = repository.findAll();
		
		// then
		assertThat(result).hasSize(3);
	}
	
    @Test
    public void testSaveNewTicketWithExistingTicketId() throws Exception {
    	// given
    	User moe = new User("Moe", "Howard", new SimpleDateFormat("yyyy-MM-dd").parse(("1897-19-06")));
    	
    	// when
        repository.save(moe);
        repository.save(moe);
        
        // then
        assertThat(repository.findAll()).hasSize(2);
    }
    
}
