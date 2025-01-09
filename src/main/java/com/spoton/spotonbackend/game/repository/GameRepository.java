package com.spoton.spotonbackend.game.repository;

import com.spoton.spotonbackend.game.dto.request.ReqGameListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class GameRepository {

    // 엔티티를 따로 JPA로 생성하지 않았기 때문에 Jdbc 사용해서 데이터 조회
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GameRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findGamesByDate(ReqGameListDto dto, String yearMonth) {
        if (dto.getSports().equals("MYTEAM")) {

            String sql = String.format("SELECT * FROM game_data WHERE (league = '%s') and DATE_FORMAT(gameDate, '%%Y-%%m') = ? and (homeTeam = '%s' or awayTeam = '%s');", dto.getLeague(), dto.getTeam(), dto.getTeam());
            return jdbcTemplate.queryForList(sql, yearMonth);
        }

        Map<String, String> leagues = LEAGUE_TEAM_MAP.get(dto.getSports());

        if (dto.getLeague().equals("ALL")) {
            List<Map<String, Object>> allResults = new ArrayList<>();

            for (String league : leagues.values()) {
                String sql = String.format("SELECT * FROM game_data WHERE (league = '%s') and DATE_FORMAT(gameDate, '%%Y-%%m') = ?", league);
                allResults.addAll(jdbcTemplate.queryForList(sql, yearMonth));
            }
            return allResults;
        }

        String league = LEAGUE_TEAM_MAP.get(dto.getSports()).get(dto.getLeague());

        String sql = String.format("SELECT * FROM game_data WHERE (league = '%s') and DATE_FORMAT(gameDate, '%%Y-%%m') = ?", league);
        return jdbcTemplate.queryForList(sql, yearMonth);
    }

    public Map<String, Object> findGameId(String gameId) {

        String sql = "SELECT * FROM game_data WHERE gameId = ?";
        return jdbcTemplate.queryForMap(sql, gameId);
    }

    public List<Map<String, Object>> todayGame(String today) {

        String sql = "SELECT * FROM game_data WHERE DATE(gameDate) = ?";
        jdbcTemplate.queryForList(sql, today);

        return jdbcTemplate.queryForList(sql, today);
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