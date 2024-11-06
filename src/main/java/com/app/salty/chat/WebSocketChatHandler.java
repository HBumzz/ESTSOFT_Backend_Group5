package com.app.salty.chat;

import com.app.salty.chat.dto.ChatMessageDto;
import com.app.salty.chat.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;


/*
 * WebSocket Handler 작성
 * 소켓 통신은 서버와 클라이언트가 1:n으로 관계를 맺는다. 따라서 한 서버에 여러 클라이언트 접속 가능
 * 서버에는 여러 클라이언트가 발송한 메세지를 받아 처리해줄 핸들러가 필요
 * TextWebSocketHandler를 상속받아 핸들러 작성
 * 클라이언트로 받은 메세지를 log로 출력하고 클라이언트로 환영 메세지를 보내줌
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final Set<WebSocketSession> sessions = new HashSet<>();
    private final Map<Long, Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();

    // 타임아웃 스케줄러와 세션별 타임아웃 작업을 저장하는 맵
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<WebSocketSession, ScheduledFuture<?>> sessionTimeoutMap = new ConcurrentHashMap<>();

    // 2분 타임아웃 설정 (120초)
    private static final long TIMEOUT_DURATION = 120;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} 연결됨", session.getId());
        sessions.add(session);
        scheduleTimeoutTask(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);

        ChatMessageDto chatMessageDto = mapper.readValue(payload, ChatMessageDto.class);
        log.info("session {}", chatMessageDto.toString());

        Long chatRoomId = chatMessageDto.getChatRoomId();
        chatRoomSessionMap.computeIfAbsent(chatRoomId, k -> new HashSet<>()).add(session);

        // 새로운 메시지를 수신할 때마다 타임아웃 스케줄을 재설정
        resetTimeoutTask(session);

        sendMessageToChatRoom(chatMessageDto, chatRoomSessionMap.get(chatRoomId));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} 연결 끊김", session.getId());
        sessions.remove(session);
        cancelTimeoutTask(session);
    }

    private void scheduleTimeoutTask(WebSocketSession session) {
        // 일정 시간이 지나면 연결을 끊는 작업 예약
        ScheduledFuture<?> timeoutTask = scheduler.schedule(() -> {
            try {
                session.close(CloseStatus.NORMAL);
                log.info("{} 세션 타임아웃으로 연결 종료됨", session.getId());
            } catch (IOException e) {
                log.error("세션 종료 실패: {}", e.getMessage());
            }
        }, TIMEOUT_DURATION, TimeUnit.SECONDS);

        sessionTimeoutMap.put(session, timeoutTask);
    }

    private void resetTimeoutTask(WebSocketSession session) {
        // 타임아웃 작업을 취소하고 새 작업을 예약
        cancelTimeoutTask(session);
        scheduleTimeoutTask(session);
    }

    private void cancelTimeoutTask(WebSocketSession session) {
        ScheduledFuture<?> timeoutTask = sessionTimeoutMap.remove(session);
        if (timeoutTask != null) {
            timeoutTask.cancel(true);
        }
    }

    private void sendMessageToChatRoom(ChatMessageDto chatMessageDto, Set<WebSocketSession> chatRoomSession) {
        chatRoomSession.parallelStream().forEach(sess -> sendMessage(sess, chatMessageDto));
    }

    private <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}