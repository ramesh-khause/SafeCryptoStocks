package com.SafeCryptoStocks.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    private long id;

    @NotBlank(message = "UserName field is required!!")
    @Size(min = 5, max = 10, message = "Min 5 and max 10 characters are allowed!!")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required!!")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Email is required!!")
    @Pattern(
    	    regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.com$",
    	    message = "Email must contain @ and domain name..."
    	)
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "First name is required!!")
    @Size(min = 3, max = 10, message = "Min 3 and max 10 characters are allowed!!")
    @Column(nullable = false)
    private String firstname;

    @NotBlank(message = "Last name is required!!")
    @Size(min = 3, max = 10, message = "Min 3 and max 10 characters are allowed!!")
    @Column(nullable = false)
    private String lastname;

    @NotBlank(message = "Address is required!!")
    @Size(min = 3, max = 20, message = "Min 3 and max 20 characters are allowed!!")
    @Column(nullable = false)
    private String address;


    @Column
   private String profilePicturePath="/image/profileicon.png";

   // Getter and Setter for profilePicturePath
   public String getProfilePicturePath() {
       return profilePicturePath;
   }

   public void setProfilePicturePath(String profilePicturePath) {
       this.profilePicturePath = profilePicturePath;
   }

 


	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email
				+ ", firstname=" + firstname + ", lastname=" + lastname + ", address=" + address
				+ ", profilePicturePath=" + profilePicturePath + "]";
	}

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Portfolio> portfolios;
    
    
	

    
   

    // Default Constructor
    public User() {}

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

   

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

   
}
