package com.spoton.spotonbackend.board.dto.request;

import com.spoton.spotonbackend.board.entity.Board;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqBoardCreateDto {

    @NotEmpty(message = "제목은 필수값입니다.")
    private String subject;

    @NotEmpty(message = "내용은 필수값입니다.")
    private String content;

    private int leagueId;

    public Board toBoard() {
        return Board.builder()
                .subject(subject)
                .content(content)
                .leagueId(leagueId)
                .build();
    }
}
