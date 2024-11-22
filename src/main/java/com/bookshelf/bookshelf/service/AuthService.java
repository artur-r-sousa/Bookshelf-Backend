package com.bookshelf.bookshelf.service;

import com.bookshelf.bookshelf.model.User;
import com.bookshelf.bookshelf.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${jwt.secret}")
  private String jwtSecret;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public User saveUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  public User registerUser(String username, String password, String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new RuntimeException("Usuário já existe");
    }

    User newUser = new User();
    newUser.setUsername(username);
    newUser.setPassword(password);
    newUser.setEmail(email);
    newUser.setIsActive(false);

    saveUser(newUser);

    return newUser;
  }

  public User updateUserPassword(String password, String email) {
    User existingUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuário não existe"));

    existingUser.setPassword(passwordEncoder.encode(password));

    return userRepository.save(existingUser);
  }

  public String authenticate(String username, String password, String email) throws Exception {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new Exception("Usuário não encontrado"));
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new Exception("Credenciais inválidas");
    }

    return generateToken(user);
  }

  public String generateToken(User user) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + 86400000);

    SecretKey key = null;
    key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    return Jwts.builder()
        .setSubject(user.getUsername())
        .setSubject(user.getEmail())
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS512)
        .compact();
  }
}
