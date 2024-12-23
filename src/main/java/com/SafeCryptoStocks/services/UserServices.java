package com.SafeCryptoStocks.services;


import com.SafeCryptoStocks.model.User;

public interface UserServices {
	public User registerUser(User user);
	public boolean checkEmail(String email);
	public User findByEmail(String email);
	public boolean validateUserLogin(String email, String password);
	void updatePassword(User user, String newPassword);

	public String verify(User user);
	
	 public User findById(Long id);
	 
	 public void updateUserProfile(User user);
}

