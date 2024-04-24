package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDateTime;

public class AvatarDTO {

  private Long avatarId;
  private Long creatorId;
  private String encodedImage;
  private LocalDateTime creationDateTime;

  public Long getAvatarId() {
    return avatarId;
  }

  public void setAvatarId(Long avatarId) {
    this.avatarId = avatarId;
  }

  public Long getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Long creatorId) {
    this.creatorId = creatorId;
  }

  public String getEncodedImage(){
    return encodedImage;
  }

  public void setEncodedImage(String encodedImage){
    this.encodedImage = encodedImage;
  }

  public LocalDateTime getCreationDateTime(){
    return creationDateTime;
  }

  public void setCreationDateTime(LocalDateTime creationDateTime){
    this.creationDateTime = creationDateTime;
  }
  
}