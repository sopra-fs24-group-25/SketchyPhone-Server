package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GameSettingsDTO {

  private Long gameSettingsId;
  private int gameSpeed;
  private int numCycles;
  private Boolean enableTextToSpeech;

  // getters

  public Long getGameSettingsId(){
    return gameSettingsId;
  }

  public Boolean getEnableTextToSpeech(){
    return enableTextToSpeech;
  }

  public int getGameSpeed(){
    return gameSpeed;
  }

  public int getNumCycles(){
    return numCycles;
  }

  // setters
  public void setGameSpeed(int gameSpeed){
    this.gameSpeed = gameSpeed;
  }

  public void setNumCycles(int numCycles){
    this.numCycles = numCycles;
  }

  public void setGameSettingsId(Long gameSettingsId){
    this.gameSettingsId = gameSettingsId;
  }

  public void setEnableTextToSpeech(Boolean enableTextToSpeech){
    this.enableTextToSpeech = enableTextToSpeech;
  }

}