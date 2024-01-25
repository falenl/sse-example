package com.example.sse;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.random.RandomGenerator;

@Service
public class SSEService {

    private final List<SSEProgress> emitters = new CopyOnWriteArrayList<>();

    private final RandomGenerator random = RandomGenerator.of("L128X256MixRandom");

    public void addEmitter(SseEmitter emitter) {
        var progress = SSEProgress
                .builder()
                .emitter(emitter)
                .build();
        emitters.add(progress);
        emitter.onCompletion(() -> emitters.remove(progress));
        emitter.onTimeout(() -> emitters.remove(progress));
    }

    @Scheduled(fixedRate = 3000)
    public void sendEvents() {
        for (SSEProgress progressList : emitters) {
            SseEmitter emitter = progressList.getEmitter();
            progressList.setPercentage(this.getProgress(progressList));
            try {
                emitter.send(progressList.getPercentage());
                if (progressList.getPercentage() >= 100) {
                    this.completeEmitter(progressList);
                }
            } catch (IOException e) {
                this.completeEmitter(progressList);
            }
        }
    }

    private void completeEmitter(SSEProgress progressList) {
        progressList.getEmitter().complete();
        emitters.remove(progressList);
    }

    private int getProgress(SSEProgress progressList) {
        int percentage = progressList.getPercentage();
        var number = random.nextInt(25);
        percentage += number;
        return Math.min(percentage, 100);
    }
}
