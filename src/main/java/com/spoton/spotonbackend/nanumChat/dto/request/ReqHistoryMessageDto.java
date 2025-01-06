package com.spoton.spotonbackend.nanumChat.dto.request;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqHistoryMessageDto {
    private Long roomId;
}
