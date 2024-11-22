package com.bookshelf.bookshelf.service;

import com.bookshelf.bookshelf.model.User;
import com.bookshelf.bookshelf.model.ConfirmationToken;
import com.bookshelf.bookshelf.repository.ConfirmationTokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConfirmationTokenService {

  @Autowired
  private ConfirmationTokenRepository confirmationTokenRepository;

  public ConfirmationToken generateConfirmationToken(User user) {
    ConfirmationToken confirmationToken = new ConfirmationToken();
    confirmationToken.setUser(user);
    confirmationToken.setToken(UUID.randomUUID().toString());
    confirmationToken.setCreatedAt(LocalDateTime.now());
    confirmationToken.setExpiredAt(LocalDateTime.now().plusHours(24)); 
    confirmationToken.setConfirmed(false); 

    confirmationToken = confirmationTokenRepository.save(confirmationToken); 
    return confirmationToken; 
  }

  public Optional<ConfirmationToken> getToken(String token) {
    return Optional.ofNullable(confirmationTokenRepository.findByToken(token));
  }

  public Optional<Long> getUserIdByToken(String token) {
    return Optional.ofNullable(confirmationTokenRepository.findUserIdByToken(token));
  }

  public void deleteToken(ConfirmationToken token) {
    confirmationTokenRepository.delete(token);
  }

}
