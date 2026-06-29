package com.pxmart.permission.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mail")
public class MailTestController {

    private final JavaMailSender mailSender;

    @Value("${mail.from:authority-system@pxmart.com.tw}")
    private String from;

    @Value("${spring.mail.host:未設定}")
    private String host;

    @Value("${spring.mail.port:0}")
    private int port;

    public MailTestController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /** 查看目前 SMTP 設定 */
    @GetMapping("/config")
    public Map<String, Object> config() {
        return Map.of("host", host, "port", port, "from", from);
    }

    /** 測試寄信：GET /api/mail/test?to=your@email.com */
    @GetMapping("/test")
    public Map<String, String> test(@RequestParam String to) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject("【權限申請系統】郵件測試");
            msg.setText("這是一封測試郵件，確認 SMTP 設定正確。\n\nSMTP: " + host + ":" + port);
            mailSender.send(msg);
            return Map.of("result", "success", "message", "測試信已寄出至 " + to);
        } catch (Exception e) {
            return Map.of("result", "error", "message", e.getMessage(),
                          "cause", e.getCause() != null ? e.getCause().getMessage() : "");
        }
    }
}
