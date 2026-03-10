package com.example.demo.reply;

import com.example.demo.board.model.Board;
import com.example.demo.reply.model.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Page<Reply> findByBoard(Board board, PageRequest of);
}
