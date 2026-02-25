package com.example.demo.reply;

import com.example.demo.reply.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findAllByBoard_IdxOrderByIdxAsc(Long boardIdx);
    long countByBoard_Idx(Long boardIdx);
}