package com.spoton.spotonbackend.board.entity;

import com.spoton.spotonbackend.board.dto.response.ResBoardDto;
import com.spoton.spotonbackend.common.entity.BaseTimeEntity;
import com.spoton.spotonbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

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
    private int leagueId;

    @Builder.Default
    @Setter
    private Long viewCount = 0L;

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

    public ResBoardDto toResBoardDto() {
        return ResBoardDto.builder()
                .boardId(boardId)
                .subject(subject)
                .content(content)
                .leagueId(leagueId)
                .viewCount(viewCount)
                .likeCount(likeCount)
                .nickname(user.getNickname())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .profile(user.getProfile())
                .build();
    }

}
