package com.spoton.spotonbackend.nanum.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spoton.spotonbackend.board.dto.response.ResBoardDto;
import com.spoton.spotonbackend.common.entity.BaseTimeEntity;
import com.spoton.spotonbackend.nanum.dto.response.ResNanumDto;
import com.spoton.spotonbackend.nanumChat.entity.NanumChatRoom;
import com.spoton.spotonbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "nanum")
public class Nanum extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nanumId;

    @Setter
    @ElementCollection
    @CollectionTable(name = "image_path", joinColumns = @JoinColumn(name = "nanumId"))
    @JsonIgnore
    private List<String> imagePath;

    @Setter
    private String thumbnail;

    @Column(nullable = false)
    @Setter
    private String subject;

    @Column(nullable = false)
    @Setter
    private String content;

    @Setter
    private String sports;

    @Setter
    private Long quantity;

    @Setter
    private String giveMethod;

    @Builder.Default
    @Setter
    private Long viewCount = 0L;

    @Builder.Default
    @Setter
    private Long likeCount = 0L;

    @Builder.Default
    @Setter
    private Long reportCount = 0L;

    @Builder.Default
    @Setter
    private Long chatCount = 0L;

    @Builder.Default
    @Setter
    private String status = "나눔중";

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "nanum", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    private List<NanumLike> nanumLikes = new ArrayList<>();

    @OneToMany(mappedBy = "nanum", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    private List<NanumReport> nanumReports = new ArrayList<>();

    @OneToMany(mappedBy = "nanum", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    private List<NanumChatRoom> nanumChatRooms = new ArrayList<>();

    public ResNanumDto toResNanumDto() {
        return ResNanumDto.builder()
                .nanumId(nanumId)
                .imagePath(imagePath)
                .subject(subject)
                .content(content)
                .sports(sports)
                .quantity(quantity)
                .giveMethod(giveMethod)
                .viewCount(viewCount)
                .likeCount(likeCount)
                .chatCount(chatCount)
                .createTime(getCreateTime())
                .updateTime(getUpdateTime())
                .nickname(user.getNickname())
                .profile(user.getProfile())
                .email(user.getEmail())
                .status(status)
                .build();
    }
}
