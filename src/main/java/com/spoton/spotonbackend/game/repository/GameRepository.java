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

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GameRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final Map<String, Map<String, String>> LEAGUE_TEAM_MAP = Map.of(
            "baseball", Map.of(
                    "KBO", "kbo_game_data",
                    "MLB", "mlb_game_data"
            ),
            "soccer", Map.of(
                    "K-League", "kleague1_game_data",
                    "Premier League", "epl_game_data"
            ),
            "basketball", Map.of(
                    "KBL", "kbl_game_data",
                    "NBA", "nba_game_data"
            ),
            "volleyball", Map.of(
                    "V - League 남자부", "kovo_game_data",
                    "V - League 여자부", "wkovo_game_data"
            ),
            "esports", Map.of(
                    "LCK", "lck_game_data"
            )
    );

    public List<Map<String, Object>> findGamesByDate(ReqGameListDto dto, String yearMonth) {

        if (dto.getSports().equals("MYTEAM")) {
            String leagueTableName = switch (dto.getLeague()) {
                case "kboTeam" -> "kbo_game_data";
                case "mlbTeam" -> "mlb_game_data";
                case "kleagueTeam" -> "kleague1_game_data";
                case "eplTeam" -> "epl_game_data";
                case "kblTeam" -> "kbl_game_data";
                case "nbaTeam" -> "nba_game_data";
                case "kovoTeam" -> "kovo_game_data";
                case "wkovwTeam" -> "wkovo_game_data";
                case "lckTeam" -> "lck_game_data";
                default -> throw new IllegalArgumentException("알 수 없는 리그: " + dto.getLeague());
            };

            String sql = String.format("SELECT * FROM %s WHERE DATE_FORMAT(gameDateTime, '%%Y-%%m') = ? and (homeTeamName = '%s' or awayTeamName = '%s');", leagueTableName, dto.getTeam(), dto.getTeam());
            return jdbcTemplate.queryForList(sql, yearMonth);
        }

            Map<String, String> leagues = LEAGUE_TEAM_MAP.get(dto.getSports());

        if (dto.getLeague().equals("ALL")) {
            List<Map<String, Object>> allResults = new ArrayList<>();

            for (String tableName : leagues.values()) {
                String sql = String.format("SELECT * FROM %s WHERE DATE_FORMAT(gameDateTime, '%%Y-%%m') = ?", tableName);
                allResults.addAll(jdbcTemplate.queryForList(sql, yearMonth));
            }
            return allResults;
        }

        String leagueTableName = leagues.get(dto.getLeague());

        String sql = String.format("SELECT * FROM %s WHERE DATE_FORMAT(gameDateTime, '%%Y-%%m') = ?", leagueTableName);
        return jdbcTemplate.queryForList(sql, yearMonth);
    }
}