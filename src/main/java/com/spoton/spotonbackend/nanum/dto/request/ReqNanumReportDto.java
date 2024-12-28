package com.spoton.spotonbackend.nanum.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqNanumReportDto {

    @NotEmpty(message = "나눔글 아이디는 필수값입니다.")
    private Long nanumId;

    @NotEmpty(message = "신고내역은 필수값입니다.")
    private String reportContent;
}
