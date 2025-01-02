package com.spoton.spotonbackend.gameChat.dto.request;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqGameChatDto {

    private String senderEmail;
    private String content;
}
