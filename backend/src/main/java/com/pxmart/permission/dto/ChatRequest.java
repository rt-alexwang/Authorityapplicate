package com.pxmart.permission.dto;

import java.util.List;

public class ChatRequest {

    private String message;
    private List<HistoryItem> history;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<HistoryItem> getHistory() { return history; }
    public void setHistory(List<HistoryItem> history) { this.history = history; }

    public static class HistoryItem {
        private String role;
        private String content;

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
