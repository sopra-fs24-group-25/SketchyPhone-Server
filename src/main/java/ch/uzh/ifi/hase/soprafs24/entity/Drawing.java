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

  @Lob
  @Column(nullable = false)
  private String encodedImage;

  @ManyToOne()
  @JoinColumn(name = "userId")
  private User creator;

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

  @Column
  private int numVotes = 0;

  public int getRound(){
    return round;
  }

  public void setRound(int round){
    this.round = round;
  }

  public int getNumVotes(){
    return numVotes;
  }

  public void setNumVotes(int numVotes){
    this.numVotes = numVotes;
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
   
  public User getCreator(){
    return creator;
  }

  public void setCreator(User creator){
    this.creator = creator;
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
