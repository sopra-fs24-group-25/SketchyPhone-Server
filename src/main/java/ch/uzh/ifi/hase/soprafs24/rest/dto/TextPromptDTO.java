package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.User;

public class TextPromptDTO {
    private UserSecurityDTO creator;
    private GameSession gameSession;
    private String content;
    private Long assignedTo;
    private Long previousDrawingId;
    private Long nextDrawingId;
    private Long textPromptId;
    private int round;
    private int numVotes;


    public TextPromptDTO() {}

    // public TextPromptDTO(User creator, String content) {
    //     this.creator = creator;
    //     this.content = content;
    // }

    // Getters and Setters
    public UserSecurityDTO getCreator() {
        return creator;
    }

    public void setCreator(UserSecurityDTO creator) {
        this.creator = creator;
    }

    public Long getPreviousDrawingId(){
        return previousDrawingId;
    }

    public void setPreviousDrawingId(Long previousDrawingId){
        this.previousDrawingId = previousDrawingId;
    }

    public int getNumVotes(){
        return numVotes;
    }

    public void setNumVotes(int numVotes){
        this.numVotes = numVotes;
    }

    public Long getTextPromptId(){
        return textPromptId;
    }

    public void setTextPromptId(Long textPromptId){
        this.textPromptId = textPromptId;
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
