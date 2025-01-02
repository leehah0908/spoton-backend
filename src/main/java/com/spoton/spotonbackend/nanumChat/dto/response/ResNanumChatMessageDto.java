package com.spoton.spotonbackend.nanumChat.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResNanumChatMessageDto {

    private Long nanumChatRoomId;
    private Long messagId;
    private Long nanumId;

    private String email;
    private String content;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
