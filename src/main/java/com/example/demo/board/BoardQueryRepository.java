package com.example.demo.board;

import com.example.demo.board.model.Board;
import com.example.demo.board.model.QBoard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BoardQueryRepository {
    private final JPAQueryFactory queryFactory;
    private QBoard board;
    public BoardQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.board = QBoard.board;
    }

    // SELECT * FROM board;
    public List<Board> search() {
        List<Board> result = queryFactory
                .selectFrom(board).fetch();

        return result;
    }
}
