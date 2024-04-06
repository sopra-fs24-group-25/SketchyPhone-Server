package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

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

  @Column
  private Byte encodedImage;

  @Column
  private Long creatorId;

  @Column
  private LocalDate creationDateTime;

   
  public Long getAvatarId(){
    return avatarId;
  }

  public void setAvatarId(Long avatarId){
    this.avatarId = avatarId;
  }
   
  public Byte getEncodedImage(){
    return encodedImage;
  }

  public void setEncodedImage(Byte encodedImage){
    this.encodedImage = encodedImage;
  }
   
  public Long getCreatorId(){
    return creatorId;
  }

  public void setCreatorId(Long creatorId){
    this.creatorId = creatorId;
  }
   
  public LocalDate getCreationDateTime(){
    return creationDateTime;
  }

  public void setCreationDateTime(LocalDate creationDateTime){
    this.creationDateTime = creationDateTime;
  }
}
