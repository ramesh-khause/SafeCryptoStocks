package com.SafeCryptoStocks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.model.UserPrincipal;
import com.SafeCryptoStocks.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for session-based authentication
            .cors(Customizer.withDefaults()) // Enable CORS support
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login", "/signup", "/registerUser", "/loginUser", "/forgotPwd", "/forgotPassword",
                    "/create-portfolio", "/learn", "/dash/trending-cryptocurrency", "/dash/crypto-news", "/dash/cryptocurrency", 
                    "/cryptocurrency", "/api/cryptocurrencies", "/budget", "/market", "/portfolio", "/verifyPasswordResetOtp", 
                    "/verifyOtp", "/resetPassword", "/verify-otp", "/portfolios", "/portfolios/**", "/dashboard", "/dashboard/**",
                    "/stock", "/stock/**", "/css/**", "/js/**", "/image/**","/pic/**", "/webjars/**", "/dummy-stock", "/create-portfolio/{userId}",
                    "/selectQuantity", "/dummy-stock/update", "/stock/bulk-insert/**", "/budgets", "/budgets/**", "/budgets/{id}/expenses", 
                    "/api/**", "/profile", "/uploads/**","/uploadProfilePicture","/home","/**","/security"
                ).permitAll() // Allow public endpoints without authentication
                .anyRequest().authenticated() // Require authentication for all other requests
            
            		)
            .logout(logout -> logout
                .logoutUrl("/logout") // URL for logging out
                .logoutSuccessUrl("/home?logout") // Redirect URL after logout
                .invalidateHttpSession(true) // Invalidate the session
                .deleteCookies("JSESSIONID") // Delete session cookie after logout
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // Create sessions as required
            .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12)); // Set password encoder
        provider.setUserDetailsService(userDetailsService()); // Set user details service
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8080"); // Frontend origin
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByEmail(username); // Look for user by email
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
            return new UserPrincipal(user.getId(), user.getUsername(), user.getPassword()); // Return user details
        };
    }
}
