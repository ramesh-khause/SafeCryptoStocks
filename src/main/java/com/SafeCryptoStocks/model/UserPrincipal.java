package com.SafeCryptoStocks.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.jackson2.SimpleGrantedAuthorityMixin;

public class UserPrincipal implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	////////////

	private User user;
	
	public UserPrincipal(User user)
	{
	
		super();
		// TODO Auto-generated constructor stub
	
	    this.user=user;
	   
	}
	
	 public UserPrincipal(long id, String username, String password) {
		// TODO Auto-generated constructor stub
	     this.user.setId(id);
	     this.user.setEmail(username);
	     this.user.setPassword(password);
	 }

	public Long getId() {
	        return user.getId(); // Access the User entity's ID
	    }
	
	
	 public com.SafeCryptoStocks.model.User getUser() {
	        return user;
	    }
	 

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return Collections.singleton(new SimpleGrantedAuthority("USER"));
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getEmail();
	}

}
