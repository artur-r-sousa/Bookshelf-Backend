package com.bookshelf.bookshelf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class BookshelfApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(BookshelfApplication.class, args);
	}

  @Override
  public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**")
          .allowedOrigins("https://f1bc-45-169-175-152.ngrok-free.app", "http://localhost:4200", "http://127.0.0.1:4200")
          .allowedHeaders("*")
          .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
          .allowCredentials(true)
          .maxAge(3600);
  }
  

}
  