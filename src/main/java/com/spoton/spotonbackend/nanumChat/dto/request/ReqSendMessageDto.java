package com.spoton.spotonbackend.nanumChat.dto.request;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqSendMessageDto {

    private Long roomId;
    private String email;
    private String content;
}
