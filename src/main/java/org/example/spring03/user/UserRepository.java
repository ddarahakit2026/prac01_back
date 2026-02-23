package org.example.spring03.user;

import org.example.spring03.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 너가 원하는 특정 속성으로 SQL을 실행하고 싶으면 특정 규칙대로 메소드를 선언만 해라

    // 메소드 이름의 시작을 find로 하면 SELECT
    // find 뒤에 By를 추가하면 WHERE
    // By뒤에 엔티티의 특정 변수이름을 추가하면 WHERE의 조건으로 추가
    // SELECT * FROM user WHERE email=?
    Optional<User> findByEmail(String email);

    // 메소드 이름의 시작을 find로 하면 SELECT
    // find 뒤에 By를 추가하면 WHERE
    // By뒤에 엔티티의 특정 변수이름을 추가하면 WHERE의 조건으로 추가
    // AND, OR 카멜 케이스로 추가 가능
    // SELECT * FROM user WHERE email=? AND password=?
    Optional<User> findByEmailAndPassword(String email, String password);

    // 메소드 이름의 시작을 find로 하면 SELECT
    // findAll 뒤에 By를 추가하면 WHERE
    // 반환 값이 List


}
