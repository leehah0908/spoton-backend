package com.spoton.spotonbackend.user.dto.response;

import com.spoton.spotonbackend.board.entity.Board;
import com.spoton.spotonbackend.nanum.entity.Nanum;
import com.spoton.spotonbackend.user.entity.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResDashboardDto {
    private User user;

    private List<Nanum> writeNanums;
    private List<Nanum> likeNanums;

    private List<Board> writeBoards;
    private List<Board> likeBoards;

}
