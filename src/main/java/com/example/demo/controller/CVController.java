package com.example.demo.controller;

import com.example.demo.model.CVProfile;
import com.example.demo.repository.CVProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/cv")
public class CVController {

    @Autowired
    private CVProfileRepository repository;

    @GetMapping("/{id}")
    public CVProfile getCV(@PathVariable Long id) {
        Optional<CVProfile> profile = repository.findById(id);
        return profile.orElse(new CVProfile());
    }

    @PostMapping
    public CVProfile saveCV(@RequestBody CVProfile cvProfile) {
        return repository.save(cvProfile);
    }
    
    @GetMapping
    public Iterable<CVProfile> getAll() {
        return repository.findAll();
    }
}
