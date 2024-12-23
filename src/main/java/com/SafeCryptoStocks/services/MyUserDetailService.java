package com.SafeCryptoStocks.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.model.UserPrincipal;
import com.SafeCryptoStocks.repository.UserRepository;

@Service
public class MyUserDetailService implements UserDetailsService{


	@Autowired
	UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		User user=userRepo.findByEmail(username);
		if(user==null)
		{
			System.out.println("user not found");
			throw new UsernameNotFoundException("user not found");
		}
		
		return new UserPrincipal(user.getId(), user.getUsername(), user.getPassword());
		//return new UserPrincipal(user);
	}

}
