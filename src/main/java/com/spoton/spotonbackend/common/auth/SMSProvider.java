package com.spoton.spotonbackend.common.auth;

import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;

@Component
public class SMSProvider {

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.api.number}")
    private String fromPhoneNumber;

    public Integer sendCertificationSms(String toNumber)  {

        // 랜덤 4자리 인증번호 생성
        int number = makeRandomNumber();

        try {
            Message smsMessage = new Message(apiKey, apiSecret);

            HashMap<String, String> params = new HashMap<>();
            params.put("to", toNumber);
            params.put("from", fromPhoneNumber);
            params.put("type", "sms");
            params.put("text", "[SpotOn] 인증번호는 [" + number + "] 입니다.");

            System.out.println(params.toString());

            // 메시지 전송
            smsMessage.send(params);

            return number;
        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
    }

    // 랜덤 4자리 숫자 생성
    private int makeRandomNumber() {
        // 난수의 범위: 111111 ~ 999999 (6자리)
        return (int) ((Math.random() * 888889) + 111111);
    }
}
