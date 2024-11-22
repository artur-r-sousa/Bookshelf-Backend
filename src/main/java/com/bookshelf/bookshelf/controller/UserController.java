package com.bookshelf.bookshelf.controller;

import com.bookshelf.bookshelf.model.ConfirmationToken;
import com.bookshelf.bookshelf.model.User;
import com.bookshelf.bookshelf.service.AuthService;
import com.bookshelf.bookshelf.service.ConfirmationTokenService;
import com.bookshelf.bookshelf.service.KafkaProducerService;
import com.bookshelf.bookshelf.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired
  private ConfirmationTokenService confirmationTokenService;

  @Autowired
  private UserService userService;

  @Autowired
  private AuthService authService;

  @Autowired
  private KafkaProducerService kafkaProducerService;

  @GetMapping("/confirm")
  public ResponseEntity<Object> confirmUser(@RequestParam("token") String token) {
    Optional<ConfirmationToken> confirmationToken = confirmationTokenService.getToken(token);
    if (confirmationToken.isPresent()) {
      Optional<User> user = userService.getUserById(confirmationToken.get().getUser().getId());

      if (user.isPresent()) {
        User existingUser = user.get();
        existingUser.setIsActive(true); 
        userService.saveUser(existingUser);

        confirmationTokenService.deleteToken(confirmationToken.get());

        Map<String, Object> response = new HashMap<>();
        response.put("userId", existingUser.getId());
        response.put("email", existingUser.getEmail());
        response.put("name", existingUser.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(response);
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
      }
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido.");
    }
  }

  @PostMapping("/recover-password")
  public ResponseEntity<String> recoverPassword(@RequestParam("email") String email) {
    Optional<User> user = userService.getUserByEmail(email);

    if (user.isPresent()) {
      User existingUser = user.get();

      ConfirmationToken token = confirmationTokenService.generateConfirmationToken(existingUser);
      String resetLink = "http://localhost:4200/reset-password?token=" + token.getToken();

      String subject = "Recuperação de Senha";
      String content = "Olá " + existingUser.getUsername() + ",\n\n" +
          "Clique no link abaixo para redefinir sua senha:\n" + resetLink;

      Map<String, String> extraData = new HashMap<>();
      extraData.put("token", token.getToken());

      kafkaProducerService.sendEmail(existingUser.getEmail(), subject, content, extraData);

      return ResponseEntity.ok("Email de recuperação enviado.");
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email não encontrado.");
    }
  }

  public void updatePassword(String email, String newPassword) {
    Optional<User> user = userService.getUserByEmail(email);

    if (user.isPresent()) {
      authService.updateUserPassword(newPassword, email);
    }
  }

  @PostMapping("/update-password")
  public ResponseEntity<String> updateUser(String email, String newPassword, ConfirmationToken token) {
    updatePassword(email, newPassword);
    confirmationTokenService.deleteToken(token);
    return ResponseEntity.ok("Usuario atualizado");
  }
}
