package com.spoton.spotonbackend.game.controller;

import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.common.dto.CommonResDto;
import com.spoton.spotonbackend.game.dto.request.ReqGameListDto;
import com.spoton.spotonbackend.game.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;

    @GetMapping("/list")
    public ResponseEntity<?> gameList(ReqGameListDto dto){
        List<Map<String, Object>> games = gameService.list(dto);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "경기 조회 완료", games);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> gameDetail(@RequestParam String gameId){
        Map<String, Object> gameDetail = gameService.detail(gameId);

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
        List<Map<String, Object>> todayGameList = gameService.today();

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "오늘 경기 조회 완료", todayGameList);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
