package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.stereotype.Controller; 
import org.springframework.messaging.handler.annotation.MessageMapping; 
import org.springframework.messaging.handler.annotation.Payload;
import java.util.HashMap;
import java.util.Map;
import org.springframework.messaging.handler.annotation.SendTo;



@Controller
public class WebSocketController {
    // Map to store WebSocket sessions and associated user IDs
    private final Map<String, String> sessionUserMap = new HashMap<>();

    @MessageMapping("/user/join")
    @SendTo("/topic/users")
    public Map<String, String> addUser(@Payload String userId) {
        // Add user to session map
        sessionUserMap.put(userId, userId);
        // Return updated user list to be broadcasted to all clients
        return sessionUserMap;
    }


}