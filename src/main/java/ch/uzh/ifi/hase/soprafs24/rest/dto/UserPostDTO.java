package ch.uzh.ifi.hase.soprafs24.rest.dto;

// successfully tested endpoint with Postman

public class UserPostDTO {

  private String nickname;
  private String password;
  private Boolean persistent;
  private String email;
  private Long avatarId;
  private String username;

  // setters
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public void setPassword(String password){
    this.password = password;
  }

  public void setPersistent(Boolean persistent){
    this.persistent = persistent;
  }

  public void setEmail(String email){
    this.email = email;
  }

  public void setAvatarId(Long avatarId){
    this.avatarId = avatarId;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  // getters

  public String getNickname() {
    return nickname;
  }

  public String getPassword() {
    return password;
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

  public String getUsername() {
    return username;
  }
  
}
