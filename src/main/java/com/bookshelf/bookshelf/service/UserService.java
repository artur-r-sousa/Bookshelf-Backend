package com.bookshelf.bookshelf.service;

import com.bookshelf.bookshelf.model.User;
import com.bookshelf.bookshelf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
  }  

  public User saveUser(User user) {
    return userRepository.save(user);
  }

  public String encryptPassword(String password) {
    return passwordEncoder.encode(password);
  }
}
