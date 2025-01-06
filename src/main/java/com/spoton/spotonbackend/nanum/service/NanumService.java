package com.spoton.spotonbackend.nanum.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spoton.spotonbackend.board.entity.Board;
import com.spoton.spotonbackend.board.entity.BoardLike;
import com.spoton.spotonbackend.board.entity.BoardReport;
import com.spoton.spotonbackend.board.entity.ReplyLike;
import com.spoton.spotonbackend.common.auth.EmailProvider;
import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.nanum.dto.request.ReqNanumCreateDto;
import com.spoton.spotonbackend.nanum.dto.request.ReqNanumModifyDto;
import com.spoton.spotonbackend.nanum.dto.request.ReqNanumReportDto;
import com.spoton.spotonbackend.nanum.dto.response.ResNanumDto;
import com.spoton.spotonbackend.nanum.entity.Nanum;
import com.spoton.spotonbackend.nanum.entity.NanumLike;
import com.spoton.spotonbackend.nanum.entity.NanumReport;
import com.spoton.spotonbackend.nanum.repository.NanumLikeRepository;
import com.spoton.spotonbackend.nanum.repository.NanumReportRepository;
import com.spoton.spotonbackend.nanum.repository.NanumRepository;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spoton.spotonbackend.nanum.entity.QNanum.*;

@Service
@RequiredArgsConstructor
@Transactional
public class NanumService {

    private final UserRepository userRepository;
    private final NanumRepository nanumRepository;
    private final NanumReportRepository nanumReportRepository;
    private final NanumLikeRepository nanumLikeRepository;

    private final JPAQueryFactory queryFactory;
    private final EmailProvider emailProvider;

    public Page<ResNanumDto> list(String searchType, String searchKeyword, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // 쿼리 조각 만들기
        if (searchKeyword != null) {
            switch (searchType) {
                case "writer" -> builder.and(nanum.user.nickname.like("%" + searchKeyword + "%"));
                case "subject" -> builder.and(nanum.subject.like("%" + searchKeyword + "%"));
                case "content" -> builder.and(nanum.content.like("%" + searchKeyword + "%"));
            }
        }

        // 검색 및 페이징 처리
        List<Nanum> rawNanums = queryFactory
                .selectFrom(nanum)
                .where(builder)
                .orderBy(nanum.createTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // page 객체로 만들기 위해 총 검색 결과 수
        long total = queryFactory
                .selectFrom(nanum)
                .where(builder)
                .fetchCount();

        // page 객체 생성.
        Page<Nanum> nanums = new PageImpl<>(rawNanums, pageable, total);

        return nanums.map(Nanum::toResNanumDto);
    }

    public Nanum create(ReqNanumCreateDto dto, List<MultipartFile> imagePaths, TokenUserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다.")
        );

        Nanum nanum = dto.toNanum();
        nanum.setUser(user);

        Nanum saveNanum = nanumRepository.save(nanum);

        List<String> images = new ArrayList<>();

        for (MultipartFile image : imagePaths) {
            String imagePath = saveNanum.getNanumId() + "_" + image.getOriginalFilename();
            File savePath = new File("/Users/leehah/spoton/spoton-frontend/public/nanum_img/" + imagePath);
            images.add(imagePath);

            try {
                image.transferTo(savePath);
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패");
            }
        }

        saveNanum.setImagePath(images);
        saveNanum.setThumbnail(images.get(0));
        return nanumRepository.save(saveNanum);
    }

    public Nanum modify(ReqNanumModifyDto dto, List<MultipartFile> imagePaths, TokenUserInfo userInfo) {
        Nanum nanum = nanumRepository.findById(dto.getNanumId()).orElseThrow(
                () -> new EntityNotFoundException("나눔글을 찾을 수 없습니다.")
        );

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다.")
        );

        if (!nanum.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        List<String> images = new ArrayList<>();

        for (MultipartFile image : imagePaths) {
            String imagePath = UUID.randomUUID() + "_" + image.getOriginalFilename();
            File savePath = new File("/Users/leehah/spoton/spoton-backend/src/main/resources/user_nanum_image/" + imagePath);
            images.add(imagePath);

            try {
                image.transferTo(savePath);
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패");
            }
        }

        nanum.setImagePath(images);
        nanum.setSubject(dto.getSubject());
        nanum.setContent(dto.getContent());
        nanum.setSports(dto.getSports());
        nanum.setQuantity(dto.getQuantity());
        nanum.setGiveMethod(dto.getGiveMethod());

        return nanum;
    }

