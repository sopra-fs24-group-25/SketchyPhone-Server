package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.User;

public class GamePostDTO {

  private String name;

  private User admin;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public User getAdmin() {
    return admin;
  }

  public void setAdmin(User admin) {
    this.admin = admin;
  }
}