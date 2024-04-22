package ch.uzh.ifi.hase.soprafs24.websocket;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ReconnectionManager {
    // Map to store user IDs and their disconnection timestamps
    private Map<String, Instant> disconnectedUsers = new ConcurrentHashMap<>();
    
    // Reconnection timeframe in milliseconds (e.g., 30 seconds)
    private static final long RECONNECTION_TIMEFRAME_MS = 30_000;

    public void handleDisconnection(String userId) {
        // Record the timestamp of the disconnection
        disconnectedUsers.put(userId, Instant.now());
    }

    public boolean attemptReconnection(String userId) {
        Instant disconnectionTime = disconnectedUsers.get(userId);
        if (disconnectionTime != null) {
            // Calculate the elapsed time since disconnection
            long elapsedTimeMs = Instant.now().toEpochMilli() - disconnectionTime.toEpochMilli();
            if (elapsedTimeMs <= RECONNECTION_TIMEFRAME_MS) {
                // Reconnection attempt is within the timeframe
                // Perform reconnection logic here, if needed
                // For example, you can update user session or perform validation
                disconnectedUsers.remove(userId); // Remove from disconnected users map
                return true;
            }
        }
        return false; // Reconnection attempt failed
    }
}