package com.avinash.openaimessage.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultUser("How can I Help you today ?")
                .defaultSystem("""
                        You are internal HR assistant. Your role is to help\s
                        employees with questions related to HR policies such as leave policies, working hours\s
                        benefits and code of conduct. If user ask anything outside this topics \s
                        kindly inform them that you can only assist with queries related to HR policies.
                        """)
                .build();
    }
}
