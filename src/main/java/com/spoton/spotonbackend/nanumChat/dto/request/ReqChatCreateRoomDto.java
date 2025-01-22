package com.spoton.spotonbackend.nanumChat.dto.request;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqChatCreateRoomDto {

    private String providerEmail;
    private String receiverEmail;
    private Long nanumId;
}
