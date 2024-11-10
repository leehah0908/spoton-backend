package com.spoton.spotonbackend.common.auth;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender mailSender;

    public boolean sendCertificationMail(String email) {

        try {
            String subject = "[SpotOn] 이메일 인증 메일입니다.";

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            String htmlContent = getCertificationMessage(makeRandomNumber());

            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String getCertificationMessage(int certificationNumber) {
        String certificationMessage = "";
        certificationMessage += "<div>\n" +
                "인증코드를 확인해주세요." +
                "<br>\n" +
                "<strong style=\"font-size: 30px;\">" + certificationNumber + "</strong>" +
                "<br>\n" +
                "해당 인증 번호를 인증번호 확인란에 기입해 주세요." +
                "</div>";

        return certificationMessage;
    }

    private int makeRandomNumber() {
        // 난수의 범위: 111111 ~ 999999 (6자리)
        return (int) ((Math.random() * 888889) + 111111);
    }
}
