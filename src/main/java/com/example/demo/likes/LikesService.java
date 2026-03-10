package com.example.demo.likes;

import com.example.demo.board.BoardRepository;
import com.example.demo.board.model.Board;
import com.example.demo.likes.model.Likes;
import com.example.demo.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikesService {
    private final LikesRepository likesRepository;
    private final BoardRepository boardRepository;

    public void like(AuthUserDetails user, Long boardIdx) {
        Board board = boardRepository.findById(boardIdx).orElseThrow();
        Likes likes = Likes.builder()
                .user(user.toEntity())
                .board(board)
                .build();
        likes = likesRepository.save(likes);
        board.increaseLikesCount();
        boardRepository.save(board);
    }
}
