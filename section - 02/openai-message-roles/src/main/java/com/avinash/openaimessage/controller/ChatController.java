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

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String helloController(@RequestParam("message") String message){
        return chatClient
                .prompt()
                // overrideing the default system message
                /*
                .system("""
                        You are an internal IT helpdesk assistant. Your role is to help\s
                        assist employees with IT related issues such as resetting password \s
                        updating the software , unloacking the accounts \s
                        If user ask anything outside this topics \s
                        kindly inform them that you can only assist with queries related to IT.
                        """)
                 */
                .user(message)
                .call()
                .content();
    }
}
