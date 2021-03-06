package com.github.service.domain;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document
@TypeAlias("User")
public class User {
	
	@MongoId
	private UUID id;
    private String firstName;
    private String surname;
    private Date dateOfBirth;
    private Date creationDate;
    
    public User() {}
    
	public User(String firstName, String surname, Date dateOfBirth) {
		this.id = UUID.randomUUID();
		this.firstName = firstName;
		this.surname = surname;
		this.dateOfBirth = dateOfBirth;
	}
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
}
