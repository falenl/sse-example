package com.example.sse;


import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Data
@Builder
public class SSEProgress {
    private final SseEmitter emitter;
    private int percentage;
}
