package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TextPromptDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class GameController {

  private final GameService gameService;

  GameController(GameService gameService) {
    this.gameService = gameService;
  }

  // Post Mapping to create a game room
  @PostMapping("/gameRooms/create")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public Game createRoom(@RequestBody UserPostDTO userPostDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    Game newGame = gameService.createGame(userInput);
    return newGame;
  }

  // Get Mapping to get a list of all users in a game room
  @GetMapping("/gameRooms/{gameRoomId}/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<User> getGameRoomUsers(@PathVariable Long gameRoomId) {
    
    return gameService.getGameRoomUsers(gameRoomId);
  }

  // Post Mapping to get the text prompt from the user 
  @PostMapping("/gameRooms/{gameSessionId}/{userId}/textPrompt")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public TextPromptDTO createTextPrompt(@PathVariable Long gameSessionId, @PathVariable Long userId, @RequestBody TextPromptDTO textPromptDTO) {
    TextPrompt newTextPrompt = gameService.createTextPrompt(gameSessionId, userId, textPromptDTO.getContent());
    return DTOMapper.INSTANCE.convertEntityToTextPromptDTO(newTextPrompt);
    }

    // Get Mapping to get the current settings
  @GetMapping("/gameRooms/{gameRoomId}/settings")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public GameSettings getGameSettings(@PathVariable Long gameRoomId) {

    return gameService.getGameSettings(gameRoomId);
  }

  // Put Mapping to update current settings
  @PutMapping("/gameRooms/{gameRoomId}/settings")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Game updateGameSettings(@PathVariable Long gameRoomId, @RequestBody GameSettingsDTO gameSettingsDTO) { 
    GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOtoEntity(gameSettingsDTO);

    return gameService.updateGameSettings(gameRoomId, gameSettings);
  }

}