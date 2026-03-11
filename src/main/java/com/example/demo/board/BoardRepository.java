package com.example.demo.board;

import com.example.demo.board.model.Board;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

//                                                     엔티티 클래스, 엔티티 클래스의 @Id 변수의 타입
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Board b WHERE b.idx = :boardIdx") // JPQL
    Optional<Board> findByIdWithLock(Long boardIdx);
}
