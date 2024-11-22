package com.bookshelf.bookshelf.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaProducerService {

  private static final String TOPIC = "bookshelf-topic";
  private static final String confirmationURL = "http://localhost:8080/confirm?token=";

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  public void sendConfirmationEmail(String to, String subject, String name, String confirmationToken) {
    String confirmationLink = confirmationURL + confirmationToken;
    Map<String, String> extraData = new HashMap<>();
    extraData.put("token", confirmationToken);

    String content = "Olá " + name + ",\n\nPor favor, clique no link abaixo para confirmar seu cadastro:\n"
        + confirmationLink;
    sendEmail(to, subject, content, extraData);
  }

  public void sendEmail(String to, String subject, String content, Map<String, String> extraData) {
    try {
      String message = mountEmailData(to, subject, content, extraData);
      kafkaTemplate.send(TOPIC, message);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sendResetPasswordConfirmationEmail(String to, String subject, String content, Map<String, String> extraData) {
    try {
      String message = mountEmailData(to, subject, content, extraData);
      kafkaTemplate.send("bookshelf-reset-password-topic", message);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String mountEmailData(String to, String subject, String content, Map<String, String> extraData) {

    Map<String, String> emailData = new HashMap<>();
    emailData.put("to", to);
    emailData.put("subject", subject);
    emailData.put("content", content);

    if (extraData != null && !extraData.isEmpty()) {
      emailData.putAll(extraData);
    }

    String message = null;
    try {
      message = objectMapper.writeValueAsString(emailData);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return message;
  }
}
