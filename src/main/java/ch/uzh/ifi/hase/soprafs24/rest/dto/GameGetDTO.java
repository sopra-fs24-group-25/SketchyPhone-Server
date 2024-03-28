package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.User;

public class GameGetDTO {

  private Long id;
  private String status;
  private Long admin;
  private Long gamePin;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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