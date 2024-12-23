package com.SafeCryptoStocks.controller;

import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.services.EmailService;
import com.SafeCryptoStocks.services.UserServices;
import com.SafeCryptoStocks.utils.OtpUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceView;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServices userServices;

    @MockBean
    private EmailService emailService;

    @InjectMocks
    private HomeController homeController;

    private User testUser;

    private Map<String, String> otpStorage;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");

        otpStorage = new HashMap<>();
        otpStorage.put("test@example.com", "123456");

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(homeController)
                .setViewResolvers((viewName, locale) -> new InternalResourceView("/WEB-INF/views/" + viewName + ".jsp"))
                .build();
    }

    // Test for /login endpoint
    @Test
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("user"));
    }

    // Test for /signup endpoint
    @Test
    void testSignupPage() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setUsername("john_doe");
        user.setPassword("password");
        user.setEmail("john.doe@example.com");
        user.setAddress("123 Main St");

        when(userServices.registerUser(any(User.class))).thenReturn(user);
        when(emailService.createRegistrationEmailBody("John", "Doe")).thenReturn("Email Body");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful...");
        response.put("messageType", "success");

        mockMvc.perform(post("/registerUser")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", user.getUsername())
                .param("password", user.getPassword())
                .param("email", user.getEmail())
                .param("firstname", user.getFirstname())
                .param("lastname", user.getLastname())
                .param("address", user.getAddress()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful..."))
                .andExpect(jsonPath("$.messageType").value("success"));
    }

    // Test for /registerUser endpoint - Email Conflict
    
    // Test for /loginUser endpoint - Success
    @Test
    void testLoginUserSuccess() throws Exception {
        when(userServices.validateUserLogin(anyString(), anyString())).thenReturn(true);
        when(userServices.findByEmail(anyString())).thenReturn(testUser);

        String userJson = """
                {
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/loginUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OTP sent to your email..."));

        verify(emailService, times(1)).sendOtpEmail(anyString(), anyString(), anyString());
    }

    // Test for /loginUser endpoint - Invalid Credentials
    @Test
    void testLoginUserInvalidCredentials() throws Exception {
        when(userServices.validateUserLogin(anyString(), anyString())).thenReturn(false);

        String userJson = """
                {
                    "email": "test@example.com",
                    "password": "wrongPassword"
                }
                """;

        mockMvc.perform(post("/loginUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials!"));
    }

   

    
    // Test for /forgotPassword endpoint
    @Test
    void testForgotPassword() throws Exception {
        when(userServices.checkEmail(anyString())).thenReturn(true);

        String requestJson = """
                {
                    "email": "test@example.com"
                }
                """;

        mockMvc.perform(post("/forgotPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password reset OTP sent to your email."));
    }

    // Test for /resetPassword endpoint
    @Test
    void testResetPassword() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userEmail", "test@example.com");

        String requestJson = """
                {
                    "newPassword": "newPassword123"
                }
                """;

        when(userServices.findByEmail(anyString())).thenReturn(testUser);

        mockMvc.perform(post("/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password reset successful."));
    }


 
    
}
