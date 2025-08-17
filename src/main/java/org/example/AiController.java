package org.example;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final OllamaService ollama;

    public AiController(OllamaService ollama) {
        this.ollama = ollama;
    }

    /** Simple prompt -> full response */
    @PostMapping("/generate")
    public Mono<GenerateResponse> generate(@Valid @RequestBody GenerateRequest req) {
        return ollama.generate(req);
    }

    /** Token streaming via Server-Sent Events */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StreamChunk> stream(@Valid @RequestBody GenerateRequest req) {
        return ollama.stream(req).map(StreamChunk::new);
    }
}
