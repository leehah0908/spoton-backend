package com.spoton.spotonbackend.game.service;

import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.game.dto.request.ReqGameListDto;
import com.spoton.spotonbackend.game.repository.GameRepository;
import com.spoton.spotonbackend.user.entity.MyTeam;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public List<Map<String, Object>> list(ReqGameListDto dto) {
        int year = dto.getYear();
        int month = dto.getMonth();

        YearMonth yearMonth = YearMonth.of(year, month);
        String startDate = yearMonth.atDay(1).toString();
        String endDate = yearMonth.atEndOfMonth().toString();

        return gameRepository.findGamesByDate(dto, startDate, endDate);
    }

    public Map<String, String> myTeamLoad(TokenUserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("유저 정보를 찾을 수 없습니다.")
        );

        MyTeam myTeam = user.getMyTeam();

        // 리그 순서 보장을 위해 LinkedHashMap 사용
        Map<String, String> myTeamMap = new LinkedHashMap<>();
        if (myTeam.getKbo() != null) myTeamMap.put("kbo", myTeam.getKbo());
        if (myTeam.getMlb() != null) myTeamMap.put("mlb", myTeam.getMlb());
        if (myTeam.getKleague() != null) myTeamMap.put("kleague", myTeam.getKleague());
        if (myTeam.getEpl() != null) myTeamMap.put("epl", myTeam.getEpl());
        if (myTeam.getKbl() != null) myTeamMap.put("kbl", myTeam.getKbl());
        if (myTeam.getNba() != null) myTeamMap.put("nba", myTeam.getNba());
        if (myTeam.getKovo() != null) myTeamMap.put("kovo", myTeam.getKovo());
        if (myTeam.getWkovo() != null) myTeamMap.put("wkovo", myTeam.getWkovo());
        if (myTeam.getLck() != null) myTeamMap.put("lck", myTeam.getLck());

        return myTeamMap;
    }

    public Map<String, Object> detail(String gameId) {
        return gameRepository.findGameId(gameId);
    }

    public List<Map<String, Object>> today() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        return gameRepository.todayGame(today);
    }
}
