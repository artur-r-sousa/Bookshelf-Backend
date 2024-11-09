package com.bookshelf.bookshelf.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;
  private String author;
  private double price;

  public Long getId() {
    return this.id;
  }

  public void setId(Long args) {
    this.id = args;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String args) {
    this.title = args;
  }

  public String getAuthor() {
    return this.author;
  }

  public void setAuthor(String args) {
    this.author = args;
  }

  public double getPrice() {
    return this.price;
  }

  public void setPrice(double args) {
    this.price = args;
  }
}
