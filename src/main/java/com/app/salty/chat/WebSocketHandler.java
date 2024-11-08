package com.app.salty.chat;

import com.app.salty.chat.dto.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;
    private final Map<Long, Set<WebSocketSession>> chatRoomSessionMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<WebSocketSession, ScheduledFuture<?>> sessionTimeoutMap = new ConcurrentHashMap<>();
    private static final long TIMEOUT_DURATION = 120;

    // 소켓 세션을 저장할 Set
    private final Set<WebSocketSession> sessions = new HashSet<>();
    private void scheduleTimeoutTask(WebSocketSession session) {
        ScheduledFuture<?> timeoutTask = scheduler.schedule(() -> {
            try {
                if (session.isOpen()) {
                    session.close(CloseStatus.NORMAL);
                    log.info("세션 타임아웃: {}", session.getId());
                }
            } catch (IOException e) {
                log.error("세션 종료 오류:", e);
            }
        }, TIMEOUT_DURATION, TimeUnit.SECONDS);

        sessionTimeoutMap.put(session, timeoutTask);
    }

    private void resetTimeoutTask(WebSocketSession session) {
        cancelTimeoutTask(session);
        scheduleTimeoutTask(session);
    }

    private void cancelTimeoutTask(WebSocketSession session) {
        ScheduledFuture<?> timeoutTask = sessionTimeoutMap.remove(session);
        if (timeoutTask != null) {
            timeoutTask.cancel(true);
        }
    }
    // 소켓 연결 확인
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket 연결됨: {}", session.getId());
        try {
            session.sendMessage(new TextMessage("{\"type\": \"info\", \"message\": \"WebSocket 연결 완료\"}"));
        } catch (IOException e) {
            log.error("WebSocket 연결 메시지 전송 오류:", e);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            ChatMessageDto chatMessageDto = mapper.readValue(payload, ChatMessageDto.class);
            Long chatRoomId = chatMessageDto.getChatRoomId();

            // 방에 세션 추가
            chatRoomSessionMap.computeIfAbsent(chatRoomId, k -> new CopyOnWriteArraySet<>()).add(session);

            log.info("받은 메시지: {}, ChatRoom ID: {}, Session ID: {}", chatMessageDto.getMessage(), chatRoomId, session.getId());
            resetTimeoutTask(session);

            // 메시지 전송
            sendMessageToChatRoom(chatMessageDto, chatRoomSessionMap.get(chatMessageDto.getChatRoomId()));
        } catch (Exception e) {
            log.error("메시지 처리 오류:", e);
        }
    }

    private void sendMessageToChatRoom(ChatMessageDto chatMessageDto, Set<WebSocketSession> chatRoomSession) {
        log.info("ChatRoom ID: {}, 메시지 전송 중, 세션 수: {}", chatMessageDto.getChatRoomId(), chatRoomSession.size());
        for (WebSocketSession sess : chatRoomSession) {
            if (sess.isOpen()) {
                log.info("세션 {}에 메시지 전송 시도", sess.getId());
                sendMessage(sess, chatMessageDto);  // sendMessage 함수를 통해 클라이언트에 메시지 전송
                log.info("메시지 전송 완료: {}, Session ID: {}", chatMessageDto.getCreatedAt(), sess.getId());
            } else {
                chatRoomSession.remove(sess);
                log.info("닫힌 세션 제거됨: {}", sess.getId());
            }
        }
    }

    private <T> void sendMessage(WebSocketSession session, T message) {
        try {
            // 메시지를 JSON으로 변환 후 전송
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
            log.info("세션 {}에 메시지 전송 완료", session.getId());
        } catch (IOException e) {
            log.error("메시지 전송 오류:", e);
        }
    }

    // 소켓 연결 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} 연결 끊김", session.getId());

        // 모든 채팅방에서 해당 세션 제거
        chatRoomSessionMap.values().forEach(sessions -> sessions.remove(session));
        sessions.remove(session);
    }

}