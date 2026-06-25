package com.kaatha.notification.controller;

import com.kaatha.notification.dto.response.NotificationResponse;
import com.kaatha.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class RealtimeNotificationController {

    private static final List<SseEmitter> EMITTERS = new CopyOnWriteArrayList<>();

    private final NotificationService notificationService;

    @GetMapping(value = "/stream/{phone}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable String phone) {
        SseEmitter emitter = new SseEmitter(0L);
        EMITTERS.add(emitter);

        emitter.onCompletion(() -> EMITTERS.remove(emitter));
        emitter.onTimeout(() -> EMITTERS.remove(emitter));

        try {
            List<NotificationResponse> existing = notificationService.getNotificationsByPhone(phone);
            emitter.send(SseEmitter.event().name("init").data(existing));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public static void broadcast(String phone, Object payload) {
        EMITTERS.removeIf(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("notification").data(payload));
                return false;
            } catch (IOException e) {
                return true;
            }
        });
    }
}
