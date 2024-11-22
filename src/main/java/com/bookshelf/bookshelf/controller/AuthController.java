package com.bookshelf.bookshelf.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookshelf.bookshelf.model.ConfirmationToken;
import com.bookshelf.bookshelf.model.LoginRequest;
import com.bookshelf.bookshelf.model.RegisterRequest;
import com.bookshelf.bookshelf.model.UpdatePasswordRequest;
import com.bookshelf.bookshelf.model.User;
import com.bookshelf.bookshelf.repository.UserRepository;
import com.bookshelf.bookshelf.service.AuthService;
import com.bookshelf.bookshelf.service.ConfirmationTokenService;
import com.bookshelf.bookshelf.service.KafkaProducerService;
import com.bookshelf.bookshelf.service.UserService;
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

  @Autowired
  private KafkaProducerService kafkaProducerService;

  @Autowired
  private ConfirmationTokenService confirmationTokenService;

  @Autowired
  private UserService userService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
    try {
      String token = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword(),
          loginRequest.getEmail());
      String jsonResponse = "{\"token\":\"" + token + "\"}";

      return ResponseEntity.ok(jsonResponse);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Credenciais inválidas\"}");
    }
  }

  @PostMapping("/register")
  @Transactional
  public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest registerRequest) {
    Map<String, String> response = new HashMap<>();
    try {
      User user = authService.registerUser(registerRequest.getUsername(), registerRequest.getPassword(),
          registerRequest.getEmail());
      ConfirmationToken confirmationToken = confirmationTokenService.generateConfirmationToken(user);
      kafkaProducerService.sendConfirmationEmail(
          user.getEmail(),
          "Email de Ativacao",
          user.getUsername(),
          confirmationToken.getToken());

      response.put("message", "Usuário criado. Por favor, confirme seu e-mail.");
      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (Exception e) {
      response.put("message", "Erro ao cadastrar usuário");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  @PostMapping("/google")
  @Transactional
  public ResponseEntity<Map<String, String>> authenticateWithGoogle(@RequestBody String idTokenString) {
    Map<String, String> response = new HashMap<>();
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
          String email = payload.getEmail();
          String name = (String) payload.get("name");

          Optional<User> existingUser = userRepository.findByEmail(email);
          User user;
          if (existingUser.isPresent()) {
            user = existingUser.get();
            if (user.getIsActive()) {
              String token = authService.generateToken(user);
              response.put("token", token);
              return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
            } else {
              response.put("message", "Erro ao cadastrar usuário");
              return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                  .body(response);
            }
          } else {
            user = authService.registerUser(name, "", email);
            ConfirmationToken confirmationToken = confirmationTokenService.generateConfirmationToken(user);
            kafkaProducerService.sendConfirmationEmail(
                email,
                "Email de Ativacao",
                name,
                confirmationToken.getToken());

            response.put("message", "Usuário criado. Por favor, confirme seu e-mail.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
          }
        } else {
          response.put("message", "Token inválido");
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
      } catch (Exception err) {
        response.put("message", "Token inválido");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
      }
    } catch (Exception e) {
      response.put("message", "Erro ao verificar o token: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  @PostMapping("/recover-password")
  public ResponseEntity<Map<String, String>> recoverPassword(@RequestBody RegisterRequest email) {
    Map<String, String> response = new HashMap<>();
    Optional<User> user = userService.getUserByEmail(email.getEmail());
    if (user.isPresent()) {
      User existingUser = user.get();
      ConfirmationToken token = confirmationTokenService.generateConfirmationToken(existingUser);
      String resetLink = "http://localhost:4200/reset-password?token=" + token.getToken();
      String subject = "Recuperação de Senha";
      String content = "Olá " + existingUser.getUsername() + ",\n\n" +
          "Clique no link abaixo para redefinir sua senha:\n" + resetLink;

      Map<String, String> extraData = new HashMap<>();
      extraData.put("token", token.getToken());
      kafkaProducerService.sendResetPasswordConfirmationEmail("artursousa505@gmail.com", subject, content, extraData);
      response.put("message", "Email de recuperação enviado.");
      return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    } else {
      response.put("message", "Email não encontrado.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
  }

  public void updatePassword(String newPassword, String token) {
    Optional<Long> userId = confirmationTokenService.getUserIdByToken(token);
    if (userId.isEmpty()) {
      throw new IllegalStateException("Token inválido ou expirado.");
    }

    User user = userService.getUserById(userId.get())
        .orElseThrow(() -> new IllegalStateException("Usuário não encontrado para o ID: " + userId.get()));

    if (user.getEmail() == null) {
      throw new IllegalStateException("Email do usuário está nulo.");
    }

    authService.updateUserPassword(newPassword, user.getEmail());
  }

  @PostMapping("/update-password")
  public ResponseEntity<Map<String, String>> updateUser(@RequestBody UpdatePasswordRequest credentials) {
    Map<String, String> response = new HashMap<>();
    updatePassword(credentials.getNewPassword(), credentials.getToken());
    ConfirmationToken tokenToBeDeleted = confirmationTokenService.getToken(credentials.getToken()).get();
    confirmationTokenService.deleteToken(tokenToBeDeleted);
    response.put("message", "Usuario atualizado");
    
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
  }

}
