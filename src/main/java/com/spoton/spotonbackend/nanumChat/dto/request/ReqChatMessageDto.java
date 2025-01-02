package com.spoton.spotonbackend.nanumChat.dto.request;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqChatMessageDto {
    private Long roomId;
}
