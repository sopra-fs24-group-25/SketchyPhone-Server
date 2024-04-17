package ch.uzh.ifi.hase.soprafs24.entity;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "TextPrompt")
public class TextPrompt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long textPromptId; // Corrected variable name

    @Column(nullable = false)
    private String content;

    @ManyToOne()
    @JoinColumn(name = "userId")
    private User creator;

    @ManyToOne()
    @JoinColumn(name = "gameId")
    private GameSession gameSession;

    @Column
    private Long assignedTo;

    @Column
    private Long previousDrawingId;

    @Column
    private Long nextDrawingId;

    @Column
    private int round;


    public Long getTextPromptId() {
        return textPromptId;
    }

    public void setTextPromptId(Long textPromptId) {
        this.textPromptId = textPromptId;
    }

    public Long getPreviousDrawingId(){
        return previousDrawingId;
    }

    public void setPreviousDrawingId(Long previousDrawingId){
        this.previousDrawingId = previousDrawingId;
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

    public User getCreator() {
        return creator;
    }

    public int getRound(){
        return round;
    }

    public void setRound(int round){
        this.round = round;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }
}
