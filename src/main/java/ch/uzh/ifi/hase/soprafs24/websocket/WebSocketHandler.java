package ch.uzh.ifi.hase.soprafs24.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.messaging.simp.SimpMessagingTemplate;


@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<WebSocketSession, String> sessionUserMap = new ConcurrentHashMap<>();
    @Autowired
    private ReconnectionManager reconnectionManager;

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            // Handle user disconnection
            if (!reconnectionManager.attemptReconnection(userId)) {
                // Permanent disconnection
                handlePermanentDisconnection(userId);
            }
        }
    }

    private String getUserIdFromSession(WebSocketSession session) {
        // Example method to extract user ID from session attributes
        // Replace with your actual logic to retrieve user ID
        return session.getAttributes().get("userId").toString();
    }

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private void handlePermanentDisconnection(String userId) {
        // Implement logic for permanent disconnection
        // Check if the number of remaining users is less than the minimum required
        if (sessionUserMap.size() < 3) {
            // If less than 3 users remaining, remove all users
            sessionUserMap.clear();
            // Notify clients to reset game state or take appropriate action
            messagingTemplate.convertAndSend("/topic/game/reset", "All users disconnected");
        }
    }

}
