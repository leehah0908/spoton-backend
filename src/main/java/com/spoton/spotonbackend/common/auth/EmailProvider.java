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

    // 이메일 인증 메일 보내기
    public Integer sendCertificationMail(String email) {

        String subject = "[SpotOn] 이메일 인증 메일입니다.";
        int number = makeRandomNumber();

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            String htmlContent = getCertificationMessage(number);

            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
        return number;
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


    // 임시 비밀번호 메일 보내기
    public String sendTemporaryPassword(String email) {

        String subject = "[SpotOn] 임시 비밀번호입니다.";
        String temporaryPassword = makeRandomPassword();

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            String htmlContent = getTemporaryPassword(temporaryPassword);

            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return temporaryPassword;
    }

    private String getTemporaryPassword(String temporaryPassword) {
        String temporaryPasswordMessage = "";

        temporaryPasswordMessage += "<div>\n" +
                "임시 비밀번호입니다." +
                "<br>\n" +
                "<strong style=\"font-size: 30px;\">" + temporaryPassword + "</strong>" +
                "<br>\n" +
                "임시 비밀번호로 로그인을 한 후 비밀번호 변경을 해주세요." +
                "</div>";

        return temporaryPasswordMessage;
    }

    private String makeRandomPassword() {
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        StringBuilder newPw = new StringBuilder();

        for (int i = 0; i < 13; i++) {
            int idx = (int) (charSet.length * Math.random());
            newPw.append(charSet[idx]);
        }
        return newPw.toString();
    }


    // 신고 내역 메일 보내기
    public String sendReportMail(Long id,
                                 String content,
                                 TokenUserInfo userInfo,
                                 String type) {

        String subject = "[SpotOn] 신고 내역 메일입니다.";

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            String htmlContent = getReportDetail(id, content, userInfo, type);

            mimeMessageHelper.setTo("leehah0908@naver.com");
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    private String getReportDetail(Long id,
                                   String content,
                                   TokenUserInfo userInfo,
                                   String type) {
        String reportDetail = "";

        reportDetail += "<div>\n" +
                userInfo.getEmail() + "님이" +
                "<br>\n" +
                "<strong style=\"font-size: 30px;\">아래와 같은 이유로" + id + " " + type + "을 신고했습니다.</strong>" +
                "<br>\n" +
                content +
                "</div>";

        return reportDetail;
    }
}
