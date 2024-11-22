package com.bookshelf.bookshelf.repository;

import com.bookshelf.bookshelf.model.ConfirmationToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByToken(String token);

    @Query("SELECT ct.user.id FROM ConfirmationToken ct WHERE ct.token = :token")
    Long findUserIdByToken(@Param("token") String token);
    
}
