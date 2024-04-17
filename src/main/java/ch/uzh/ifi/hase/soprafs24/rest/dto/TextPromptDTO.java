package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.User;

public class TextPromptDTO {
    private User creator;
    private GameSession gameSession;
    private String content;
    private Long assignedTo;
    private Long previousDrawingId;
    private Long nextDrawingId;
    private int round;


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

    public Long getPreviousDrawingId(){
        return previousDrawingId;
    }

    public void setPreviousDrawingId(Long previousDrawingId){
        this.previousDrawingId = previousDrawingId;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public Long getNextDrawingId(){
        return nextDrawingId;
    }

    public void setNextDrawingId(Long nextDrawingId){
        this.nextDrawingId = nextDrawingId;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public int getRound(){
        return round;
    }

    public void setRound(int round){
        this.round = round;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }
}
