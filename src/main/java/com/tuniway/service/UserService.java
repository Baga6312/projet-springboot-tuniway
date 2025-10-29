package com.tuniway.service;

import com.tuniway.model.Utilisateur;
import com.tuniway.model.Client;
import com.tuniway.model.Guide;
import com.tuniway.model.Admin;
import com.tuniway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<Utilisateur> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Utilisateur> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<Utilisateur> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<Utilisateur> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Utilisateur createUser(Utilisateur user) {
        return userRepository.save(user);
    }

    public Utilisateur updateUser(Utilisateur user) {
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