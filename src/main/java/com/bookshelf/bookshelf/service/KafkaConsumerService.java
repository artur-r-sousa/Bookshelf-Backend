package com.bookshelf.bookshelf.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import com.bookshelf.bookshelf.model.ConfirmationToken;
import com.bookshelf.bookshelf.model.User;
import com.bookshelf.bookshelf.repository.ConfirmationTokenRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumerService {

  @Autowired
  private ConfirmationTokenRepository confirmationTokenRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private EmailService emailService;

  @KafkaListener(topics = "bookshelf-topic", groupId = "bookshelf-email-group")
  public void listenEmailMessage(String message) {

    try {
      Map<String, String> emailData = objectMapper.readValue(message, new TypeReference<Map<String, String>>() {
      });
      String token = emailData.get("token");

      ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);
      if (confirmationToken != null && confirmationToken.getExpiredAt().isAfter(LocalDateTime.now())
          && !confirmationToken.isConfirmed()) {

        User user = confirmationToken.getUser();
        String confirmationLink = "http://localhost:4200/confirm?token=" + token;
        try {
          emailService.sendConfirmationEmail(user.getEmail(), user.getUsername(), confirmationLink, "confirmation-email");
        } catch (MessagingException e) {
          e.printStackTrace();
        }
        
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @KafkaListener(topics = "bookshelf-reset-password-topic", groupId = "bookshelf-email-group")
  public void listenResetPassword(String message) {

    try {
      Map<String, String> emailData = objectMapper.readValue(message, new TypeReference<Map<String, String>>() {
      });
      String token = emailData.get("token");

      ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);
      if (confirmationToken != null && confirmationToken.getExpiredAt().isAfter(LocalDateTime.now())
          && !confirmationToken.isConfirmed()) {

        User user = confirmationToken.getUser();
        String confirmationLink = "http://localhost:4200/recovery?token=" + token;
        try {
          emailService.sendConfirmationEmail(user.getEmail(), user.getUsername(), confirmationLink, "recover-password");
        } catch (MessagingException e) {
          e.printStackTrace();
        }
        
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
