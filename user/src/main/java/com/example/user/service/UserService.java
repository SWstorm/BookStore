package com.example.user.service;

import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public User save (User user) {
        return repository.save(user);
    }

    public User register(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Шифруем пароль перед сохранением
        user.setRole(Role.ROLE_USER); // Устанавливаем роль по умолчанию
        return save(user);
    }
    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    public User updateUser(Long id, User userDetails) {
        User existingUser = getById(id);
        existingUser.setFirstname(userDetails.getFirstname());
        existingUser.setSurname(userDetails.getSurname());
        existingUser.setLastname(userDetails.getLastname());
        existingUser.setUsername(userDetails.getUsername());
        if (userDetails.getRole() != Role.ROLE_ADMIN || userDetails.getRole() != Role.ROLE_USER){
            existingUser.setRole(existingUser.getRole());
        }
        existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        return repository.save(existingUser);
    }
    public boolean userExists(String username) {
        return repository.existsByUsername(username);
    }
    public void deleteUser(Long id) {
        User user = getById(id);
        repository.delete(user);
    }

    @Deprecated
    public void getAdmin() {
        var user = getCurrentUser();
        user.setRole(Role.ROLE_ADMIN);
        save(user);
    }

}
