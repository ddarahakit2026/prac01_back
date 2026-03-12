package com.example.demo.board.model;

import com.example.demo.common.model.BaseEntity;
import com.example.demo.likes.model.Likes;
import com.example.demo.reply.model.Reply;
import com.example.demo.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Column(nullable=false, length = 100)
    private String title;
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @BatchSize(size = 5)
    @OneToMany(mappedBy = "board")
    List<Reply> replyList;

    private int likesCount;

    // 기존에 수정할 때 실행되는 쿼리
    // UPDATE board SET likes_count = 2 WHERE idx=1;
    //
    // 낙관적 락으로 설정한 version과 함께 실행되는 쿼리
    // UPDATE board SET likes_count = 2, version = version + 1 WHERE idx=1 AND version =1;
    @Version
    @ColumnDefault("0")
    private Long version;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    List<Likes> likesList;

    public void update(BoardDto.RegReq dto) {
        this.title = dto.getTitle();
        this.contents = dto.getContents();
    }

    public void increaseLikesCount() {
        this.likesCount = this.likesCount+1;
    }

}
