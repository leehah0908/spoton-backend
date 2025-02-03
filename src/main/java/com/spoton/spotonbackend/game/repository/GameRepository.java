package com.spoton.spotonbackend.game.repository;

import com.spoton.spotonbackend.game.dto.request.ReqGameListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class GameRepository {

    // 엔티티를 따로 JPA로 생성하지 않았기 때문에 Jdbc 사용해서 데이터 조회
    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findGamesByDate(ReqGameListDto dto, String startDate, String endDate) {
        if (dto.getSports().equals("MYTEAM")) {
            String sql = "SELECT * FROM game_data WHERE league = ? AND gameDate BETWEEN ? AND ? AND (homeTeam = ? OR awayTeam = ?)";
            return jdbcTemplate.queryForList(sql, dto.getLeague(), startDate, endDate, dto.getTeam(), dto.getTeam());
        }

        Map<String, String> leagues = LEAGUE_TEAM_MAP.get(dto.getSports());

        if (dto.getLeague().equals("ALL")) {
            List<Map<String, Object>> allResults = new ArrayList<>();
            for (String league : leagues.values()) {
                String sql = "SELECT * FROM game_data WHERE league = ? AND gameDate BETWEEN ? AND ?";
                allResults.addAll(jdbcTemplate.queryForList(sql, league, startDate, endDate));
            }
            return allResults;
        }

        String league = LEAGUE_TEAM_MAP.get(dto.getSports()).get(dto.getLeague());

        String sql = "SELECT * FROM game_data WHERE league = ? AND gameDate BETWEEN ? AND ?";
        return jdbcTemplate.queryForList(sql, league, startDate, endDate);
    }

    public Map<String, Object> findGameId(String gameId) {
        String sql = "SELECT * FROM game_data WHERE gameId = ?";
        return jdbcTemplate.queryForMap(sql, gameId);
    }

    public List<Map<String, Object>> todayGame(LocalDate today) {
        String sql = "SELECT * FROM game_data WHERE gameDate >= ? AND gameDate < ?";
        return jdbcTemplate.queryForList(sql, today.toString(), today.plusDays(1).toString());
    }

    private static final Map<String, Map<String, String>> LEAGUE_TEAM_MAP = Map.of(
            "baseball", Map.of(
                    "KBO", "kbo",
                    "MLB", "mlb"
            ),
            "soccer", Map.of(
                    "K-League", "kleague",
                    "Premier League", "epl"
            ),
            "basketball", Map.of(
                    "KBL", "kbl",
                    "NBA", "nba"
            ),
            "volleyball", Map.of(
                    "V - League 남자부", "kovo",
                    "V - League 여자부", "wkovo"
            ),
            "esports", Map.of(
                    "LCK", "lck"
            )
    );
}