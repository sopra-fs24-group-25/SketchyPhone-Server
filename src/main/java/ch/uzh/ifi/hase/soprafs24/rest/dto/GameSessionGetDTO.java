package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

public class GameSessionGetDTO {
    private GameStatus status;
    private Long gameSessionId;
    private String token;

    public GameSessionGetDTO() {
    }
    
    public GameSessionGetDTO(GameStatus status) {
        this.status = status;
    }

    // Getters
    public GameStatus getStatus() {
        return status;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    // Setters
    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void setGameSessionId(Long gameSessionId) {
        this.gameSessionId = gameSessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
