package com.spoton.spotonbackend.board.entity;

import com.spoton.spotonbackend.board.dto.response.ResBoardDto;
import com.spoton.spotonbackend.board.dto.response.ResReplyDto;
import com.spoton.spotonbackend.common.entity.BaseTimeEntity;
import com.spoton.spotonbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "reply")
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    @Column(nullable = false)
    @Setter
    private String content;

    @Builder.Default
    private Long likeCount = 0L;

    @Builder.Default
    @Setter
    private Long reportCount = 0L;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    @Setter
    private Board board;

    public ResReplyDto toResReplyDto() {
        return ResReplyDto.builder()
                .replyId(replyId)
                .content(content)
                .likeCount(likeCount)
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .nickname(user.getNickname())
                .profile(user.getProfile())
                .build();
    }
}
