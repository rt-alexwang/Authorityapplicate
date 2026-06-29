package com.pxmart.permission.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.MessageParam;
import com.anthropic.models.messages.Model;
import com.pxmart.permission.dto.ChatRequest;
import com.pxmart.permission.entity.PermissionApplication;
import com.pxmart.permission.repository.PermissionApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    private final PermissionApplicationRepository repository;

    @Value("${anthropic.api-key:}")
    private String apiKey;

    public ChatService(PermissionApplicationRepository repository) {
        this.repository = repository;
    }

    public String chat(ChatRequest req) {
        try {
            AnthropicClient client = apiKey != null && !apiKey.isBlank()
                    ? AnthropicOkHttpClient.builder().apiKey(apiKey).build()
                    : AnthropicOkHttpClient.fromEnv();

            String systemPrompt = buildSystemPrompt();

            MessageCreateParams.Builder builder = MessageCreateParams.builder()
                    .model(Model.CLAUDE_OPUS_4_1_20250805)
                    .maxTokens(1024L)
                    .system(systemPrompt);

            if (req.getHistory() != null) {
                for (ChatRequest.HistoryItem item : req.getHistory()) {
                    if ("user".equals(item.getRole())) {
                        builder.addUserMessage(item.getContent());
                    } else if ("assistant".equals(item.getRole())) {
                        builder.addMessage(MessageParam.builder()
                                .role(MessageParam.Role.ASSISTANT)
                                .content(item.getContent())
                                .build());
                    }
                }
            }

            builder.addUserMessage(req.getMessage());

            Message response = client.messages().create(builder.build());
            return response.content().stream()
                    .flatMap(block -> block.text().stream())
                    .map(t -> t.text())
                    .collect(Collectors.joining());

        } catch (Exception e) {
            log.error("Claude API 呼叫失敗: {}", e.getMessage(), e);
            return "抱歉，目前無法連接 AI 服務，請稍後再試。（" + e.getMessage() + "）";
        }
    }

    private String buildSystemPrompt() {
        List<PermissionApplication> all = repository.findAllByOrderByCreatedAtDesc();

        long pending = all.stream()
                .filter(a -> a.getStatus() == PermissionApplication.ApplicationStatus.PENDING)
                .count();
        long approved = all.stream()
                .filter(a -> a.getStatus() == PermissionApplication.ApplicationStatus.APPROVED)
                .count();
        long rejected = all.stream()
                .filter(a -> a.getStatus() == PermissionApplication.ApplicationStatus.REJECTED)
                .count();

        List<PermissionApplication> recentPending = all.stream()
                .filter(a -> a.getStatus() == PermissionApplication.ApplicationStatus.PENDING)
                .limit(5)
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("你是 PXMart（全聯實業）DB 權限申請系統的客服助理。\n");
        sb.append("請用繁體中文回答，語氣專業但友善，回答盡量簡潔。\n\n");
        sb.append("=== 目前系統狀態（即時資料）===\n");
        sb.append("總申請數：").append(all.size()).append(" 筆\n");
        sb.append("待審核：").append(pending).append(" 筆\n");
        sb.append("已核准：").append(approved).append(" 筆\n");
        sb.append("已拒絕：").append(rejected).append(" 筆\n\n");

        if (!recentPending.isEmpty()) {
            sb.append("=== 最近待審核申請 ===\n");
            for (PermissionApplication app : recentPending) {
                String appNo = "APP-" + String.format("%06d", app.getId());
                sb.append("- ").append(appNo)
                  .append(" | ").append(app.getApplicantName())
                  .append(" | ").append(nvl(app.getRequestItem()))
                  .append(" | 送出：").append(app.getCreatedAt() != null ? app.getCreatedAt().format(FMT) : "—")
                  .append("\n");
            }
            sb.append("\n");
        }

        sb.append("=== 系統說明 ===\n");
        sb.append("本系統用於申請 DB（資料庫）存取權限。\n");
        sb.append("申請流程：填寫申請表 → 審核人員審核 → 核准/拒絕 → 系統寄送通知信。\n");
        sb.append("申請人可到前台（首頁）填寫申請精靈；審核人員可到後台管理頁面審核。\n");
        sb.append("申請單號格式為 APP-XXXXXX（6位數）。\n");
        sb.append("如需查詢特定申請單，請提供申請單號或申請人姓名。\n");

        return sb.toString();
    }

    private String nvl(String s) {
        return (s == null || s.isBlank()) ? "（未填）" : s;
    }
}
