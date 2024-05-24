package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class UserSecurityDTO {

  private Long userId;
  private String nickname;
  private String role;
  private Long avatarId;

  // getters

  public Long getUserId() {
    return userId;
  }

  public String getNickname() {
    return nickname;
  }

  public String getRole() {
    return role;
  }

  public Long getAvatarId() {
    return avatarId;
  }

  // setters

  public void setUserId(Long userId) {
    this.userId = userId;
  }
  
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
  
  public void setRole(String role) {
    this.role = role;
  }

  public void setAvatarId(Long avatarId) {
    this.avatarId = avatarId;
  }
}
