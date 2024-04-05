package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

import java.io.Serializable;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "GAME")
public class Game implements Serializable{
    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "game")
    private List<GameSession> gameSessions;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(unique = true, nullable = false)
    private Long gamePin;

    @Column(nullable = true)
    private LocalDate gameCreationDate;

    @Column(nullable = false)
    private String gameToken;

    @Column(nullable = false)
    private GameStatus status; // State of the game: OPEN, IN-PLAY, CLOSED

    @OneToMany()
    @JoinColumn(name = "GAME_ID", referencedColumnName = "gameId")
    private List<User> users;
    
    @Column(nullable = false)
    private Long admin;

    @Column(nullable = false)
    private Long gameSettingsId;


    public void setGameSessions(List<GameSession> gameSessions) {
        this.gameSessions = gameSessions;}
    
    public List<GameSession> getGameSessions() {
        return gameSessions;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getGamePin() {
        return gamePin;
    }

    public void setGamePin(Long gamePin) {
        this.gamePin = gamePin;
    }

    public LocalDate getGameCreationDate() {
        return gameCreationDate;
    }

    public void setGameCreationDate(LocalDate gameCreationDate) {
        this.gameCreationDate = gameCreationDate;

    }

    public String getGameToken() {
        return gameToken;
    }

    public void setGameToken(String gameToken) {
        this.gameToken = gameToken;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Long getAdmin() {
        return admin;
      }
    
    public void setAdmin(Long admin) {
    this.admin = admin;
    }

    public List<User> getUsers() {
    return users;
    }

    public void setUsers(List<User> users) {
    this.users = users;
    }

    public Long getGameSettingsId() {
    return gameSettingsId;
    }

    public void setGameSettingsId(Long gameSettingsId) {
    this.gameSettingsId = gameSettingsId;
    }

}
