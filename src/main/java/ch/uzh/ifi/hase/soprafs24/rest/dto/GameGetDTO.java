package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.User;

public class GameGetDTO {

  private Long gameId;
  private String status;
  private Long admin;
  private Long gamePin;

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
}