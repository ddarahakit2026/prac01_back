package com.example.prac01_back.user;

import com.example.prac01_back.user.model.EmailVerify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerifyRepository extends JpaRepository<EmailVerify, Long> {

    Optional<EmailVerify> findByUuid(String uuid);
}
