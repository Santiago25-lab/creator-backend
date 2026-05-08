package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class CVProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition="TEXT")
    private String cvData;

    public CVProfile() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCvData() {
        return cvData;
    }

    public void setCvData(String cvData) {
        this.cvData = cvData;
    }
}
