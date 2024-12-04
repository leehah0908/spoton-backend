package com.spoton.spotonbackend.board.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqReplyReportDto {

    @NotEmpty(message = "댓긓 아이디는 필수값입니다.")
    private Long replyId;

    @NotEmpty(message = "신고내역은 필수값입니다.")
    private String reportContent;
}
