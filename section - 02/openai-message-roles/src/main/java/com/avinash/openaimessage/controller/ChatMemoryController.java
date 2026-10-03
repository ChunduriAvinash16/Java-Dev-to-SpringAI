package com.avinash.openaimessage.controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatMemoryController {

    private final ChatClient chatClient;

    public ChatMemoryController(@Qualifier("chatMemoryClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat-memory")
    public String helloController(@RequestParam("message") String message){
        return chatClient
                .prompt()
                .user(message)
                .advisors(advisorSpec -> {
                    advisorSpec.param(ChatMemory.CONVERSATION_ID, "default");
                })
                .call()
                .content();
    }
}
