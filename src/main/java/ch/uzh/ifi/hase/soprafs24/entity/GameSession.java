package ch.uzh.ifi.hase.soprafs24.entity;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "GameSession")
public class GameSession implements Serializable{
    private static final long serialVersionUID = 1L;

    // Game session extends the game entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gameSession")
    private List<TextPrompt> textPrompts;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameSessionId;

    @Column(nullable = true, unique = true)
    private LocalDate creationDate;

    @Column(nullable = false)
    private String token;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public void setGameSessionId(Long gameSessionId) {
        this.gameSessionId = gameSessionId;
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
}
