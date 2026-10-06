package com.avinash.openaimessage.controller;


import com.avinash.openaimessage.model.CountryCities;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StructuredOutputController {

    private final ChatClient chatClient;

    public StructuredOutputController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }

    @GetMapping("/structured-output")
    public ResponseEntity<CountryCities> chatBean(@RequestParam("message") String message){
        CountryCities countryCities = chatClient
                .prompt()
                .user(message)
                .call()
                //.entity(new BeanOutputConverter<>(CountryCities.class));
                .entity(CountryCities.class);
        return ResponseEntity.ok(countryCities);
    }


    @GetMapping("/structured-output-list")
    public ResponseEntity<List<String>> chatList(@RequestParam("message") String message){
        List<String> countryCities = chatClient
                .prompt()
                .user(message)
                .call()
                .entity(new ListOutputConverter());
        return ResponseEntity.ok(countryCities);
    }

    @GetMapping("/structured-output-map")
    public ResponseEntity<Map<String,Object>> chatMap(@RequestParam("message") String message){
        Map<String,Object> countryCities = chatClient
                .prompt()
                .user(message)
                .call()
                .entity(new MapOutputConverter());
        return ResponseEntity.ok(countryCities);
    }

    @GetMapping("/structured-output-list-country")
    public ResponseEntity<List<CountryCities>> chatCountryList(@RequestParam("message") String message){
        List<CountryCities> countryCities = chatClient
                .prompt()
                .user(message)
                .call()
                .entity(new ParameterizedTypeReference<List<CountryCities>>() {});
        return ResponseEntity.ok(countryCities);
    }
}
