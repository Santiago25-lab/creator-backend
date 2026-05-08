package com.example.demo.controller;

import com.example.demo.service.AIService;
import com.example.demo.service.PromptBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/cv/ai")
public class CVAIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private PromptBuilder promptBuilder;

    @PostMapping("/generate")
    public ResponseEntity<?> generateCV(@RequestBody CVRequest request) {
        String userPrompt = request.getPrompt();
        Map<String, Object> currentState = request.getCurrentState();
        
        if (userPrompt == null || userPrompt.isEmpty()) {
            return ResponseEntity.badRequest().body("El prompt no puede estar vacío");
        }

        String currentStateStr = (currentState != null) ? currentState.toString() : "{}";
        String systemInstruction = promptBuilder.buildCVGenerationSystemInstruction(currentStateStr);
        String finalPrompt = systemInstruction + "\n\nMENSAJE DEL USUARIO: " + userPrompt;

        try {
            String result = aiService.callOpenRouter(finalPrompt);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            if ("RATE_LIMIT_EXCEEDED".equals(e.getMessage())) {
                return ResponseEntity.status(429).body("{\"ai_message\":\"El servicio de IA está temporalmente saturado (rate limit). Espera unos segundos e intenta de nuevo.\",\"personalInfo\":{},\"experience\":[],\"education\":[],\"skills\":[],\"languages\":[]}");
            }
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/analyze-style")
    public ResponseEntity<?> analyzeStyle(@RequestBody Map<String, String> request) {
        String styleDescription = request.get("prompt");
        String prompt = promptBuilder.buildStyleAnalysisPrompt(styleDescription);
        try {
            return ResponseEntity.ok(aiService.callOpenRouter(prompt));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/analyze-design")
    public ResponseEntity<?> analyzeDesign(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }
        try {
            byte[] bytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            String mimeType = file.getContentType();
            String prompt = promptBuilder.buildDesignCloningPrompt();

            return ResponseEntity.ok(aiService.callOpenRouter(prompt, base64Image, mimeType));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar la imagen: " + e.getMessage());
        }
    }
}
