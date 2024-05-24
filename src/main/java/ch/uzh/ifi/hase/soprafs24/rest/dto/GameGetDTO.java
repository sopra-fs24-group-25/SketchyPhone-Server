package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import ch.uzh.ifi.hase.soprafs24.entity.GameSession;

public class GameGetDTO {

  private Long gameId;
  private String status;
  private Long admin;
  private Long gamePin;
  @JsonProperty("gameSessions")
  private List<GameSession> gameSessions;
  private List<UserSecurityDTO> users;
  private Long gameSettingsId;

  public Long getGameId() {
    return gameId;
  }

  public void setGameId(Long gameId) {
    this.gameId = gameId;
  }

  public Long getGameSettingsId() {
    return gameSettingsId;
  }

  public void setGameSettingsId(Long gameSettingsId) {
    this.gameSettingsId = gameSettingsId;
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

  public List<UserSecurityDTO> getUsers() {
    return users;
  }

  public void setUsers(List<UserSecurityDTO> users){
    this.users = users;
  }

  public Long getAdmin() {
    return admin;
  }

  public void setAdmin(Long admin) {
    this.admin = admin;
  }

  // getters and setters for the new field
  public List<GameSession> getGameSessions() {
    return gameSessions;
  }

  public void setGameSessions(List<GameSession> gameSessions) {
      this.gameSessions = gameSessions;
  }
  
}