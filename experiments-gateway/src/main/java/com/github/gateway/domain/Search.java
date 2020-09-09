package com.github.gateway.domain;

public class Search {

	private String keyword;
	private String sort;
	private String order;
	
	public Search() {}
	
	public Search(String keyword, String sort, String order) {
		super();
		this.keyword = keyword;
		this.sort = sort;
		this.order = order;
	}

	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getSort() {
		return sort;
	}
	
	public void setSort(String sort) {
		this.sort = sort;
	}
	
	public String getOrder() {
		return order;
	}
	
	public void setOrder(String order) {
		this.order = order;
	}

}
