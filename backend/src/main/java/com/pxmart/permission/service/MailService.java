package com.pxmart.permission.service;

import com.pxmart.permission.entity.PermissionApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    private final JavaMailSender mailSender;

    @Value("${mail.from:authority-system@pxmart.com.tw}")
    private String from;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /** 申請送出通知 */
    @Async
    public void sendApplicationReceived(PermissionApplication app) {
        if (app.getApplicantEmail() == null || app.getApplicantEmail().isBlank()) return;
        try {
            String appNo = "APP-" + String.format("%06d", app.getId());
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(app.getApplicantEmail());
            msg.setSubject("【權限申請系統】申請單已收到 - " + appNo);
            msg.setText(buildReceivedBody(app, appNo));
            mailSender.send(msg);
            log.info("申請通知信已寄出：{} -> {}", appNo, app.getApplicantEmail());
        } catch (Exception e) {
            log.error("申請通知信寄送失敗（{}）：{}", app.getApplicantEmail(), e.getMessage(), e);
        }
    }

    private String buildReceivedBody(PermissionApplication app, String appNo) {
        return String.join("\n",
            app.getApplicantName() + " 您好，",
            "",
            "您的權限申請單已成功送出，目前進入審核流程，請等待審核人員審核。",
            "",
            "─────────────────────────────",
            "申請編號：" + appNo,
            "申請項目：" + nvl(app.getRequestItem()),
            "申請帳號：" + nvl(app.getRequestAccounts()),
            "需求說明：" + nvl(app.getDescription()),
            "送出時間：" + (app.getCreatedAt() != null ? app.getCreatedAt().format(FMT) : "—"),
            "─────────────────────────────",
            "",
            "如有任何問題，請聯絡 IT 部門。",
            "",
            "此郵件由系統自動發送，請勿直接回覆。",
            "PXMart 權限申請系統"
        );
    }

    private String nvl(String s) {
        return (s == null || s.isBlank()) ? "（未填）" : s;
    }
}
