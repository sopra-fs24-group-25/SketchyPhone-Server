package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
// successfully tested endpoint with Postman
import ch.uzh.ifi.hase.soprafs24.entity.Avatar;

public class UserGetDTO {

  private Long id;
  private String nickname;
  private LocalDate creationDate;
  private UserStatus status;
  private Boolean persistent;
  private String email;
  private Long avatarId;
  private String role;

  // getters

  public Long getId() {
    return id;
  }

  public String getNickname() {
    return nickname;
  }

  public LocalDate getCreationDate() {
    return creationDate;
  }

  public UserStatus getStatus() {
    return status;
  }

  public Boolean getPersistent() {
    return persistent;
  }

  public String getEmail() {
    return email;
  }

  public Long getAvatarId() {
    return avatarId;
  }

  public String getRole() {
    return role;
  }

  // setters

  public void setId(Long id) {
    this.id = id;
  }
  
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public void setPersistent(Boolean persistent) {
    this.persistent = persistent;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setAvatar(Long avatarId) {
    this.avatarId = avatarId;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
