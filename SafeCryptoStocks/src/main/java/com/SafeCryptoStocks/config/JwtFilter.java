package com.SafeCryptoStocks.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.SafeCryptoStocks.services.JwtService;
import com.SafeCryptoStocks.services.MyUserDetailService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter  {

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	ApplicationContext context; 
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String authHeader=request.getHeader("Authorization");
		String token=null;
		String username=null;
		
		// Get the requested URI
        String requestURI = request.getRequestURI();
		
		
		
		if(authHeader!=null && authHeader.startsWith("bearer"))
		{
			token=authHeader.substring(7);
			username=jwtService.extractUserName(token);
		}

		  if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            UserDetails userDetails = context.getBean(MyUserDetailService.class).loadUserByUsername(username);
	            if (jwtService.validateToken(token, userDetails)) {
	                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	                authToken.setDetails(new WebAuthenticationDetailsSource()
	                        .buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(authToken);
	            }
	        }

	       
		
		
	//	==============================================
		

	
	
	     // Step 1: Skip JWT validation for public routes
	        if (requestURI.equals("/registerUser") || requestURI.equals("/signup")||
	     	 requestURI.equals("/forgotPassword")|| requestURI.equals("/verifyPasswordResetOtp")	
	        	||requestURI.equals("/login")	
	        	||requestURI.equals("/verifyOtp")||requestURI.equals("/restePassword")
	     	) 
	        {
	            
	            // Skip JWT filter logic for these routes and continue the filter chain
	            filterChain.doFilter(request, response);
	            return;
	        }

	        // Step 2: Extract the JWT token from the Authorization header
	        String authHeader1 = request.getHeader("Authorization");
	        String token1 = null;
	        String username1 = null;

	        if (authHeader1 != null && authHeader1.startsWith("bearer ")) {
	            token1 = authHeader1.substring(7);  // Remove "bearer " prefix
	            username1 = jwtService.extractUserName(token1);  // Extract username from the token
	        }

	        // Step 3: If a token is present and there's no authentication in SecurityContext
	        if (username1 != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            UserDetails userDetails = context.getBean(MyUserDetailService.class).loadUserByUsername(username1);
	            if (jwtService.validateToken(token1, userDetails)) {
	                // If the token is valid, set the authentication in SecurityContext
	                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
	                    userDetails, null, userDetails.getAuthorities());
	                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(authToken);
	            }
	        }

	        // Step 4: Continue the filter chain after authentication logic
	        filterChain.doFilter(request, response);
	    }
	
	}


