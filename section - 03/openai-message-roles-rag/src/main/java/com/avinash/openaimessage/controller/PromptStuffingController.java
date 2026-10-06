package com.avinash.openaimessage.controller;

import com.openai.models.ChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PromptStuffingController {
    private final ChatClient chatClient;

    public PromptStuffingController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Value("classpath:/promptTemplates/userTemplate.st")
    Resource promptTemplate;

    @GetMapping("/email")
    public String emailResponse(@RequestParam("customerName") String customerName,
                                @RequestParam("customerMessage") String customerMessage){
        return chatClient
                .prompt()
                .options(OpenAiChatOptions.builder().model(ChatModel.GPT_3_5_TURBO.asString()).maxCompletionTokens(100))
                .system("""
                         You are a professional customer service assistant which helps drafting email
                          responses to improve the productivity of the customer support team
                        """)
                .user(promptUserSpec -> promptUserSpec.text(promptTemplate)
                        .param("customerName", customerName)
                        .param("customerMessage", customerMessage))
                .call()
                .content();
    }
}
