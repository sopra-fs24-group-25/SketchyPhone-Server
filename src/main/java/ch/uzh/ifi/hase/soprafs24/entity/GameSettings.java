package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;

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
@Table(name = "GAMESETTINGS")
public class GameSettings implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long gameSettingsId;

  @Column(nullable = false)
  private int gameSpeed;

  @Column(nullable = false)
  private int numCycles;

  @Column(nullable = false)
  private Boolean enableTextToSpeech;

  @OneToOne
  @JoinColumn(name = "game_room_id")
  private Game gameRoom;

  public Long getGameSettingsId() {
    return gameSettingsId;
  }

  public void setGameSettingsId(Long gameSettingsId) {
    this.gameSettingsId = gameSettingsId;
  }

  public int getGameSpeed() {
    return gameSpeed;
  }

  public void setGameSpeed(int gameSpeed) {
    this.gameSpeed = gameSpeed;
  }

  public int getNumCycles() {
    return numCycles;
  }

  public void setUsername(int numCycles) {
    this.numCycles = numCycles;
  }

  public Boolean getEnableTextToSpeech() {
    return enableTextToSpeech;
  }

  public void setToken(Boolean enableTextToSpeech) {
    this.enableTextToSpeech = enableTextToSpeech;
  }
}