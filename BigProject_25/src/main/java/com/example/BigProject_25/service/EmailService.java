package com.example.BigProject_25.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 이메일 인증 함수
    public void sendVerificationEmail(String recipientAddress, String token) {
        String subject = "[AIrport] 이메일 인증을 진행해주세요.";
        String confirmationUrl = "http://localhost:8080/auth/confirm?token=" + token;
        String message = "아래의 링크를 클릭해 이메일을 인증하고 회원가입 과정을 완료합니다.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + confirmationUrl);
        mailSender.send(email);
    }

    // 임시 비밀번호 전송 함수
    public void sendTemporaryPasswordEmail(String recipientAddress, String tempPassword) {
        String subject = "[AIrport] 임시 비밀번호입니다.";
        String message = "아래의 임시 비밀번호로 로그인하고 비밀번호를 변경해주시기 바랍니다.\n\n임시 비밀번호: " + tempPassword;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }
}
