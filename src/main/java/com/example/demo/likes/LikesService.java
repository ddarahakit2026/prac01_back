package com.example.demo.likes;

import com.example.demo.board.BoardRepository;
import com.example.demo.board.model.Board;
import com.example.demo.likes.model.Likes;
import com.example.demo.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikesService {
    private final LikesRepository likesRepository;
    private final BoardRepository boardRepository;


    // 트랜잭션 : 하나의 작업 단위를 묶어서 처리하는 것
    //      Atomicity   : 원자성, 트랜잭션은 전부 성공하거나 전부 실패
    //      Consistency : 일관성, 트랜잭션 실행 전,후 데이터는 항상 일관된 상태를 유지한다.
    //      Isolation   : 격리성, 동시에 실행 중인 트랜잭션은 서로 영향을 주지 않음
    //      Durability  : 지속성, 실행을 마친(커밋) 트랜잭션은 영구적으로 보존된다.


    // 동시성 제어가 안될 때(격리성이 지켜지지 않았을 때) 발생하는 문제
    //      1. Dirty Read : 아직 커밋되지 않은 데이터를 다른 트랜잭션이 읽는 문제
    //      2. Non-Repeatable Read : 같은 트랜잭션 내에서 같은 데이터를 두 번 읽었는데 값이 다른 문제
    //      3. Phantom Read : 같은 조건으로 두 번 조회했는데 결과 행의 수가 다른 문제

    // 격리 수준                Dirty Read      Non-Repeatable Read         Phantom Read    성능
    //      READ UNCOMMITTED    발생              발생                          발생         빠름
    //      READ COMMITTED      방지              발생                          발생
    //      REPEATABLE READ     방지              방지                          발생
    //      SERIALIZABLE        방지              방지                          방지         느림

    //  스프링의 Transactional와 DB의 트랜잭션은 비슷하지만 적용되는 레벨이 다름
    //      스프링의 Transactional 어플리케이션에서만 적용
    //      DB의 트랜잭션은 DB에서만 적용

    // 결국 실무에서는 락, 또는 쿼리를 직접 사용
    //  Lock 종류
    //      비관적 락 : 최악의 상황을 생각해서 무조건 동시성 문제가 발생할 것이라고 가정하고 설정하는 방법
    //                  데이터를 먼저 잠그고 작업 -> 충돌이 발생하지 않게 사전에 방지
    //      낙관적 락 : 최선의 상황을 생각해서 문제가 발생하지 않을수도 있지 않을까라고 가정하고 설정하는 방법
    //                  충돌이 생기면 예외를 발생 -> 해당 작업을 다시 실행

    // 락을 사용하지 않고 동시성 문제
    //      단순 카운트

    @Transactional
    public /* synchronized */ void like(AuthUserDetails user, Long boardIdx) {
        Board board = boardRepository.findById(boardIdx).orElseThrow(

        );
        Likes likes = Likes.builder()
                .user(user.toEntity())
                .board(board)
                .build();
        likes = likesRepository.save(likes);
//        board.increaseLikesCount();
//        boardRepository.save(board);

        int updated = boardRepository.increaseLikeCount(boardIdx);
        System.out.printf("updated : " + updated);
    }

}
