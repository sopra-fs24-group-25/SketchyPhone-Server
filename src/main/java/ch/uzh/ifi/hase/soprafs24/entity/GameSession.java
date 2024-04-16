package ch.uzh.ifi.hase.soprafs24.entity;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.time.LocalDate;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "GameSession")
public class GameSession implements Serializable{
    private static final long serialVersionUID = 1L;

    // Game session extends the game entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonBackReference
    private Game game;

    @OneToMany(mappedBy = "gameSession")
    private List<TextPrompt> textPrompts;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameSessionId;

    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private GameStatus status;  

    @ElementCollection
    private List<Long> usersInSession = new ArrayList<>();

    @Column
    private int roundCounter = 1;

    public int getRoundCounter(){
        return roundCounter;
    }

    public void setRoundCounter(int roundCounter){
        this.roundCounter = roundCounter;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public void setGameSessionId(Long id) {
        this.gameSessionId = id;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public List<Long> getUsersInSession(){
        return usersInSession;
    }

    public void setUsersInSession(List<Long> usersInSession){
        this.usersInSession = usersInSession;
    }
}
