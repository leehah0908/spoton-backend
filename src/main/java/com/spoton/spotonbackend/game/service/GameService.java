package com.spoton.spotonbackend.game.service;

import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.game.dto.request.ReqGameListDto;
import com.spoton.spotonbackend.game.dto.response.ResGameDto;
import com.spoton.spotonbackend.game.repository.GameRepository;
import com.spoton.spotonbackend.user.entity.MyTeam;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public List<ResGameDto> list(ReqGameListDto dto) {

        int year = dto.getYear();
        int month = dto.getMonth();
        String yearMonth = String.format("%d-%02d", year, month);

        List<Map<String, Object>> games = gameRepository.findGamesByDate(dto, yearMonth);

        return getResGameDto(games);
    }

    public MyTeam myTeamLoad(TokenUserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("유저 정보를 찾을 수 없습니다.")
        );
        return user.getMyTeam();
    }

    public Map<String, Object> detail(String league, String gameId) {
        return gameRepository.findGameId(league, gameId);
    }

    public List<ResGameDto> today() {

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<Map<String, Object>> todayGames = gameRepository.todayGame(today);

        return getResGameDto(todayGames);
    }

    private List<ResGameDto> getResGameDto(List<Map<String, Object>> gams) {
        return gams.stream().map(game -> ResGameDto.builder()
                .gameId(game.get("gameId").toString())
                .leagueName(game.get("categoryId").toString())
                .gameStartTime((LocalDateTime) game.get("gameDateTime"))
                .stadium(game.get("stadium").toString())
                .series(game.get("roundCode").toString())
                .statusCode(game.get("statusCode").toString())
                .statusInfo(game.get("statusInfo").toString())
                .cancel((Boolean) game.get("cancel"))
                .homeTeamName(game.get("homeTeamName").toString())
                .awayTeamName(game.get("awayTeamName").toString())
                .homeTeamScore(game.get("homeTeamScore").toString())
                .awayTeamScore(game.get("awayTeamScore").toString())
                .homeTeamLogo(game.get("homeTeamEmblemUrl").toString())
                .awayTeamLogo(game.get("awayTeamEmblemUrl").toString())
                .build()).toList();
    }
}
