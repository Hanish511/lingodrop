package com.lingodrop.server.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.lingodrop.server.dto.DeepLTranslationRequest;
import com.lingodrop.server.dto.DeepLTranslationResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service 
public class DeepLTranslationClient implements TranslationService {

    private static final Logger log = LoggerFactory.getLogger(DeepLTranslationClient.class);

    private final RestClient restClient;
    private final String apiKey;

    public DeepLTranslationClient(
            RestClient.Builder restClientBuilder,
            @Value("${lingodrop.translation.deepl.base-url}") String baseUrl,
            @Value("${lingodrop.translation.deepl.api-key}") String apiKey) {
        
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    @Override
    @Cacheable(value = "translations", key = "{#text, #sourceLanguage, #targetLanguage}", unless = "#result == #text")
    @CircuitBreaker(name = "translationService", fallbackMethod = "fallbackTranslate")
    public String translate(String text, String sourceLanguage, String targetLanguage) {
        DeepLTranslationRequest requestBody = new DeepLTranslationRequest(
            List.of(text),
            sourceLanguage.toUpperCase(),
            targetLanguage.toUpperCase()
        );

        DeepLTranslationResponse response = restClient.post()
                .uri("/v2/translate")
                .header("Authorization", "DeepL-Auth-Key " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(DeepLTranslationResponse.class);

        if(response == null || response.translations() == null || response.translations().isEmpty()){
            throw new IllegalStateException("Empty translation response received from DeepL");
        }

        return response.translations().get(0).text();
    }

    public String fallbackTranslate(String text, String sourceLanguage, String targetLanguage, Throwable throwable){
        log.warn("DeepL translation failed for [{}] -> [{}]. Falling back to original text. Error: {}", sourceLanguage, targetLanguage, throwable.getMessage());
        return text;
    }

}
