package com.bookshelf.bookshelf.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = true)
  private String password;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = true)
  private String whatsapp;

  @Column(nullable = false)
  private Boolean isActive;

  public Long getId() {
    return this.id;
  }

  public void setId(Long args) {
    this.id = args;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String args) {
    this.username = args;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String args) {
    this.password = args;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String args) {
    this.email = args;
  }

  public String getWhatsapp() {
    return this.whatsapp;
  }

  public void setWhatsapp(String args) {
    this.whatsapp = args;
  }

  public Boolean getIsActive() {
    return this.isActive;
  }

  public void setIsActive(Boolean args) {
    this.isActive = args;
  }
}
