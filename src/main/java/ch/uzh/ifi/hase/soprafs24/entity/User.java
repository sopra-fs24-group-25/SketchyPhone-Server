package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

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
@Table(name = "USER")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @OneToMany(mappedBy = "creator")
  private List<TextPrompt> textPrompts;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private LocalDate creationDate;

  @Column(nullable = false)
  private UserStatus status = UserStatus.OFFLINE; // Default value

  @Column(nullable = true)
  private Boolean persistent;

  @Column(nullable = true)
  private String email;

  @Column(nullable = true)
  private String password;

  @Column(nullable = true)
  private Long avatarId;

  @Column
  private String role;

  @Column(nullable = false, unique = true)
  private String token;

  @ManyToOne(fetch = FetchType.EAGER)
  private Game gameRoom;

  // getters and setters

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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Long getAvatarId() {
    return avatarId;
  }

  public void setAvatarId(Long avatarId) {
    this.avatarId = avatarId;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

}
