package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

@Entity
@Table(name = "SESSION_HISTORY")
public class SessionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable =  false)
    private Long gameSessionId;

    @Column
    private String historyName;
    
    // Getters and Setters
    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setGameSessionId(Long gameSessionId) {
        this.gameSessionId = gameSessionId;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public String getHistoryName() {
        return historyName;
    }

    public void setHistoryName(String historyName) {
        this.historyName = historyName;
    }
}