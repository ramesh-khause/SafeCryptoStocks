package com.SafeCryptoStocks.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Article {
    public Article() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String category;
    private String link;
    
    
    
    
    public Long getId() {
		return id;
	}
    
	public Article(Long id, String title, String category, String link) {
		super();
		this.id = id;
		this.title = title;
		this.category = category;
		this.link = link;
	}


	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
    public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	// Getters and Setters
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
}
