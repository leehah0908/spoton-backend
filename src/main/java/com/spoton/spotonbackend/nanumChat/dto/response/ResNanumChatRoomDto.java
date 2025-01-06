package com.spoton.spotonbackend.nanumChat.dto.response;

import com.spoton.spotonbackend.nanum.entity.Nanum;
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
    private Nanum nanum;
    private String nanumImage;

    private User provider;
    private User receiver;
    private String lastMessage;
    private LocalDateTime lastMessageTime;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
