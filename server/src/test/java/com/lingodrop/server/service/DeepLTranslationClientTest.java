package com.lingodrop.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@SpringBootTest
@ActiveProfiles("test") 
public class DeepLTranslationClientTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("lingodrop.translation.deepl.base-url", wireMock::baseUrl);
        registry.add("lingodrop.translation.deepl.api-key", () -> "test-api-key");
    }

    @Autowired 
    private TranslationService translationService;

    @Autowired 
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        wireMock.resetAll();
        if(circuitBreakerRegistry != null){
            circuitBreakerRegistry.getAllCircuitBreakers()
                        .forEach(cb -> cb.reset());
        }
        
        if(cacheManager != null){
            cacheManager.getCacheNames()
                    .forEach(name -> cacheManager.getCache(name).clear());
        }
    }

    @Test 
    void shouldTranslateTextSuccessfully() {
        wireMock.stubFor(post(urlEqualTo("/v2/translate"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("""
                            {
                                "translations": [
                                    {
                                        "detected_source_language": "EN",
                                        "text": "Hola Mundo"
                                    }
                                ]
                            }
                        """)));
        
        String result = translationService.translate("Hello World", "en", "es");
        assertEquals("Hola Mundo", result);
    }

    @Test 
    void shouldFallbackToOriginalTextOnHttp500Error() {
        wireMock.stubFor(post(urlEqualTo("/v2/translate"))
                .willReturn(aResponse()
                        .withStatus(500)));
        
        String result = translationService.translate("Hello World", "en", "es");
        assertEquals("Hello World", result);
    }

    @Test 
    void shouldFallbackToOriginalTextOnTimeout() {
        wireMock.stubFor(post(urlEqualTo("/v2/translate"))
                .willReturn(aResponse()
                        .withFixedDelay(3000)
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("""
                            {
                                "translations": [
                                    {
                                        "detected_source_language": "EN",
                                        "text": "Hola Mundo"
                                    }
                                ]
                            }
                        """)));
            
        String result = translationService.translate("Hello World", "en", "es");
        assertEquals("Hello World", result);
    }

    @Test
    void shouldCacheTranslationAndAvoidDuplicateHttpRequests() {
        wireMock.stubFor(post(urlEqualTo("/v2/translate"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("""
                            {
                                "translations": [
                                    {
                                        "detected_source_language": "EN",
                                        "text": "Hola Mundo"
                                    }
                                ]
                            }
                        """)));

        String result1 = translationService.translate("Hello World", "en", "es");
        assertEquals("Hola Mundo", result1);

        String result2 = translationService.translate("Hello World", "en", "es");
        assertEquals("Hola Mundo", result2);

        wireMock.verify(1, postRequestedFor(urlEqualTo("/v2/translate")));
    }

}
