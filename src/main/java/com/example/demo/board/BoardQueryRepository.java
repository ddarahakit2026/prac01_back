package com.example.demo.board;

import com.example.demo.board.model.Board;
import com.example.demo.board.model.BoardDto;
import com.example.demo.board.model.QBoard;
import com.querydsl.core.types.dsl.BooleanExpression;
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

    public List<Board> search(BoardDto.SearchReq dto) {
        List<Board> result = queryFactory
                .selectFrom(board)
                .where(
                        idxGoe(dto.getIdx()),
                        idxLoe(dto.getIdx()),
                        titleContains(dto.getTitle()),
                        contentsContains(dto.getContents()),
                        writerContains(dto.getWriter())
                )
                .fetch();

        return result;
    }

    private BooleanExpression writerContains(String writer) {
        return writer != null && !writer.isBlank() ?
                board.user.name.containsIgnoreCase(writer) : null;
    }

    private BooleanExpression contentsContains(String contents) {
        return contents != null && !contents.isBlank() ?
                board.contents.containsIgnoreCase(contents) : null;
    }

    private BooleanExpression titleContains(String title) {
        return title != null && !title.isBlank() ?
                board.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression idxGoe(Long idx) {
        return idx != null ? board.idx.goe(idx) : null;
    }

    private BooleanExpression idxLoe(Long idx) {
        return idx != null ? board.idx.loe(idx) : null;
    }


}
