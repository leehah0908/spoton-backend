package com.spoton.spotonbackend.board.dto.request;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqReplyModifyDto {

    private String content;
    private Long replyId;

}
