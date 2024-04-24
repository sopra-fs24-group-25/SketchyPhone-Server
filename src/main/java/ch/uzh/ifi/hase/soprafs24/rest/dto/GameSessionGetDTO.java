package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameLoopStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import java.util.List;

public class GameSessionGetDTO {
    private GameStatus status;
    private Long gameSessionId;
    private String token;
    private List<Long> usersInSession;
    private int roundCounter;
    private GameLoopStatus gameLoopStatus;

    public GameSessionGetDTO() {
    }
    
    public GameSessionGetDTO(GameStatus status) {
        this.status = status;
    }

    // Getters
    public GameStatus getStatus() {
        return status;
    }

    public GameLoopStatus getGameLoopStatus() {
        return gameLoopStatus;
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

    public void setGameLoopStatus(GameLoopStatus gameLoopStatus) {
        this.gameLoopStatus = gameLoopStatus;
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