    public void delete(Long nanumdId, TokenUserInfo userInfo) {
        Nanum nanum = nanumRepository.findById(nanumdId).orElseThrow(
                () -> new EntityNotFoundException("나눔글을 찾을 수 없습니다.")
        );

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다.")
        );

        if (!nanum.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        nanumRepository.delete(nanum);
    }

    public ResNanumDto nanumDetail(Long nanumdId) {

        Nanum nanum = nanumRepository.findById(nanumdId).orElseThrow(
                () -> new EntityNotFoundException("게시물 정보를 찾을 수 없습니다.")
        );

        return nanum.toResNanumDto();
    }

    public List<ResNanumDto> lastestNanum() {

        List<Nanum> lastestNanums = nanumRepository.findTop10ByOrderByCreateTimeDesc();

        return lastestNanums.stream().map(Nanum::toResNanumDto).collect(Collectors.toList());
    }

    public String sendReport(ReqNanumReportDto dto, TokenUserInfo userInfo) {
        Nanum nanum = nanumRepository.findById(dto.getNanumId()).orElseThrow(
                () -> new EntityNotFoundException("나눔글을 찾을 수 없습니다.")
        );

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        if (!nanumReportRepository.existsByUser_UserIdAndNanum_NanumId(user.getUserId(), dto.getNanumId())) {

            // 관리자에게 신고 내역 보내기
            String result = emailProvider.sendReportMail(dto.getNanumId(), dto.getReportContent(), userInfo, "나눔글");
            if (result.equals("fail")) {
                return "email send fail";
            }

            NanumReport nanumReport = new NanumReport();
            nanumReport.setNanum(nanum);
            nanumReport.setUser(user);

            nanumReportRepository.save(nanumReport);
            nanum.setReportCount(nanum.getReportCount() + 1);

            return "success";
        }
        return "existed";
    }

    public Nanum increaseViewCount(Long nanumId) {

        Nanum nanum = nanumRepository.findById(nanumId).orElseThrow(
                () -> new EntityNotFoundException("나눔글을 찾을 수 없습니다.")
        );

        nanum.setViewCount(nanum.getViewCount() + 1);

        return nanum;
    }

    public void likeCount(Long nanumId, TokenUserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        Nanum nanum = nanumRepository.findById(nanumId).orElseThrow(
                () -> new EntityNotFoundException("나눔글을 찾을 수 없습니다.")
        );

        if (nanumLikeRepository.existsByUser_UserIdAndNanum_NanumId(user.getUserId(), nanumId)) {
            NanumLike nanumLike = nanumLikeRepository.findByUser_UserIdAndNanum_NanumId(user.getUserId(), nanumId).orElseThrow(
                    () -> new EntityNotFoundException("좋아요 로그를 찾을 수 없습니다.")
            );

            nanumLikeRepository.delete(nanumLike);
            nanum.setLikeCount(nanum.getLikeCount() - 1);
        } else {
            NanumLike nanumLike = new NanumLike();
            nanumLike.setNanum(nanum);
            nanumLike.setUser(user);

            nanumLikeRepository.save(nanumLike);
            nanum.setLikeCount(nanum.getLikeCount() + 1);
        }
    }

    public List<String> nanumLikeList(Long nanumId) {

        List<NanumLike> nanumLikes = nanumLikeRepository.findByNanum_NanumId(nanumId).orElseThrow(
                () -> new EntityNotFoundException("이 나눔글의 좋아요 명단을 찾을 수 없음.")
        );

        return nanumLikes.stream().map(nanumLike -> nanumLike.getUser().getEmail()).toList();
    }

    public boolean chaneStatus(Long nanumId, TokenUserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원정보를 찾을 수 없습니다.")
        );

        Nanum nanum = nanumRepository.findById(nanumId).orElseThrow(
                () -> new EntityNotFoundException("나눔글을 찾을 수 없습니다.")
        );

        if (user.getUserId().equals(nanum.getUser().getUserId())) {
            nanum.setStatus("나눔 종료");
            return true;
        }
        return false;
    }
}
