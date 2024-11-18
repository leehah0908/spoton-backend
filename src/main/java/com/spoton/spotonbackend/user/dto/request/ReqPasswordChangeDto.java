package com.spoton.spotonbackend.user.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqPasswordChangeDto {

    private String oldPassword;
    private String newPassword;

}
