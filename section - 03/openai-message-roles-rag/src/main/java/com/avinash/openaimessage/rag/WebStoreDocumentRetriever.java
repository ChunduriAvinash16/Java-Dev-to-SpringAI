package com.avinash.openaimessage.rag;

import com.google.common.net.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;
import java.util.List;

public class WebStoreDocumentRetriever implements DocumentRetriever {


    private static final Logger LOG = LoggerFactory.getLogger(WebStoreDocumentRetriever.class);
    private static final String TAVILY_API_KEY = "TAVILY_API_KEY";
    private static final String TAVILY_BASE_URL = "https://api.tavily.com/search";
    private static final int DEFAULT_RESULT_LIMIT = 5;

    private final int resultLimit;
    private final RestClient restClient;

    WebStoreDocumentRetriever(RestClient.Builder clientBuilder, int resultLimit) {
        Assert.notNull(clientBuilder, "clientBuilder must not be null");
        String apiKey = System.getenv(TAVILY_API_KEY);
        Assert.hasText(apiKey, "apiKey must not be empty");

        this.restClient = clientBuilder
                .baseUrl(TAVILY_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();

        if(resultLimit <= 0) {
            throw new IllegalArgumentException("resultLimit must be greater than 0");
        }
        this.resultLimit = resultLimit;
    }


    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record TavilyRequestPayload(String query, String searchDepth, int maxResults) {}

    record TavilyResponsePayload(List<Hit> results) {
        record Hit(String title, String url, String content, Double score) {}
    }


    @Override
    public List<Document> retrieve(Query query) {
        LOG.info("Processing query {}", query);
        Assert.notNull(query, "query must not be null");

        String qr = query.text();
        Assert.hasText(qr, "qr must not be empty");

        TavilyResponsePayload response = restClient.post()
                .body(new TavilyRequestPayload(qr, "advanced", resultLimit))
                .retrieve()
                .body(TavilyResponsePayload.class);

        if(response == null || CollectionUtils.isEmpty(response.results)) {
            return List.of();
        }

        List<Document> docs = new ArrayList<>(response.results.size());
        for(TavilyResponsePayload.Hit hit : response.results) {
            Document document = Document.builder()
                    .text(hit.content())
                    .metadata("metaData", hit.title())
                    .metadata("url", hit.url())
                    .score(hit.score())
                    .build();
            docs.add(document);
        }
        return docs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RestClient.Builder clientBuilder;
        private int resultLimit = DEFAULT_RESULT_LIMIT;

        private Builder() {}

        public Builder restClient(RestClient.Builder clientBuilder) {
            this.clientBuilder = clientBuilder;
            return this;
        }

        public Builder maxResults(int maxResult) {
            if(maxResult <= 0) {
                throw new IllegalArgumentException("maxResults must be greater than 0");
            }
            this.resultLimit = maxResult;
            return this;
        }

        public WebStoreDocumentRetriever build() {
            return new WebStoreDocumentRetriever(clientBuilder, resultLimit);
        }
    }
}
