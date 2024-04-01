package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class TextPromptDTO {
    private Long userId;
    private Long gameSessionId;
    private String content;

    public TextPromptDTO() {}

    public TextPromptDTO(Long userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public void setGameSessionId(Long gameSessionId) {
        this.gameSessionId = gameSessionId;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
