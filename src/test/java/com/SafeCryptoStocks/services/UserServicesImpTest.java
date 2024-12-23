package com.SafeCryptoStocks.services;

import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServicesImpTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private UserServicesImp userServices;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword123"); // Mocked password hash (this could be any hash in practice)
    }

    // Test cases follow

    @Test
    void testRegisterUser() {
        // Arrange
        User userToRegister = new User();
        userToRegister.setEmail("test@example.com");
        userToRegister.setPassword("password123");

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User registeredUser = userServices.registerUser(userToRegister);

        // Assert
        assertNotNull(registeredUser);
        assertEquals("test@example.com", registeredUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class)); // Ensure save method was called
    }

    @Test
    void testCheckEmailExists() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        boolean exists = userServices.checkEmail("test@example.com");

        // Assert
        assertTrue(exists);
        verify(userRepository, times(1)).existsByEmail("test@example.com");
    }

    @Test
    void testFindByEmail() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        // Act
        User foundUser = userServices.findByEmail("test@example.com");

        // Assert
        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testValidateUserLoginValid() {
        // Arrange
        String plainPassword = "password123"; // The plain password used for comparison
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        // Mock the behavior of BCrypt.checkpw to return true when passwords match
        try (MockedStatic<BCrypt> bcryptMock = Mockito.mockStatic(BCrypt.class)) {
            bcryptMock.when(() -> BCrypt.checkpw(plainPassword, testUser.getPassword())).thenReturn(true);

            // Act
            boolean isValid = userServices.validateUserLogin("test@example.com", plainPassword);

            // Assert
            assertTrue(isValid);
            verify(userRepository, times(1)).findByEmail("test@example.com");
        }
    }

    @Test
    void testValidateUserLoginInvalid() {
        // Arrange
        String plainPassword = "wrongpassword";
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        // Mock the behavior of BCrypt.checkpw to return false when passwords don't match
        try (MockedStatic<BCrypt> bcryptMock = Mockito.mockStatic(BCrypt.class)) {
            bcryptMock.when(() -> BCrypt.checkpw(plainPassword, testUser.getPassword())).thenReturn(false);

            // Act
            boolean isValid = userServices.validateUserLogin("test@example.com", plainPassword);

            // Assert
            assertFalse(isValid);
            verify(userRepository, times(1)).findByEmail("test@example.com");
        }
    }

    @Test
    void testUpdatePassword() {
        // Arrange
        String newPassword = "newPassword123";
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        userServices.updatePassword(testUser, newPassword);

        // Assert
        assertNotNull(testUser);
        assertTrue(BCrypt.checkpw(newPassword, testUser.getPassword())); // Check if the password was hashed
        verify(userRepository, times(1)).save(testUser);
    }



    @Test
    void testFindById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));

        // Act
        User foundUser = userServices.findById(1L);

        // Assert
        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        // Act
        User foundUser = userServices.findById(999L);

        // Assert
        assertNull(foundUser);
        verify(userRepository, times(1)).findById(999L);
    }


    @Test
    void testVerifyAuthenticationSuccess() {
      

        // Mock AuthenticationManager to return an authenticated user
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        

        // Act
        String actualToken = userServices.verify(testUser);

        
    }

    @Test
    void testVerifyAuthenticationFail() {
        // Arrange
        // Mock AuthenticationManager to simulate failed authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        // Act
        String result = userServices.verify(testUser);

        // Assert
        assertEquals("fail", result); // Should return "fail" if authentication fails
    }

    @Test
    void testVerifyAuthenticationException() {
        // Arrange
        // Simulate an exception thrown by the AuthenticationManager during authentication
        when(authManager.authenticate(any(Authentication.class))).thenThrow(new RuntimeException("Authentication failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userServices.verify(testUser)); // Should throw an exception
    }
}


