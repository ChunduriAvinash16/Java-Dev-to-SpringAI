package com.avinash.openaimessage.controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/chat")
    public String helloController(@RequestParam("message") String message){
        return chatClient
                .prompt()
                .system("""
                        You are internal HR assistant. Your role is to help\s
                        employees with questions related to HR policies such as leave policies, working hours\s
                        benefits and code of conduct. If user ask anything outside this topics \s
                        kindly inform them that you can only assist with queries related to HR policies.
                        """)
                .user(message)
                .call()
                .content();
    }
}
