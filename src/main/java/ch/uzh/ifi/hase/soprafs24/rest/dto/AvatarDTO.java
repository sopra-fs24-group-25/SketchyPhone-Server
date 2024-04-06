package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDate;

public class AvatarDTO {

  private Long avatarId;
  private Long creatorId;
  private Byte encodedImage;
  private LocalDate creationDateTime;

  public Long getAvatarId() {
    return avatarId;
  }

  public void setGameId(Long avatarId) {
    this.avatarId = avatarId;
  }

  public Long getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Long creatorId) {
    this.creatorId = creatorId;
  }

  public Byte getEncodedImage(){
    return encodedImage;
  }

  public void setEncodedImage(Byte encodedImage){
    this.encodedImage = encodedImage;
  }

  public LocalDate getCreationDateTime(){
    return creationDateTime;
  }

  public void setCreationDateTime(LocalDate creationDateTime){
    this.creationDateTime = creationDateTime;
  }
  
}