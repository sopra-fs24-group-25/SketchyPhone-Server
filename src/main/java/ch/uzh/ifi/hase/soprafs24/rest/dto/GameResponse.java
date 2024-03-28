package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

public class GameResponse {
    private GameStatus status;
    private String message;

    public GameResponse(GameStatus status, String message) {
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
    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "GameSessionResponse{" +
               "status='" + status + '\'' +
               ", message='" + message + '\'' +
               '}';
    }
}
