package com.spoton.spotonbackend.user.service;

import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.user.dto.request.ReqLoginDto;
import com.spoton.spotonbackend.user.dto.request.ReqModifyDto;
import com.spoton.spotonbackend.user.dto.request.ReqSignupDto;
import com.spoton.spotonbackend.user.dto.response.UserResDto;
import com.spoton.spotonbackend.user.entity.LoginType;
import com.spoton.spotonbackend.user.entity.MyTeam;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public User signup(@Valid ReqSignupDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(
                    () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
            );
            throw new IllegalArgumentException("이미 " + user.getLoginType() + "로 가입된 이메일입니다.");
        }

        if (userRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        User user = dto.toUser(passwordEncoder);
        user.setMyTeam(dto.getMyTeam());

        return userRepository.save(user);
    }

    // 로그인
    public User login(@Valid ReqLoginDto dto) {

        User loginUser = userRepository.findByEmail(dto.getEmail()).orElseThrow(() ->
                new EntityNotFoundException("회원 정보 찾을 수 없습니다."));

        if (loginUser.getLoginType() != LoginType.COMMON) {
            throw new IllegalArgumentException(loginUser.getLoginType() + "로 가입된 이메일입니다.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), loginUser.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return loginUser;
    }

    // 이메일 중복 체크
    public boolean checkEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
            );
            throw new IllegalArgumentException("이미 " + user.getLoginType() + "로 가입된 이메일입니다.");
        }
        return true;
    }

    // 닉네임 중복 체크
    public boolean checkNickname(String nickname) {

        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        return true;
    }

    // 비밀번호 일치 확인
    public User checkPassword(String email, String password) {

        User loginUser = userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException("회원 정보 찾을 수 없음"));

        if (!passwordEncoder.matches(password, loginUser.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return loginUser;
    }

    // 마이페이지용 회원 개인 정보 조회
    public UserResDto myInfo(TokenUserInfo userInfo) {

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        return user.toUserResDto();
    }

    // 회원관리용 회원 정보 리스트 조회
    public List<UserResDto> userInfo(Pageable pageable) {

        Page<User> userList = userRepository.findAll(pageable);

        return userList.stream()
                .map(User::toUserResDto)
                .collect(Collectors.toList());
    }

    // 회원 정보 수정
    public User modify(TokenUserInfo userInfo, ReqModifyDto dto) {

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        if (dto.getType().equals("nickname")) {
            user.setNickname(dto.getNickname());
        } else if (dto.getType().equals("myTeam")) {
            MyTeam myTeam = user.getMyTeam();
            myTeam.setKboTeam(dto.getMyTeam().getKboTeam());
            myTeam.setMlbTeam(dto.getMyTeam().getMlbTeam());
            myTeam.setKleagueTeam(dto.getMyTeam().getKleagueTeam());
            myTeam.setEplTeam(dto.getMyTeam().getEplTeam());
            myTeam.setKblTeam(dto.getMyTeam().getKblTeam());
            myTeam.setNbaTeam(dto.getMyTeam().getNbaTeam());
            myTeam.setKovoTeam(dto.getMyTeam().getKovoTeam());
            myTeam.setWkovwTeam(dto.getMyTeam().getWkovwTeam());
            myTeam.setLckTeam(dto.getMyTeam().getLckTeam());
        }
        return user;
    }

    // id로 유저 정보 찾기 (access 토큰 재발급시 필요)
    public User findById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("회원정보를 찾을 수 없습니다.")
        );
    }

    // 프로필 사진 업데이트
    public User profileSet(TokenUserInfo userInfo, MultipartFile imgFile) {
        // 이미지 경로 저장
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        if (imgFile != null) {
            String imagePath = UUID.randomUUID() + "_" + imgFile.getOriginalFilename();
            File savePath = new File("/Users/leehah/spoton/spoton-backend/src/main/resources/user_profile_image/" + imagePath);

            try {
                imgFile.transferTo(savePath);
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패");
            }
            user.setProfile(imagePath);
        } else {
            user.setProfile(null);
        }

        return user;
    }

    // 비밀번호 변경 (임시 비밀번호 지정)
    public User setNewPassword(String email, String newPw) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        user.setPassword(passwordEncoder.encode(newPw));
        user.setLoginType(LoginType.COMMON);

        System.out.println("변경완료");
        return user;
    }

    // 회원 탈퇴
    public void withdraw(TokenUserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        userRepository.delete(user);
    }

    // 프로필 사진 얻기
    public String getProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        return user.getProfile();
    }
}
