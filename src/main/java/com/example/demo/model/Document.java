package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalName;       // Nombre original del archivo

    @Column(nullable = false)
    private String storedName;         // Nombre UUID en disco

    @Column(nullable = false)
    private String contentType;        // "application/pdf", "image/jpeg", etc.

    private Long fileSize;             // Bytes

    private String description;        // Descripción opcional del documento

    @Column(nullable = true)
    private String userId;             // ID del usuario de Supabase Auth

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getStoredName() { return storedName; }
    public void setStoredName(String storedName) { this.storedName = storedName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
