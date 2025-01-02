package com.spoton.spotonbackend.gameChat.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResGameChatDto {

    private String gameId;
    private String email;
    private String nickname;
    private String content;
    private LocalDateTime createTime;
}
