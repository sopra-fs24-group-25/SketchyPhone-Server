package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GameSettingsPostDTO {

  private Long gameSettingsId;
  private int gameSpeed;
  private int numCycles;
  private Boolean enableTextToSpeech;


  public Long getGameSettingsId() {
    return gameSettingsId;
  }

  public void setGameSettingsid(Long gameSettingsid) {
    this.gameSettingsId = gameSettingsid;
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

  public void setNumCycles(int numCycles) {
    this.numCycles = numCycles;
  }

  public Boolean getEnableTextToSpeech() {
    return enableTextToSpeech;
  }

  public void setEnableTextToSpeech(Boolean enableTextToSpeech) {
    this.enableTextToSpeech = enableTextToSpeech;
  }
}