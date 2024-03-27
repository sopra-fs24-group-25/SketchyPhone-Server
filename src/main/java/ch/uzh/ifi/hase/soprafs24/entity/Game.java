package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

import java.io.Serializable;

@Entity
@Table(name = "GAME")
public class Game implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long gamePin;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private GameStatus status; // State of the game: OPEN, IN-PLAY, CLOSED

    @Column(nullable = false)
    private User admin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public User getAdmin() {
        return admin;
      }
    
    public void setAdmin(User admin) {
    this.admin = admin;
    }

}
