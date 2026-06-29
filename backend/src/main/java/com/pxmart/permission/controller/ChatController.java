package com.pxmart.permission.controller;

import com.pxmart.permission.dto.ChatRequest;
import com.pxmart.permission.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public Map<String, String> chat(@RequestBody ChatRequest req) {
        String reply = chatService.chat(req);
        return Map.of("reply", reply);
    }
}
