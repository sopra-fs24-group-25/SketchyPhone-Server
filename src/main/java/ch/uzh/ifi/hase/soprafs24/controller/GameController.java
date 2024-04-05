package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TextPromptDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionDTO;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository; // Import the GameRepository class
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

  // Post Mapping to create a game room - when testing with Postman, the body should be a JSON object with the key "username" and 'name' as the value
  @PostMapping("/gameRooms/create")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public Game createRoom(@RequestBody UserPostDTO userPostDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    Game newGame = gameService.createGame(userInput);
    return newGame;
  }

  // Post Mapping to join a game room - when testing with Postman, the body should be a JSON object with the key "username" and 'name' as the value
  // tested with postman for the exisiting user and for the new user(201)
  @PostMapping("/gameRooms/join/{submittedPin}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public ResponseEntity<GameSessionDTO> joinRoom(@PathVariable Long submittedPin, @RequestBody UserPostDTO userPostDTO) {
      // Convert UserPostDTO to User entity
      User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
      
      // pass both submittedPin and the User entity to the service method
      GameSessionDTO newGameSession = gameService.joinGame(submittedPin, userInput);
      return new ResponseEntity<>(newGameSession, HttpStatus.CREATED);
  }

  // Get Mapping to get a list of all users in a game room - when testing with Postman, the body should be a JSON object with the key "gameId (The ID of the gameroom)" and 'token' as the value
  // tested with postman to get all the users from the game room, passed (200 OK)
  @GetMapping("/gameRooms/{gameRoomId}/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<User> getGameRoomUsers(@PathVariable Long gameRoomId) {
    return gameService.getGameRoomUsers(gameRoomId);
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

  // Get Mapping to get a list of all game sessions
  @GetMapping("/games/{gameId}/sessions")
  public ResponseEntity<List<GameSessionDTO>> listGameSessions(@PathVariable Long gameId) {
    List<GameSession> sessions = gameService.getGameSessionsByGameId(gameId);
    List<GameSessionDTO> sessionDTOs = sessions.stream()
                                               .map(DTOMapper.INSTANCE::gameSessionToGameSessionDTO)
                                               .collect(Collectors.toList());
    return ResponseEntity.ok(sessionDTOs);
  }

  // Post Mapping to get the text prompt from the user with the text prompt
  // tested with postman to create a text prompt, passed (201 Created)
  @PostMapping("/gameRooms/{gameSessionId}/{userId}/textPrompt")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public TextPromptDTO createTextPrompt(@PathVariable Long gameSessionId, @PathVariable Long userId, @RequestBody TextPromptDTO textPromptDTO) {
    TextPrompt newTextPrompt = gameService.createTextPrompt(gameSessionId, userId, textPromptDTO.getContent());
    return DTOMapper.INSTANCE.convertEntityToTextPromptDTO(newTextPrompt);
    }

}