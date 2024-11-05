package com.spoton.spotonbackend.user.controller;

import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.common.dto.CommonErrorDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.spoton.spotonbackend.common.auth.JwtTokenProvider;
import com.spoton.spotonbackend.common.dto.CommonResDto;
import com.spoton.spotonbackend.user.dto.request.ReqLoginDto;
import com.spoton.spotonbackend.user.dto.request.ReqSignupDto;
import com.spoton.spotonbackend.user.dto.response.UserResDto;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.service.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    @Qualifier("refresh-template")
    private final RedisTemplate<String, Object> redisTemplate;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody ReqSignupDto dto){

        User user = userService.signup(dto);

        CommonResDto resDto = new CommonResDto(HttpStatus.CREATED, "회원가입 완료", user.getUserId());
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody ReqLoginDto dto){

        User user = userService.login(dto);

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getAuth().toString());

        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getAuth().toString());
        redisTemplate.opsForValue().set(String.valueOf(user.getEmail()), refreshToken, 14400, TimeUnit.HOURS);

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("token", accessToken);
        resMap.put("user_id", user.getUserId());

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "로그인 성공", resMap);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 로그아웃 -> 프론트에서 결정??

    // 이메일 중복 확인
    @GetMapping("/check_email")
    public ResponseEntity<?> checkEmail(@RequestParam String email){

        boolean checkEmail = userService.checkEmail(email);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "사용가능한 이메일입니다.", checkEmail);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 닉네임 중복 확인
    @GetMapping("/check_nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname){

        boolean checkNickname = userService.checkNickname(nickname);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "사용가능한 닉네임입니다.", checkNickname);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 비밀번호 확인
    @PostMapping("/check_pw")
    public ResponseEntity<?> checkPw(@RequestBody String password,
                                     @AuthenticationPrincipal TokenUserInfo userInfo){

        boolean checkPassword = userService.checkPassword(userInfo, password);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "비밀번호가 일치합니다.", checkPassword);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 회원 정보 조회 (관리자)
    @GetMapping("/user_info")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> userInfo(Pageable pageable) {

        List<UserResDto> ResDto = userService.userInfo(pageable);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "회원정보 조회 성공", ResDto);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 회원 정보 조화 (회원)
    @GetMapping("/my_info")
    public ResponseEntity<?> myInfo() {

        UserResDto dto = userService.myInfo();

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "myinfo 조회 성공", dto);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 정보 수정 (닉네임, 마이팀)
    @PatchMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody ReqSignupDto dto,
                                    @AuthenticationPrincipal TokenUserInfo userInfo) {

        User user = userService.modify(userInfo, dto);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "회원 정보 수정 성공", user.getUserId());

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }


    // 비밀번호 변경 -> 확인 요청이랑 같이할지 분리할지 정하기

    // 비밀번호 찾기 -> 재발급(UUID로)

    // 프로필 사진 설정
    @PostMapping("/set_profile")
    public ResponseEntity<?> setProfile(@RequestBody MultipartFile imgFile,
                                        @AuthenticationPrincipal TokenUserInfo userInfo) {

        User user = userService.profileSet(userInfo, imgFile);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "프로필 사진 저장 완료", user.getUserId());

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 토큰 재요청시 refresh token 확인 후 발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody String userId) {
        User user = userService.findById(Long.parseLong(userId));

        Object refreshToken = redisTemplate.opsForValue().get(user.getEmail());

        if (refreshToken == null) {
            return new ResponseEntity<>(
                    new CommonErrorDto(HttpStatus.UNAUTHORIZED, "다시 로그인이 필요합니다."),
                    HttpStatus.UNAUTHORIZED);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getAuth().toString());

        Map<String, Object> map = new HashMap<>();
        map.put("token", newAccessToken);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "토큰 재발급 성공", map);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
