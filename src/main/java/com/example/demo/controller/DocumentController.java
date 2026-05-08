package com.example.demo.controller;

import com.example.demo.model.Document;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.service.AIService;
import com.example.demo.service.PromptBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;
import java.util.Base64;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private AIService aiService;

    @Autowired
    private PromptBuilder promptBuilder;

    private static final Path UPLOAD_DIR = Paths.get("uploads/documents");

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "application/pdf",
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/webp"
    );

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Tipo de archivo no permitido. Solo PDF, JPG y PNG."));
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "El archivo supera el límite de 10MB."));
        }

        try {
            Files.createDirectories(UPLOAD_DIR);

            String originalName = sanitizeFilename(file.getOriginalFilename());
            String extension = getExtension(originalName);
            String storedName = UUID.randomUUID().toString() + "." + extension;
            Path targetPath = UPLOAD_DIR.resolve(storedName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            Document doc = new Document();
            doc.setOriginalName(originalName);
            doc.setStoredName(storedName);
            doc.setContentType(contentType);
            doc.setFileSize(file.getSize());
            doc.setDescription(description);
            Document saved = documentRepository.save(doc);

            return ResponseEntity.ok(toMap(saved));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al guardar el archivo: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listDocuments() {
        List<Document> docs = documentRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Document d : docs) result.add(toMap(d));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/view")
    public ResponseEntity<Resource> viewDocument(@PathVariable Long id) {
        Optional<Document> opt = documentRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Document doc = opt.get();
        Path filePath = UPLOAD_DIR.resolve(doc.getStoredName());

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) return ResponseEntity.notFound().build();

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + doc.getOriginalName() + "\"")
                .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<?> analyzeDocument(@PathVariable Long id) {
        Optional<Document> opt = documentRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Document doc = opt.get();
        Path filePath = UPLOAD_DIR.resolve(doc.getStoredName());

        try {
            byte[] fileBytes = Files.readAllBytes(filePath);
            String promptText = promptBuilder.buildDocumentAnalysisPrompt(doc.getOriginalName(), doc.getDescription());

            String result;
            if (doc.getContentType().startsWith("image/")) {
                String base64 = Base64.getEncoder().encodeToString(fileBytes);
                result = aiService.callOpenRouter(promptText, base64, doc.getContentType());
            } else if (doc.getContentType().equals("application/pdf")) {
                String pdfText;
                try (PDDocument pdf = Loader.loadPDF(filePath.toFile())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    pdfText = stripper.getText(pdf);
                }
                if (pdfText == null || pdfText.isBlank()) {
                    return ResponseEntity.ok(Map.of("error", "No se pudo extraer texto del PDF."));
                }
                result = aiService.callOpenRouter(promptText + "\n\n=== CONTENIDO DEL DOCUMENTO ===\n" + pdfText);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Tipo de archivo no soportado."));
            }
            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            if ("RATE_LIMIT_EXCEEDED".equals(e.getMessage())) {
                return ResponseEntity.ok(Map.of(
                    "documentType", "No disponible",
                    "summary", "El servicio de IA está temporalmente saturado. Espera unos segundos e intenta de nuevo.",
                    "extractedData", Map.of("personalInfo", Map.of(), "experience", List.of(), "education", List.of(), "skills", List.of(), "languages", List.of())
                ));
            }
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error leyendo el archivo: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        Optional<Document> opt = documentRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Document doc = opt.get();
        Path filePath = UPLOAD_DIR.resolve(doc.getStoredName());
        try { Files.deleteIfExists(filePath); } catch (IOException e) {}

        documentRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Documento eliminado."));
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "bin";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private String sanitizeFilename(String filename) {
        if (filename == null) return "document";
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private Map<String, Object> toMap(Document doc) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", doc.getId());
        m.put("originalName", doc.getOriginalName());
        m.put("contentType", doc.getContentType());
        m.put("fileSize", doc.getFileSize());
        m.put("description", doc.getDescription());
        m.put("uploadedAt", doc.getUploadedAt() != null ? doc.getUploadedAt().toString() : null);
        
        String viewUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/documents/")
                .path(doc.getId().toString())
                .path("/view")
                .toUriString();
        
        m.put("viewUrl", viewUrl);
        return m;
    }
}
