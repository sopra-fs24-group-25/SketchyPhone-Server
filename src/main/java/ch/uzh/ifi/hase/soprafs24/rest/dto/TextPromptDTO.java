package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.User;

public class TextPromptDTO {
    private User creator;
    private GameSession gameSession;
    private String content;

    public TextPromptDTO() {}

    public TextPromptDTO(User creator, String content) {
        this.creator = creator;
        this.content = content;
    }

    // Getters and Setters
    public User getcreator() {
        return creator;
    }

    public void setcreator(User creator) {
        this.creator = creator;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
