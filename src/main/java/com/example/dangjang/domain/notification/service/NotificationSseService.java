package com.example.dangjang.domain.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class NotificationSseService {

    private final ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>> subscribers = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(0L);

        subscribers.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        Runnable detach = () -> detach(userId, emitter);
        emitter.onCompletion(detach);
        emitter.onTimeout(detach);
        emitter.onError(e -> detach.run());

        try {
            emitter.send(SseEmitter.event().comment("ok"));
        } catch (IOException e) {
            detach.run();
            emitter.completeWithError(e);
            throw new IllegalStateException("SSE 초기 전송 실패", e);
        }

        return emitter;
    }

    private void detach(Long userId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> list = subscribers.get(userId);
        if (list == null) {
            return;
        }
        list.remove(emitter);
        if (list.isEmpty()) {
            subscribers.remove(userId, list);
        }
    }

    public void sendToUser(Long userId, String eventName, String data) {
        List<SseEmitter> list = subscribers.get(userId);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException ex) {
                log.debug("SSE send failed, closing emitter userId={}", userId, ex);
                emitter.completeWithError(ex);
            }
        }
    }
}
