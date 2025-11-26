package com.tuniway.service;

import com.tuniway.model.User;
import com.tuniway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {

        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {

        return userRepository.save(user);
    }

    public User updateUser(User user) {

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {

        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {

        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {

        return userRepository.existsByEmail(email);
    }
}