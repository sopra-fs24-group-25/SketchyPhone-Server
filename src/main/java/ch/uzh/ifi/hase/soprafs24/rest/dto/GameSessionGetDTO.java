package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import java.util.List;

public class GameSessionGetDTO {
    private GameStatus status;
    private Long gameSessionId;
    private String token;
    private List<Long> usersInSession;
    private int roundCounter;

    public GameSessionGetDTO() {
    }
    
    public GameSessionGetDTO(GameStatus status) {
        this.status = status;
    }

    // Getters
    public GameStatus getStatus() {
        return status;
    }

    public List<Long> getUsersInSession(){
        return usersInSession;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public int getRoundCounter(){
        return roundCounter;
    }

    // Setters
    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void setGameSessionId(Long gameSessionId) {
        this.gameSessionId = gameSessionId;
    }

    public void setUsersInSession(List<Long> usersInSession){
        this.usersInSession = usersInSession;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRoundCounter(int roundCounter){
        this.roundCounter = roundCounter;
    }

}
