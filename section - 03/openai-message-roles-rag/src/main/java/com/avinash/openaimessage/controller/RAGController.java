package com.avinash.openaimessage.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;


@RestController
@RequestMapping("/api/rag")
public class RAGController {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:/promptTemplates/systemRandomDataTemplate.st")
    Resource promptTemplate;

    RAGController(@Qualifier("chatMemoryClient") ChatClient chatClient, VectorStore vectorStore){
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/random/chat")
    public ResponseEntity<String> getRandomChat(@RequestParam("message") String message,
                                                @RequestHeader("username") String username){

        //Creating the Search Request
        SearchRequest searchRequest = SearchRequest.builder()
                .query(message).topK(3).similarityThreshold(0.5).build();

        //Getting the similar documents based on the search request
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);

        //Converting the List<Documents> into get with the lineSperator
        String similarContext = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));

        //
        String answer = chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(promptTemplate).param("documents", similarContext))
                .advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message).call().content();

        return ResponseEntity.ok(answer);
    }
}
