package com.spoton.spotonbackend.board.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResReplyDto {

    private Long replyId;
    private String email;
    private String content;
    private Long likeCount;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String nickname;
    private String profile;

}
