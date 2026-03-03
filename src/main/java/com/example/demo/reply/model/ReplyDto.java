package com.example.demo.reply.model;

import com.example.demo.board.model.Board;
import com.example.demo.relation.model.A;
import com.example.demo.relation.model.ADto;
import com.example.demo.relation.model.BDto;
import com.example.demo.user.model.AuthUserDetails;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ReplyDto {
    @Getter
    public static class ReplyReq {
        private String contents;

        public Reply toEntity(AuthUserDetails user, Long boardIdx) {
            return Reply.builder()
                    .contents(contents)
                    .board(Board.builder().idx(boardIdx).build())
                    .user(user.toEntity())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class ReplyRes {
        private Long idx;
        private String contents;
        private String writer;

        public static ReplyDto.ReplyRes from(Reply entity) {
            return ReplyRes.builder()
                    .idx(entity.getIdx())
                    .contents(entity.getContents())
                    .writer(entity.getUser().getName())
                    .build();
        }
    }
}
