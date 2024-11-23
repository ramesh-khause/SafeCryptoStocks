package com.SafeCryptoStocks.model;

import java.util.List;

import javax.validation.constraints.NotBlank;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "Name field is required!!")
    @Size(min = 5, max = 10, message = "Min 5 and max 10 characters are allowed!!")
    @Column(unique = true, nullable = false)
    private String username;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    @Column(nullable = false)
    private String password;

    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "Invalid email format"
    )
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "First name is required!!")
    @Size(min = 3, max = 10, message = "Min 3 and max 10 characters are allowed!!")
    @Column(nullable = false)
    private String firstname;

    @NotBlank(message = "Last name is required!!")
    @Size(min = 3, max= 10, message = "Min 3 and max 10 characters are allowed!!")
    @Column(nullable = false)
    private String lastname;

    @NotBlank(message = "Address is required!!")
    @Size(min = 3, max = 20, message = "Min 3 and max 20 characters are allowed!!")
    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String role = "USER"; // Default role

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Portfolio> portfolios;

    // Default Constructor
    public User() {
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    // toString method
    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", email=" + email + ", firstname=" + firstname
                + ", lastname=" + lastname + ", address=" + address + ", role=" + role + "]";
    }
}
