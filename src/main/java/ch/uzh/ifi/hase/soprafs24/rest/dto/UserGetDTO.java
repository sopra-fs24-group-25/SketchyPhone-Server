package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
// successfully tested endpoint with Postman
import ch.uzh.ifi.hase.soprafs24.entity.Avatar;

public class UserGetDTO {

  private Long id;
  private String name;
  private LocalDate creationDate;
  private UserStatus status;
  private Boolean persistent;
  private String email;
  private Avatar avatar;
  private String role;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDate getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public Boolean getPersistent() {
    return persistent;
  }

  public void setPersistent(Boolean persistent) {
    this.persistent = persistent;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Avatar getAvatar() {
    return avatar;
  }

  public void setAvatar(Avatar avatar) {
    this.avatar = avatar;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
