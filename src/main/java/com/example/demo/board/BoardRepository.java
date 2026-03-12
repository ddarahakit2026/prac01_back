package com.example.demo.board;

import com.example.demo.board.model.Board;
import com.example.demo.board.model.BoardDto;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/*
    N+1 문제
        1번 쿼리로 N개의 결과를 가져올 때
        가져온 데이터와 관계를 맺은 데이터를 추가로 가져오기 위해서 N번의 쿼리를 더 실행하는 문제

        ex)                                             결과)
            SELECT * FROM board;                            1, 2, 3, 4, 5
            SELECT * FROM reply WHERE board_idx = 1;        1, 2, 3
            SELECT * FROM reply WHERE board_idx = 2;        4, 5, 6
            SELECT * FROM reply WHERE board_idx = 3;        7, 8
            SELECT * FROM reply WHERE board_idx = 4;        9,
            SELECT * FROM reply WHERE board_idx = 5;        10, 11, 12, 13

    원인
        board 객체를 가져왔을 때 JPA에서는 관계를 맺은 객체(reply) 객체는 실제 reply 객체가 아니라
        Proxy(가짜) 객체를 저장해두고 해당 객체를 실제 사용할 때 추가로 데이터를 가져온다.

    해결 방법
        Fetch Join    : 직접 JPQL로 JOIN 쿼리를 만들어주는 것
        @EntityGraph  : JPA가 JOIN 쿼리를 생성하도록 설정하는 것
        @BatchSize    : 한 번에 가져올 데이터의 수를 지정
        Dto로 처리     : 엔티티로 데이터를 가져오는 것 대신에 바로 Dto에 데이터를 담는 방식


    JPA 서버 개발하는 순서
        1. 엔티티로 기본 기능 개발
            모든 관계는 전부 LAZY로처리

        2. ~~~ToOne
            Fetch Join으로 최적화

        3. ~~~ToMany
          1) 페이징 처리 필요 없고 관계가 1개만 맺었을 때
            Fetch Join으로 최적화

          2-1) 페이징 처리가 필요, 페이지 번호 필요
            @BatchSize 로 최적화, 레포지토리에서 반환 Page

          2-2) 페이징 처리가 필요, 페이지 번호 필요 없음
            @BatchSize 로 최적화, 레포지토리에서 반환 Slice

         3) 읽기 성능이 중요한 경우
            Dto로 처리 방식으로 최적화

*/


/*

	// N+1 개선 전
	SELECT * FROM board;							1번 		100 개

	SELECT * FROM reply WHERE board_idx=?			100 번

	// @BatchSize로 N+1 개선 후
	SELECT * FROM board;							1번		100 개

	SELECT * FROM reply WHERE board_idx IN (?, ?, ?, ?, ?)	1번
*/

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 레포지토리 통해서 Dto에 바로 데이터 담기
    //      성능 제일 좋음
    @Query("""
            SELECT new com.example.demo.board.model.BoardDto$ListRes(b.idx, b.title, b.user.name, COUNT(r), b.likesCount) 
            FROM Board b
            LEFT JOIN b.replyList r
           """)
    List<BoardDto.ListRes> findAllDto();

    @Query("""
            SELECT new com.example.demo.board.model.BoardDto$ListRes(
                       b.idx, b.title, b.user.name, (SELECT COUNT(r) FROM Reply r WHERE r.board.idx = b.idx), b.likesCount) 
            FROM Board b
           """)
    Page<BoardDto.ListRes> findAllDtoWithPage(Pageable pageable);


    // FETCH JOIN으로 reply를 같이 조회
    //  페이징 처리 X,둘 이상의 FETCH JOIN 불가(MultipleBagFetchException)
    @Query("SELECT b from Board b LEFT JOIN FETCH b.replyList")
    List<Board> findAllWithReply();

    // 페이징 처리 X,둘 이상의 FETCH JOIN 불가(MultipleBagFetchException)
//    @EntityGraph(attributePaths = "replyList")
//    List<Board> findAll();


    // 비관적 락
    // SELECT * FROM board WHERE idx=1 FOR UPDATE
    // UPDATE 문을 실행하기 위해서 SELECT로 조회하는거니까 내가 UPDATE다 할 때까지 다른 애들이 조회 못하게 잠금
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Board b WHERE b.idx = :boardIdx") // JPQL
    Optional<Board> findByIdWithLock(Long boardIdx);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Board> findByIdx(Long boardIdx);


}
