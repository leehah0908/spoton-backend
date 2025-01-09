package com.spoton.spotonbackend.nanum.dto.request;

import com.spoton.spotonbackend.nanum.entity.Nanum;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqNanumCreateDto {

    @NotEmpty(message = "제목은 필수값입니다.")
    private String subject;

    @NotEmpty(message = "내용은 필수값입니다.")
    private String content;

    private String sports;
    private Long quantity;
    private String giveMethod;

    public Nanum toNanum() {
        return Nanum.builder()
                .subject(subject)
                .content(content)
                .sports(sports)
                .quantity(quantity)
                .giveMethod(giveMethod)
                .build();
    }
}
