package com.avinash.openaimessage.config;

import com.avinash.openaimessage.advisors.TokenUsageAuditAdvisor;
import com.avinash.openaimessage.rag.WebStoreDocumentRetriever;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
public class WebSearchatRAGChatClientConfig {

    @Bean("webSearchRAGChatClient")
    public ChatClient webSearchRAGChatClient(ChatClient.Builder chatClientBuilder,
                                             ChatMemory chatMemory, RestClient.Builder restClientBuilder) {
        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        Advisor webSearchRAGAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(
                        WebStoreDocumentRetriever.builder()
                                .restClient(restClientBuilder).maxResults(5).build()
                ).build();

        return chatClientBuilder
                .defaultAdvisors(List.of(loggerAdvisor, tokenUsageAdvisor, memoryAdvisor, webSearchRAGAdvisor))
                .build();
    }
}
