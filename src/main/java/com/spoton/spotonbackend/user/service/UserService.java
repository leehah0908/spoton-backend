package com.spoton.spotonbackend.user.service;

import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.common.dto.CommonErrorDto;
import com.spoton.spotonbackend.user.dto.request.ReqLoginDto;
import com.spoton.spotonbackend.user.dto.request.ReqSignupDto;
import com.spoton.spotonbackend.user.dto.response.UserResDto;
import com.spoton.spotonbackend.user.entity.MyTeam;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.MyTeamRepository;
import com.spoton.spotonbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final MyTeamRepository myTeamRepository;

    private final PasswordEncoder passwordEncoder;

    public User signup(@Valid ReqSignupDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if (userRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        User user = dto.toUser(passwordEncoder);
        user.setMyTeam(dto.getMyTeam());

        return userRepository.save(user);
    }

    public User login(@Valid ReqLoginDto dto) {

        User loginUser = userRepository.findByEmail(dto.getEmail()).orElseThrow(() ->
                new EntityNotFoundException("회원 정보 찾을 수 없습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), loginUser.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return loginUser;
    }

    public boolean checkEmail(String email) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        return true;
    }

    public boolean checkNickname(String nickname) {

        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        return true;
    }

    public User checkPassword(String email, String password) {

        User loginUser = userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException("회원 정보 찾을 수 없음"));

        if (!passwordEncoder.matches(password, loginUser.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return loginUser;
    }

    public UserResDto myInfo() {
        TokenUserInfo userInfo =
                (TokenUserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        return user.toUserResDto();
    }

    public List<UserResDto> userInfo(Pageable pageable) {

        Page<User> userList = userRepository.findAll(pageable);

        return userList.stream()
                .map(User::toUserResDto)
                .collect(Collectors.toList());
    }

    public User modify(TokenUserInfo userInfo, ReqSignupDto dto) {

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );
        user.setNickname(dto.getNickname());

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

        return user;
    }

    public User findById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("회원정보를 찾을 수 없습니다.")
        );
    }

    public User profileSet(TokenUserInfo userInfo, MultipartFile imgFile) {

        String imagePath = UUID.randomUUID() + "_" + imgFile.getOriginalFilename();

        File savePath = new File("/Users/leehah/spoton/spoton-backend/src/main/resources/user_profile_image/" + imagePath);
        try {
            imgFile.transferTo(savePath);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패");
        }

        // 이미지 경로 저장
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        user.setProfile(imagePath);

        return user;
    }

    public User setNewPassword(String email, String newPw) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        user.setPassword(passwordEncoder.encode(newPw));

        return user;
    }
}
