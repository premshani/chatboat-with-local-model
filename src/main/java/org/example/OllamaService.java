package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OllamaService {

    private final WebClient client;
    private final String defaultModel;
    private final String systemPrompt;

    public OllamaService(
            WebClient ollamaWebClient,
            @Value("${ollama.model}") String defaultModel,
            @Value("${ollama.system-prompt}") String systemPrompt) {
        this.client = ollamaWebClient;
        this.defaultModel = defaultModel;
        this.systemPrompt = systemPrompt;
    }

    /** Non-streaming generate using /api/generate */
    public Mono<GenerateResponse> generate(GenerateRequest req) {
        String model = (req.getModel() != null && !req.getModel().isEmpty())
                ? req.getModel() : defaultModel;

        Map<String, Object> payload = Map.of(
                "model", model,
                "prompt", buildPrompt(req.getPrompt()),
                "stream", false
        );

        return client.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Map.class)
                .map(json -> new GenerateResponse((String) json.getOrDefault("response", "")));
    }

    /** Streaming tokens (SSE) using /api/generate with stream=true */
    public Flux<String> stream(GenerateRequest req) {
        String model = (req.getModel() != null && !req.getModel().isEmpty())
                ? req.getModel() : defaultModel;

        Map<String, Object> payload = Map.of(
                "model", model,
                "prompt", buildPrompt(req.getPrompt()),
                "stream", true
        );

        return client.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_NDJSON, MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToFlux(Map.class)
                .map(chunk -> (String) chunk.getOrDefault("response", ""));
    }

    private String buildPrompt(String user) {
        if (systemPrompt == null || systemPrompt.isBlank()) return user;
        return systemPrompt + "\n\nUser: " + user + "\nAssistant:";
    }
}
