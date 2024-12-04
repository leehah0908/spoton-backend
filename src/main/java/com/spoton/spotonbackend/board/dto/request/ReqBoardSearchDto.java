package com.spoton.spotonbackend.board.dto.request;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqBoardSearchDto {

    private String searchType;
    private String searchKeyword;

}
