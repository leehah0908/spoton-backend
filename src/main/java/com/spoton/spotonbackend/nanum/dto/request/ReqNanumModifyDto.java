package com.spoton.spotonbackend.nanum.dto.request;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqNanumModifyDto {

    private Long nanumId;

    private String subject;
    private String content;
    private String sports;
    private Long quantity;
    private String giveMethod;
}
