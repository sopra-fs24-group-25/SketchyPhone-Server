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

    // one game can have multiple game sessions
    @OneToMany(mappedBy = "game")
    private List<GameSession> gameSessions;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long gamePin;

    @Column(nullable = true)
    private LocalDate creationDate;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private GameStatus status; // State of the game: OPEN, IN-PLAY, CLOSED

    @OneToMany(mappedBy = "gameRoom")
    private List<User> users;
    
    @Column(nullable = false)
    private Long admin;

    public void setGameSessions(List<GameSession> gameSessions) {
        this.gameSessions = gameSessions;
    }

    public Long getGamePin() {
        return gamePin;
    }

    public void setGamePin(Long gamePin) {
        this.gamePin = gamePin;
    }

    public List<GameSession> getGameSessions() {
        return gameSessions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

}
