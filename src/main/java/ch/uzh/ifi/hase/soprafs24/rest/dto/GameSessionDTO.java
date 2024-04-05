package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
// gamesession 

public class GameSessionDTO {
    private GameStatus status;
    private String message;

    public GameSessionDTO() {
        // Empty constructor
    }

    public GameSessionDTO(GameStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters
    public GameStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    // Setters

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
}
