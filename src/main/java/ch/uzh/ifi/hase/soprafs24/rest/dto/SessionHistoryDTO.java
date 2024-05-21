package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class SessionHistoryDTO {
    
    private Long historyId;
    private Long gameSessionId;
    private Long userId;
    private String historyName;

    // Getters
    public String getHistoryName() {
        return historyName;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public Long getUserId() {
        return userId;
    }

    // Setters
    public void setHistoryName(String historyName) {
        this.historyName = historyName;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public void setGameSessionId(Long gameSessionId) {
        this.gameSessionId = gameSessionId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}