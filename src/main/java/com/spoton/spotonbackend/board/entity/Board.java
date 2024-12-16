package com.spoton.spotonbackend.board.entity;

import com.spoton.spotonbackend.board.dto.response.ResBoardDto;
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
@Table(name = "board")
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    @Setter
    private String subject;

    @Column(nullable = false)
    @Setter
    private String content;

    @Setter
    private String sports;

    @Builder.Default
    @Setter
    private Long viewCount = 0L;

    @Builder.Default
    @Setter
    private Long likeCount = 0L;

    @Builder.Default
    @Setter
    private Long reportCount = 0L;

    @Builder.Default
    @Setter
    private Long replyCount = 0L;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardLike> boardLikes = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardReport> boardReports = new ArrayList<>();

    public ResBoardDto toResBoardDto() {
        return ResBoardDto.builder()
                .boardId(boardId)
                .email(user.getEmail())
                .subject(subject)
                .content(content)
                .sports(sports)
                .viewCount(viewCount)
                .likeCount(likeCount)
                .replyCount(replyCount)
                .nickname(user.getNickname())
                .createTime(getCreateTime())
                .updateTime(getUpdateTime())
                .profile(user.getProfile())
                .build();
    }

}
