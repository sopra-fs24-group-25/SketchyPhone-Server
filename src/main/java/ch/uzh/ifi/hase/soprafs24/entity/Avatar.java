package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "AVATAR")
public class Avatar implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long avatarId;

  @Lob
  @Column(nullable = false)
  private String encodedImage;

  @Column(nullable = false)
  private Long creatorId;

  @Column(nullable = false)
  private LocalDateTime creationDateTime;

   
  public Long getAvatarId(){
    return avatarId;
  }

  public void setAvatarId(Long avatarId){
    this.avatarId = avatarId;
  }
   
  public String getEncodedImage(){
    return encodedImage;
  }

  public void setEncodedImage(String encodedImage){
    this.encodedImage = encodedImage;
  }
   
  public Long getCreatorId(){
    return creatorId;
  }

  public void setCreatorId(Long creatorId){
    this.creatorId = creatorId;
  }
   
  public LocalDateTime getCreationDateTime(){
    return creationDateTime;
  }

  public void setCreationDateTime(LocalDateTime creationDateTime){
    this.creationDateTime = creationDateTime;
  }
}
