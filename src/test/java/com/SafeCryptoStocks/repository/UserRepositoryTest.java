package com.SafeCryptoStocks.repository;

import com.SafeCryptoStocks.model.User;
import com.SafeCryptoStocks.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void testSaveUser() {
        // Create a new user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("Password@123");
        user.setEmail("testuser@example.com");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setAddress("123 Test Street");

        // Save the user
        User savedUser = userRepository.save(user);

        // Assert that the user was saved correctly
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    @Transactional
    void testFindUserByEmail() {
        // Create and save a user
        User user = new User();
        user.setUsername("finduser");
        user.setPassword("Password@123");
        user.setEmail("finduser@example.com");
        user.setFirstname("Find");
        user.setLastname("User");
        user.setAddress("456 Find Street");
        userRepository.save(user);

        // Retrieve the user by email
        User retrievedUser = userRepository.findByEmail("finduser@example.com");

        // Assert that the user was retrieved correctly
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo("finduser");
    }

    @Test
    @Transactional
    void testExistsByEmail() {
        // Create and save a user
        User user = new User();
        user.setUsername("existsuser");
        user.setPassword("Password@123");
        user.setEmail("existsuser@example.com");
        user.setFirstname("Exists");
        user.setLastname("User");
        user.setAddress("789 Exists Street");
        userRepository.save(user);

        // Check if the email exists
        boolean exists = userRepository.existsByEmail("existsuser@example.com");

        // Assert that the email exists
        assertThat(exists).isTrue();
    }

    @Test
    @Transactional
    void testDeleteUser() {
        // Create and save a user
        User user = new User();
        user.setUsername("deleteuser");
        user.setPassword("Password@123");
        user.setEmail("deleteuser@example.com");
        user.setFirstname("Delete");
        user.setLastname("User");
        user.setAddress("123 Delete Street");
        userRepository.save(user);

        // Delete the user
        userRepository.delete(user);

        // Check that the user no longer exists
        Optional<User> deletedUser = userRepository.findById(user.getId());
        assertThat(deletedUser).isEmpty();
    }
}
