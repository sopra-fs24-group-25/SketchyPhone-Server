package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDateTime;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import ch.uzh.ifi.hase.soprafs24.entity.User;

public class DrawingDTO {

  private Long drawingId;
  private User creator;
  private String encodedImage;
  private LocalDateTime creationDateTime;
  private Long previousTextPromptId;
  private Long nextTextPromptId;
  private Long gameSessionId;
  private Long assignedTo;
  private int round;

  public Long getDrawingId() {
    return drawingId;
  }

  public void setDrawingId(Long drawingId) {
    this.drawingId = drawingId;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public int getRound(){
    return round;
  }

  public void setRound(int round){
      this.round = round;
  }

  public Long getPreviousTextPrompt(){
    return previousTextPromptId;
  }

  public void setPreviousTextPrompt(Long previousTextPrompt){
    this.previousTextPromptId = previousTextPrompt;
  }

  public String getEncodedImage(){
    return encodedImage;
  }

  public void setEncodedImage(String encodedImage){
    this.encodedImage = encodedImage;
  }

  public Long getNextTextPrompt(){
    return nextTextPromptId;
  }

  public void setNextTextPrompt(Long nextTextPrompt){
    this.nextTextPromptId = nextTextPrompt;
  }

  public Long getGameSessionId(){
    return gameSessionId;
  }

  public void setGameSessionId(Long gameSessionId){
    this.gameSessionId = gameSessionId;
  }

  public LocalDateTime getCreationDateTime(){
    return creationDateTime;
  }

  public void setCreationDateTime(LocalDateTime creationDateTime){
    this.creationDateTime = creationDateTime;
  }

  public void setAssignedTo(Long assignedTo){
    this.assignedTo = assignedTo;
  }
  
  public Long getAssignedTo(){
    return assignedTo;
  }
}