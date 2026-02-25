package com.example.demo.reply.model;

import lombok.Builder;
import lombok.Getter;

public class ReplyDto {

    @Getter
    public static class RegReq {
        private String contents;

        public Reply toEntity() {
            return Reply.builder()
                    .contents(this.contents)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class RegRes {
        private Long idx;
        private Long boardIdx;
        private String contents;
        private String writer;

        public static RegRes from(Reply entity) {
            return RegRes.builder()
                    .idx(entity.getIdx())
                    .boardIdx(entity.getBoard() == null ? null : entity.getBoard().getIdx())
                    .contents(entity.getContents())
                    .writer(entity.getUser() == null ? null : entity.getUser().getName())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class ListRes {
        private Long idx;
        private String contents;
        private String writer;

        public static ListRes from(Reply entity) {
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .contents(entity.getContents())
                    .writer(entity.getUser() == null ? null : entity.getUser().getName())
                    .build();
        }
    }
}