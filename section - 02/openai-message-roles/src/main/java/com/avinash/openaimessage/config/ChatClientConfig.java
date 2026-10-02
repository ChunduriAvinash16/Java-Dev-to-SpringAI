package com.avinash.openaimessage.config;

import com.avinash.openaimessage.advisors.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), new TokenUsageAuditAdvisor()))
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
