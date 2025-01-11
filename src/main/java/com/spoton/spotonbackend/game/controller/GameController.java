package com.spoton.spotonbackend.game.controller;

import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.common.configs.JsonConfig;
import com.spoton.spotonbackend.common.dto.CommonResDto;
import com.spoton.spotonbackend.game.dto.request.ReqGameListDto;
import com.spoton.spotonbackend.game.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;
    private final RedisTemplate<String, String> gameCacheTemplate;

    @GetMapping("/list")
    public ResponseEntity<?> gameList(ReqGameListDto dto){
        List<Map<String, Object>> games = gameService.list(dto);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "경기 조회 완료", games);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> gameDetail(@RequestParam String gameId) {
        String cacheData = gameCacheTemplate.opsForValue().get(gameId);

        if (cacheData != null) {
            Map<String, Object> gameDetail = JsonConfig.stringToMap(cacheData);

            CommonResDto resDto = new CommonResDto(HttpStatus.OK, "경기 상세 정보 완료", gameDetail);
            return new ResponseEntity<>(resDto, HttpStatus.OK);
        }

        Map<String, Object> gameDetail = gameService.detail(gameId);

        // game Data redis에 저장
        String gameDetailStr = JsonConfig.mapToString(gameDetail);

        gameCacheTemplate.opsForValue().set(gameId, gameDetailStr, 7, TimeUnit.DAYS);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "경기 상세 정보 완료", gameDetail);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @GetMapping("/myteam")
    public ResponseEntity<?> myTeamLoad(@AuthenticationPrincipal TokenUserInfo userInfo){
        Map<String, String> myTeam = gameService.myTeamLoad(userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "마이팀 정보 조회 완료", myTeam);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @GetMapping("/today")
    public ResponseEntity<?> todayGame(){
        log.info("오늘 경기 들어왔따용~");
        List<Map<String, Object>> todayGameList = gameService.today();

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "오늘 경기 조회 완료", todayGameList);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
