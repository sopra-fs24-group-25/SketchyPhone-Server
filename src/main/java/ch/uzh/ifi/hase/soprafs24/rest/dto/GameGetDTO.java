package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionGetDTO;

public class GameGetDTO {

  private Long gameId;
  private String status;
  private Long admin;
  private Long gamePin;
  @JsonProperty("gameSessions")
  private List<GameSessionGetDTO> gameSessions;

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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getAdmin() {
    return admin;
  }

  public void setAdmin(Long admin) {
    this.admin = admin;
  }

  // getters and setters for the new field
  public List<GameSessionGetDTO> getGameSessions() {
    return gameSessions;
  }

  public void setGameSessions(List<GameSessionGetDTO> gameSessions) {
      this.gameSessions = gameSessions;
  }
  
}