package com.bookshelf.bookshelf.controller;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookshelf.bookshelf.model.LoginRequest;
import com.bookshelf.bookshelf.model.RegisterRequest;
import com.bookshelf.bookshelf.model.User;
import com.bookshelf.bookshelf.repository.UserRepository;
import com.bookshelf.bookshelf.service.AuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  private static final String CLIENT_ID = "145922955760-uqke4thoftejou9cbvn2c9cmp6bjrj2f.apps.googleusercontent.com";
  @Autowired
  private UserRepository userRepository;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
    try {
      String token = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
      String jsonResponse = "{\"token\":\"" + token + "\"}";

      return ResponseEntity.ok(jsonResponse);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Credenciais inválidas\"}");
    }
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
    try {
      User user = authService.registerUser(registerRequest.getUsername(), registerRequest.getPassword());
      String token = authService.generateToken(user);
      String jsonResponse = "{\"token\":\"" + token + "\"}";

      return ResponseEntity.status(HttpStatus.CREATED).body(jsonResponse);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"Erro ao cadastrar usuário\"}");
    }
  }

  @PostMapping("/google")
  public ResponseEntity<String> authenticateWithGoogle(@RequestBody String idTokenString) {

    try {
      JsonFactory jsonFactory = new GsonFactory();
      HttpTransport httpTransport = new NetHttpTransport();
  
      GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
          .setAudience(Collections.singletonList(CLIENT_ID))
          .build();
      

      try {
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
      

          GoogleIdToken.Payload payload = idToken.getPayload();
          // String userId = payload.getSubject(); // No reason to use GoogleId right now. I'll leave it here in just in case.
          String email = payload.getEmail();
          String name = (String) payload.get("name");
  
          
          Optional<User> existingUser = userRepository.findByEmail(email);
  
          if (existingUser.isPresent()) {
            String token = authService.generateToken(existingUser.get());
            return ResponseEntity.ok("{\"token\":\"" + token + "\"}");
          } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name); 
            newUser.setPassword("");
            userRepository.save(newUser);

            String token = authService.generateToken(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("{\"token\":\"" + token + "\"}");
          }
        } else {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
      } catch (Exception err) {
    
      }
      
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao verificar o token: " + e.getMessage());
    }
  }

}
