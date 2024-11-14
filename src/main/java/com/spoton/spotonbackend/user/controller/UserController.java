package com.spoton.spotonbackend.user.controller;

import com.spoton.spotonbackend.common.auth.EmailProvider;
import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.common.dto.CommonErrorDto;
import com.spoton.spotonbackend.user.dto.request.ReqPasswordChangeDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailProvider emailProvider;

    // 레디스 관련 주입
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Integer> emailCertificationTemplate;


    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody ReqSignupDto dto){

        User user = userService.signup(dto);

        CommonResDto resDto = new CommonResDto(HttpStatus.CREATED, "회원가입 완료", user.getUserId());
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }


    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody ReqLoginDto dto,
                                   HttpServletResponse response){

        // 회원 정보 일치 여부 확인
        User user = userService.login(dto);

        // 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getAuth().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getAuth().toString());

        // 리프레시 토큰 redis에 저장
        redisTemplate.opsForValue().set(String.valueOf(user.getEmail()), refreshToken, 14400, TimeUnit.MINUTES);

        // 쿠키로 로그인 인증
        Cookie cookie = new Cookie("access_token", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(10 * 24 * 60 * 60); // 쿠키 유효기간 10일
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "로그인 성공", user.getUserId());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }


    // 로그인 후 회원 정보 응답
    @GetMapping("/login/check")
    public ResponseEntity<?> checkAuthStatus(@AuthenticationPrincipal TokenUserInfo userInfo) {

        String profile = userService.getProfile(userInfo.getEmail());

        Map<String,String> map = new HashMap<>();
        map.put("profile", profile);
        map.put("auth", String.valueOf(userInfo.getAuth()));

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "로그인 완료!", map);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }


    // 이메일 중복 확인
    @GetMapping("/check_email")
    public ResponseEntity<?> checkEmail(@RequestParam String email){
        System.out.println(email);

        boolean checkEmail = userService.checkEmail(email);
        System.out.println("체크 결과: " + checkEmail);
        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "사용가능한 이메일입니다.", checkEmail);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 이메일 인증 메일 보내기
    @PostMapping("/email_send")
    public ResponseEntity<?> sendEmail(@RequestParam String email){

        int number = emailProvider.sendCertificationMail(email);
        if (number == 500) {
            CommonErrorDto errorDto = new CommonErrorDto(HttpStatus.SERVICE_UNAVAILABLE, "인증 이메일 전송 실패");
            return new ResponseEntity<>(errorDto, HttpStatus.SERVICE_UNAVAILABLE);
        }

        // 레디스에 인증번호 3분동안 저장
        emailCertificationTemplate.opsForValue().set(email, number, 3, TimeUnit.MINUTES);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "인증 이메일 전송 성공", true);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 이메일 인증 확인
    @GetMapping("/email_certi")
    public ResponseEntity<?> emailCertification(@RequestParam String email,
                                                @RequestParam Integer reqNumber) {

        Integer tokenNumber = emailCertificationTemplate.opsForValue().get(email);

        if (tokenNumber == null) {
            return new ResponseEntity<>(
                    new CommonErrorDto(HttpStatus.REQUEST_TIMEOUT, "인증 번호를 재전송 한 후 다시시도 해주세요."),
                    HttpStatus.REQUEST_TIMEOUT);
        }

        if (!tokenNumber.equals(reqNumber)){
            return new ResponseEntity<>(
                    new CommonErrorDto(HttpStatus.UNAUTHORIZED, "인증번호가 일치하지 않습니다."),
                    HttpStatus.UNAUTHORIZED);
        }

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "인증이 완료되었습니다.", true);
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

        User user = userService.checkPassword(userInfo.getEmail(), password);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "비밀번호가 일치합니다.", user.getUserId());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 비밀번호 재발급
    @PostMapping("/pw_send")
    public ResponseEntity<?> sendPassword(@RequestParam String email){
        System.out.println(email);

        String newPw = emailProvider.sendTemporaryPassword(email);

        if (newPw.equals("fail")) {
            CommonErrorDto errorDto = new CommonErrorDto(HttpStatus.SERVICE_UNAVAILABLE, "임시 비밀번호 전송 실패");
            return new ResponseEntity<>(errorDto, HttpStatus.SERVICE_UNAVAILABLE);
        }

        User user = userService.setNewPassword(email, newPw);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "임시 비밀번호 전송 성공", user.getUserId());

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 비밀번호 변경
    @PostMapping("/change_pw")
    public ResponseEntity<?> changePassword(@RequestBody ReqPasswordChangeDto dto,
                                            @AuthenticationPrincipal TokenUserInfo userInfo) {

        User user = userService.checkPassword(userInfo.getEmail(), dto.getOldPassword());

        User newUser = userService.setNewPassword(user.getEmail(), dto.getNewPassword());

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "비밀번호 변경이 완료되었습니다.", newUser.getUserId());

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

    // 회원 정보 조회 (회원)
    @GetMapping("/my_info")
    public ResponseEntity<?> myInfo(@AuthenticationPrincipal TokenUserInfo userInfo) {

        UserResDto dto = userService.myInfo(userInfo);

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


    // 회원 탈퇴
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@AuthenticationPrincipal TokenUserInfo userInfo){

        userService.withdraw(userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "회원탈퇴가 완료되었습니다.", true);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
