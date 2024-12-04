package com.spoton.spotonbackend.board.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqReplyCreateDto {

    @NotEmpty(message = "내용은 필수값입니다.")
    private String content;

    @NotEmpty(message = "게시물 아이디는 필수값입니다.")
    private Long boardId;
}
