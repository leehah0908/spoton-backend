package com.spoton.spotonbackend.board.dto.response;

import com.spoton.spotonbackend.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResBoardDto {

    private Long boardId;
    private String subject;
    private String content;
    private int leagueId;
    private Long viewCount;
    private Long likeCount;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String nickname;
    private String profile;

}
