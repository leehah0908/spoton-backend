package com.spoton.spotonbackend.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.spoton.spotonbackend.common.auth.JwtTokenProvider;
import com.spoton.spotonbackend.common.dto.CommonResDto;
import com.spoton.spotonbackend.user.dto.request.ReqLoginDto;
import com.spoton.spotonbackend.user.dto.request.ReqSignupDto;
import com.spoton.spotonbackend.user.dto.response.UserResDto;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @ModelAttribute ReqSignupDto dto){
        System.out.println(dto);
        System.out.println(dto.getMyTeam());
        System.out.println(dto.getMyTeam().getKboTeam());

        User user = userService.signup(dto);

        CommonResDto resDto = new CommonResDto(HttpStatus.CREATED, "회원가입 완료", user.getUserId());
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody ReqLoginDto dto){

        User user = userService.login(dto);

        String token = jwtTokenProvider.createToken(user.getEmail(), user.getAuth().toString());

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("token", token);
        resMap.put("user_id", user.getUserId());

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "로그인 성공", resMap);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @GetMapping("/check_email")
    public ResponseEntity<?> checkEmail(@RequestParam String email){

        boolean checkEmail = userService.checkEmail(email);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "사용가능한 이메일입니다.", checkEmail);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }


    @GetMapping("/check_nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname){

        boolean checkNickname = userService.checkNickname(nickname);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "사용가능한 닉네임입니다.", checkNickname);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @PostMapping("/check_pw")
    public ResponseEntity<?> checkPw(@RequestBody String password){

//        boolean checkPassword = userService.checkPassword(password);

//        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "비밀번호가 일치합니다.", checkPassword);
//        return new ResponseEntity<>(resDto, HttpStatus.OK);
        return null;
    }

    @GetMapping("/user_info")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> userInfo(Pageable pageable) {

        List<UserResDto> ResDto = userService.userInfo(pageable);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "회원정보 조회 성공", ResDto);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @GetMapping("/my_info")
    public ResponseEntity<?> myInfo() {

        UserResDto dto = userService.myInfo();

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "myinfo 조회 성공", dto);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
