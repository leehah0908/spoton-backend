package com.spoton.spotonbackend.board.dto.request;

import com.spoton.spotonbackend.board.entity.Board;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqBoardModifyDto {

    private Long boardId;
    private String subject;
    private String content;
    private int leagueId;
}
