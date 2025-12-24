package com.tuniway.tuniway.ServicesTest;

import com.tuniway.model.User;
import com.tuniway.repository.UserRepository;
import com.tuniway.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        List<User> expectedUsers = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result).contains(testUser);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WhenExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WhenNotExists_ShouldReturnEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999L);

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void getUserByUsername_WhenExists_ShouldReturnUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByUsername("testuser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void getUserByEmail_WhenExists_ShouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser(testUser);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateUser_ShouldSaveAndReturnUpdated() {
        testUser.setEmail("newemail@example.com");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(testUser);

        assertThat(result.getEmail()).isEqualTo("newemail@example.com");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void deleteUser_ShouldCallRepositoryDelete() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void existsByUsername_WhenExists_ShouldReturnTrue() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        boolean result = userService.existsByUsername("testuser");

        assertThat(result).isTrue();
        verify(userRepository, times(1)).existsByUsername("testuser");
    }

    @Test
    void existsByUsername_WhenNotExists_ShouldReturnFalse() {
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        boolean result = userService.existsByUsername("nonexistent");

        assertThat(result).isFalse();
        verify(userRepository, times(1)).existsByUsername("nonexistent");
    }

    @Test
    void existsByEmail_WhenExists_ShouldReturnTrue() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertThat(result).isTrue();
        verify(userRepository, times(1)).existsByEmail("test@example.com");
    }

    @Test
    void existsByEmail_WhenNotExists_ShouldReturnFalse() {
        when(userRepository.existsByEmail("none@example.com")).thenReturn(false);

        boolean result = userService.existsByEmail("none@example.com");

        assertThat(result).isFalse();
        verify(userRepository, times(1)).existsByEmail("none@example.com");
    }
}