package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class SessionHistoryDTO {
    
    private Long historyId;
    private Long gameSessionId;
    private Long userId;
    private Long textPromptId;
    private Long drawingId;

    // Getters
    public Long getHistoryId() {
        return historyId;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTextPromptId() {
        return textPromptId;
    }

    public Long getDrawingId() {
        return drawingId;
    }

    // Setters
    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public void setGameSessionId(Long gameSessionId) {
        this.gameSessionId = gameSessionId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTextPromptId(Long textPromptId) {
        this.textPromptId = textPromptId;
    }

    public void setDrawingId(Long drawingId) {
        this.drawingId = drawingId;
    }
}