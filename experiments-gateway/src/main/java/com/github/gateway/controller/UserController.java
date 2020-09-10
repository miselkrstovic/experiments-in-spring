package com.github.gateway.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.github.gateway.domain.Error;
import com.github.gateway.domain.Message;
import com.github.gateway.domain.User;
import com.github.gateway.service.UserManagementService;

import static org.springframework.web.servlet.HandlerMapping.LOOKUP_PATH;

@RestController
@RequestMapping("/v1")
public class UserController {

	@Autowired
	UserManagementService proxy;

	@PostMapping("/users")
	public ResponseEntity<Void> add(@RequestHeader("host") String hostName, @RequestBody User user) {
		try {
			String resourceId = proxy.performAdd(user);
				
			MultiValueMap<String, String> headers = new HttpHeaders();
			headers.add("Location", generateLocationUri(hostName, resourceId));
			
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		} catch (AmqpException ex) {
			return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
	private String generateLocationUri(String hostName, String resourceId) {		
		RequestAttributes reqAttributes = RequestContextHolder.currentRequestAttributes();
		String lookupPath = reqAttributes.getAttribute(LOOKUP_PATH, 0).toString();
		
		StringBuilder sb = new StringBuilder();
		
		if (lookupPath != null && !lookupPath.isEmpty()) {
			if (hostName != null && !hostName.isEmpty()) {
				sb.append("http://");  // TODO: Remove this static string!
				sb.append(hostName);			
			}
			sb.append(lookupPath);
		}
		
		sb.append(resourceId);
		
		return sb.toString();
	}

	@PutMapping("/users/{userId}")
	public ResponseEntity<Void> update(@PathVariable String userId, @RequestBody User user) {
		try {
			if (user != null) {
				user.setId(UUID.fromString(userId));
			}
			proxy.performUpdate(user);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (AmqpException ex) {
			return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
		}	
	}

	@GetMapping("/users/{userId}")
	public User retrieve(@PathVariable String userId) {
		return proxy.performRetrieve(userId);
	}
	
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<Void> delete(@PathVariable String userId) {
		try {
			proxy.performDelete(userId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (AmqpException ex) {
			return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
		}		
	}
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> list(@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "sort", defaultValue = "") String sort,
			@RequestParam(value = "order", defaultValue = "") String order) {
		try {
			List<User> users = proxy.performSearch(keyword, sort, order);
			if (users != null) {
				return ResponseEntity.ok(users);
			} else {
				return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
			}
		} catch (AmqpException ex) {
			return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
		}
	}
	
	@GetMapping("/status")
	public Object status() {
		try {
			String token = proxy.performStatus();
			if (token != null && !token.isEmpty()) {
				return new Message("All systems are go! (" + LocalDateTime.now().toString() + ")");
			} else {
				return new Error("Backend service is down! (" + LocalDateTime.now().toString() + ")");
			}
		} catch (AmqpException ex) {
			return new Error("Message broker is down! (" + LocalDateTime.now().toString() + ")");
		}
	}
	
}