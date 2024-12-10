package com.spoton.spotonbackend.game.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResGameDto {

    private String gameId;
    private String leagueName;
    private LocalDateTime gameStartTime;
    private String stadium;
    private String series;
    private String statusCode;
    private String statusInfo;
    private boolean cancel;

    private String homeTeamName;
    private String awayTeamName;
    private String homeTeamScore;
    private String awayTeamScore;
    private String homeTeamLogo;
    private String awayTeamLogo;
}
