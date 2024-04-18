package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "DRAWING")
public class Drawing implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long drawingId;

  @Column(nullable = false)
  private String encodedImage;

  @Column(nullable = false)
  private Long creatorId;

  @Column
  private Long previousTextPromptId;

  @Column 
  private Long nextTextPromtId;

  @Column(nullable = false)
  private LocalDateTime creationDateTime;

  @Column
  private Long gameSessionId;

  @Column
  private Long assignedTo;

  @Column
  private int round;

  public int getRound(){
    return round;
  }

  public void setRound(int round){
    this.round = round;
  }

   
  public Long getDrawingId(){
    return drawingId;
  }

  public void setDrawingId(Long drawingId){
    this.drawingId = drawingId;
  }

  public Long getPreviousTextPrompt(){
    return previousTextPromptId;
  }

  public void setPreviousTextPrompt(Long previousTextPromptId){
    this.previousTextPromptId = previousTextPromptId;
  }
   
  public String getEncodedImage(){
    return encodedImage;
  }

  public void setEncodedImage(String encodedImage){
    this.encodedImage = encodedImage;
  }

  public Long getNextTextPrompt(){
    return nextTextPromtId;
  }

  public void setNextTextPrompt(Long nextTextPromptId){
    this.nextTextPromtId = nextTextPromptId;
  }
   
  public Long getCreatorId(){
    return creatorId;
  }

  public void setCreatorId(Long creatorId){
    this.creatorId = creatorId;
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

  public Long getAssignedTo(){
    return assignedTo;
  }

  public void setAssignedTo(Long assignedTo){
    this.assignedTo = assignedTo;
  }
}
