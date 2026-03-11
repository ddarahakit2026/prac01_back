package com.example.demo.board;

import com.example.demo.board.model.Board;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

//                                                     엔티티 클래스, 엔티티 클래스의 @Id 변수의 타입
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 비관적 락
    // SELECT * FROM board WHERE idx=1 FOR UPDATE
    // UPDATE 문을 실행하기 위해서 SELECT로 조회하는거니까 내가 UPDATE다 할 때까지 다른 애들이 조회 못하게 잠금
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Board b WHERE b.idx = :boardIdx") // JPQL
    Optional<Board> findByIdWithLock(Long boardIdx);

//    @Lock(LockModeType.OPTIMISTIC)
//    Optional<Board> findByIdx(Long boardIdx);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Board b SET b.likesCount = b.likesCount + 1 WHERE b.idx=:boardIdx")
    int increaseLikeCount(Long boardIdx);
}
