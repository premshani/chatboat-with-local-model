package org.example;

import jakarta.validation.constraints.NotBlank;

public class GenerateRequest {
    @NotBlank
    private String prompt;
    private String model;        // optional override
    private boolean stream;      // default false

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public boolean isStream() { return stream; }
    public void setStream(boolean stream) { this.stream = stream; }
}
