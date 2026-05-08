package com.example.demo.controller;

import java.util.List;
import java.util.Map;

public class CVRequest {
    private String prompt;
    private Map<String, Object> currentState;
    private List<Map<String, String>> history;

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public Map<String, Object> getCurrentState() { return currentState; }
    public void setCurrentState(Map<String, Object> currentState) { this.currentState = currentState; }

    public List<Map<String, String>> getHistory() { return history; }
    public void setHistory(List<Map<String, String>> history) { this.history = history; }
}
