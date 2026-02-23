package org.example.spring03.user;

import org.example.spring03.user.model.EmailVerify;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerifyRepository extends JpaRepository<EmailVerify, Long> {

}
