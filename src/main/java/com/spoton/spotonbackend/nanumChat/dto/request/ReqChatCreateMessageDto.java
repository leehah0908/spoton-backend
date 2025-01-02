package com.spoton.spotonbackend.nanumChat.dto.request;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqChatCreateMessageDto {

    private Long roomId;
    private String message;
}
