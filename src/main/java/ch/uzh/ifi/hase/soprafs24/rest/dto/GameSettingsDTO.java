package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GameSettingsDTO {

  private Long gameSettingsId;
  private int gameSpeed;
  private int numCycles;
  private Boolean enableTextToSpeech;

  public Long getGameSettingsId(){
    return gameSettingsId;
  }

  public void setGameSettingsId(Long gameSettingsId){
    this.gameSettingsId = gameSettingsId;
  }

  public int getGameSpeed(){
    return gameSpeed;
  }

  public void setGameSpeed(int gameSpeed){
    this.gameSpeed = gameSpeed;
  }

  public int getNumCycles(){
    return numCycles;
  }

  public void setNumCycles(int numCycles){
    this.numCycles = numCycles;
  }

  public Boolean getEnableTextToSpeech(){
    return enableTextToSpeech;
  }

  public void setEnableTextToSpeech(Boolean enableTextToSpeech){
    this.enableTextToSpeech = enableTextToSpeech;
  }

}