package com.spoton.spotonbackend.board.dto.response;

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
    private String email;
    private String subject;
    private String content;
    private String sports;
    private Long viewCount;
    private Long likeCount;
    private Long replyCount;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String nickname;
    private String profile;
}
