package com.github.gateway.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gateway.domain.Error;

@RestController
public class ExceptionController implements ErrorController {

	@Override
	@RequestMapping(path = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getErrorPath() {
		try {
			return new ObjectMapper().writeValueAsString(new Error());
		} catch (JsonProcessingException e) {
			return "{}";
		}
	}
	
}