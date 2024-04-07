package ch.uzh.ifi.hase.soprafs24.rest.dto;

// successfully tested endpoint with Postman

public class UserPostDTO {

  private String name;
  private String password;
  private Boolean persistent;
  private String email;

  // setters
  public void setName(String name) {
    this.name = name;
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

  // getters

  public String getName() {
    return name;
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

  
}
