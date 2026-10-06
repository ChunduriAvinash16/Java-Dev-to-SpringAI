package com.avinash.openaimessage.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PromptTemplateController {
    private final ChatClient chatClient;

    public PromptTemplateController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Value("classpath:/promptTemplates/systemTemplate.st")
    Resource systemPromptTemplate;

    @GetMapping("/prompt-stuffing")
    public String promptStuffing(@RequestParam("message") String message){
        return chatClient
                .prompt()
                .system(systemPromptTemplate)
                .user(message)
                .call()
                .content();
    }
}
