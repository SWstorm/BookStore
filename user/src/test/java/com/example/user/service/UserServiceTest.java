package com.example.user.service;

import com.example.user.model.User;
import com.example.user.repositroty.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setMiddleName("M");
        user.setLogin("jdoe");
        user.setRole("USER");
        user.setPassword("password");
    }

    @Test
    void createUser_shouldSaveUser() {
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals(user.getId(), createdUser.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> allUsers = userService.getAllUsers();

        assertNotNull(allUsers);
        assertEquals(1, allUsers.size());
        assertEquals(user.getId(), allUsers.get(0).getId());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_shouldReturnEmptyIfNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(1L);

        assertFalse(foundUser.isPresent());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void updateUser_shouldUpdateAndReturnUser() {
        User updatedDetails = new User();
        updatedDetails.setFirstName("Jane");
        updatedDetails.setLastName("Doe");
        updatedDetails.setMiddleName("A");
        updatedDetails.setLogin("janedoe");
        updatedDetails.setRole("ADMIN");
        updatedDetails.setPassword("newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedDetails);

        User updatedUser = userService.updateUser(1L, updatedDetails);

        assertNotNull(updatedUser);
        assertEquals("Jane", updatedUser.getFirstName());
        assertEquals("Doe", updatedUser.getLastName());
        assertEquals("A", updatedUser.getMiddleName());
        assertEquals("janedoe", updatedUser.getLogin());
        assertEquals("ADMIN", updatedUser.getRole());
        assertEquals("newpassword", updatedUser.getPassword());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_shouldThrowExceptionIfUserNotFound() {
        User updatedDetails = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, updatedDetails));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

}