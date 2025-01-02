package com.spoton.spotonbackend.nanumChat.dto.response;

import com.spoton.spotonbackend.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResNanumChatRoomDto {

    private Long nanumChatRoomId;
    private Long nanumId;

    private User provider;
    private User receiver;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
