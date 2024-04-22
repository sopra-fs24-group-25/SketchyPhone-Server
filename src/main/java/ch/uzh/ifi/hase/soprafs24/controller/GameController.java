package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Drawing;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TextPromptDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.Game.GameService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import ch.uzh.ifi.hase.soprafs24.rest.dto.DrawingDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionDTO;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository; // Import the GameRepository class
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



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
  private final UserService userService;

  GameController(GameService gameService, UserService userService) {
    this.gameService = gameService;
    this.userService = userService;
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
  public Game joinRoom(@PathVariable Long submittedPin, @RequestBody UserPostDTO userPostDTO) {
      // Convert UserPostDTO to User entity
      User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

      return gameService.joinGame(submittedPin, userInput);
  }

  @DeleteMapping("/games/{gameRoomId}/leave/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void leaveRoom(@PathVariable Long gameRoomId, @PathVariable Long userId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id){
      userService.authenticateUser(token, userService.getUserById(id));
      gameService.leaveRoom(gameRoomId, userId);
  }

  // Get Mapping to get a list of all users in a game room - when testing with Postman, the body should be a JSON object with the key "gameId (The ID of the gameroom)" and 'token' as the value
  // tested with postman to get all the users from the game room, passed (200 OK)
  @GetMapping("/gameRooms/{gameRoomId}/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<User> getGameRoomUsers(@PathVariable Long gameRoomId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));


    return gameService.getGameRoomUsers(gameRoomId);
  }

  // Get Mapping to get the current settings
  @GetMapping("/gameRooms/{gameRoomId}/settings")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public GameSettings getGameSettings(@PathVariable Long gameRoomId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));


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

  // Post Mapping to start a game session
  @PostMapping("/games/{gameId}/start")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public Game createGameSession(@PathVariable Long gameId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    gameService.authenticateAdmin(token, userService.getUserById(id));
    return gameService.createGameSession(gameId);
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
  // for the very first text prompts -> insert 777 as previousDrawingId
  @PostMapping("/games/{gameSessionId}/prompts/{userId}/{previousDrawingId}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  // TODO make post return type to void to comply with REST specs
  public TextPrompt createTextPrompt(@PathVariable Long gameSessionId, @PathVariable Long userId, @PathVariable Long previousDrawingId, @RequestBody TextPromptDTO textPromptDTO, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));

    return gameService.createTextPrompt(gameSessionId, userId, previousDrawingId, textPromptDTO.getContent());
    }
  
  // Get Mapping to get the text prompt from the user, need to be then assigned to different partecipantes in the game
  @GetMapping("/games/{gameSessionId}/prompts/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public TextPrompt getTextPrompt(@PathVariable Long gameSessionId, @PathVariable Long userId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));

    return gameService.getTextPrompt(gameSessionId, userId);
    }



  // Post mapping to create drawing entity in the database and save the drawing from the user
  @PostMapping("/games/{gameSessionId}/drawings/{userId}/{previousTextPromptId}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  // TODO make post return type to void to comply with REST specs
  public Drawing createDrawing(@PathVariable Long gameSessionId, @PathVariable Long userId, @PathVariable Long previousTextPromptId, @RequestBody String drawingBase64, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));
    
    return gameService.createDrawing(gameSessionId, userId, previousTextPromptId, drawingBase64);
  }
  
  // Get mapping for assigning drawing to user in game session
  @GetMapping("/games/{gameSessionId}/drawings/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Drawing getDrawing(@PathVariable Long gameSessionId, @PathVariable Long userId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));

    return gameService.getDrawing(gameSessionId, userId);
  }

  // just for testing
  // TODO once everything looks fine -> remove
  @GetMapping("/drawings")
  public List<Drawing> getDrawings() {
      return gameService.getDrawings();
  }

  // mapping to start the next round -> increase roundCounter in current game session
  @PutMapping("games/{gameSessionId}/nextround")
  public void startNextRound(@PathVariable Long gameSessionId) {
      gameService.startNextRound(gameSessionId);
  }
  
  // TODO get mappings for text prompt and drawing in presentation at the end -> /games/{gameId}/next/text OR /games/{gameId}/next/drawing
  // with the current textPrompt/drawingId in the path to fetch from server
  @GetMapping("/games/{gameSessionId}/next/text/{previousDrawingId}")
  public TextPrompt getNextTextPrompt(@PathVariable Long gameSessionId, @PathVariable Long previousDrawingId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
      gameService.authenticateAdmin(token, userService.getUserById(id));
      
      return gameService.getNextTextPrompt(gameSessionId, previousDrawingId);
  }

  @GetMapping("/games/{gameSessionId}/next/drawing/{previousTextPromptId}")
  public Drawing getNextDrawing(@PathVariable Long gameSessionId, @PathVariable Long previousTextPromptId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
      gameService.authenticateAdmin(token, userService.getUserById(id));
      
      return gameService.getNextDrawing(gameSessionId, previousTextPromptId);
  }
  

  // TODO get mapping to get one of the first text prompts 
  // maybe add field in text prompts (and drawings) showing if it has already been presented -> prevent showing same flow twice
  @GetMapping("/games/{gameSessionId}/presentation")
  public TextPrompt getFirstTextPrompt(@PathVariable Long gameSessionId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
      gameService.authenticateAdmin(token, userService.getUserById(id));
      
      return gameService.getFirstTextPrompt(gameSessionId);
  }
  

  
}