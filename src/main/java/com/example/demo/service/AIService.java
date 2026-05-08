package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class AIService {

    @Value("${gemini.api.key:YOUR_API_KEY}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";

    public String callOpenRouter(String textPrompt) {
        return callOpenRouter(textPrompt, null, null);
    }

    @SuppressWarnings("unchecked")
    public String callOpenRouter(String textPrompt, String base64Image, String mimeType) {
        if (geminiApiKey == null || geminiApiKey.equals("YOUR_API_KEY") || geminiApiKey.equals("NO_KEY")) {
            throw new RuntimeException("API Key no configurada. Por favor, configura la variable de entorno OPENROUTER_API_KEY.");
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "google/gemini-2.0-flash-lite-001");

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");

        if (base64Image == null) {
            message.put("content", textPrompt);
        } else {
            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", textPrompt);

            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image_url");
            Map<String, String> imageUrlMap = new HashMap<>();
            imageUrlMap.put("url", "data:" + (mimeType != null ? mimeType : "image/jpeg") + ";base64," + base64Image);
            imageContent.put("image_url", imageUrlMap);

            message.put("content", List.of(textContent, imageContent));
        }

        requestBody.put("messages", List.of(message));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + geminiApiKey);
        headers.set("HTTP-Referer", "http://localhost:5173");
        headers.set("X-Title", "CreatorCV");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENROUTER_URL, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> messageObj = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) messageObj.get("content");
                    return cleanJsonResponse(content);
                }
            }
            throw new RuntimeException("Sin respuesta de la IA.");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                throw new RuntimeException("RATE_LIMIT_EXCEEDED");
            }
            throw new RuntimeException("Error API: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación con IA: " + e.getMessage());
        }
    }

    private String cleanJsonResponse(String raw) {
        int firstBrace = raw.indexOf("{");
        int lastBrace = raw.lastIndexOf("}");
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return raw.substring(firstBrace, lastBrace + 1);
        }
        return raw;
    }
}
