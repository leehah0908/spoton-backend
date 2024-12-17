package com.spoton.spotonbackend.board.entity;

import com.spoton.spotonbackend.board.dto.response.ResReplyDto;
import com.spoton.spotonbackend.common.entity.BaseTimeEntity;
import com.spoton.spotonbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    @Setter
    private Long likeCount = 0L;

    @Builder.Default
    @Setter
    private Long reportCount = 0L;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplyLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplyReport> replyReports = new ArrayList<>();

    public ResReplyDto toResReplyDto() {
        return ResReplyDto.builder()
                .replyId(replyId)
                .email(user.getEmail())
                .content(content)
                .likeCount(likeCount)
                .createTime(getCreateTime())
                .updateTime(getUpdateTime())
                .nickname(user.getNickname())
                .profile(user.getProfile())
                .build();
    }
}
