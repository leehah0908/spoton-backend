package com.spoton.spotonbackend.nanum.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResNanumDto {
    private Long nanumId;

    private List<String> imagePath;
    private String subject;
    private String content;
    private String sports;
    private Long quantity;
    private String giveMethod;
    private Long viewCount;
    private Long likeCount;
    private Long chatCount;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String nickname;
    private String profile;
    private String email;
}
