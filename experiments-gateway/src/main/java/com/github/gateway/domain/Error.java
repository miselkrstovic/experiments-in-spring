package com.github.gateway.domain;

public class Error {
	
	private String error;
	
	public Error() {}
	
	public Error(String error) {
		super();
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
}
