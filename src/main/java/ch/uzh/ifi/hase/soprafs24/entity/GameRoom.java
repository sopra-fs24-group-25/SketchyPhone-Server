package ch.uzh.ifi.hase.soprafs24.entity;

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
@Table(name = "GAMEROOM")
public class GameRoom implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = true)
  private String name;

  @Column(nullable = true, unique = true)
  private LocalDate creationDate;

  @OneToMany(mappedBy = "gameRoom")
  private List<User> users;

  @Column(nullable = true)
  private String link;

  @Column(nullable = true)
  private String token;

  @Column(nullable = true)
  private String status;

  @Column(nullable = true)
  private Long admin;

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

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getAdmin() {
    return admin;
  }

  // code
  public void setAdmin(Long admin) {
    this.admin = admin;
  }

}

