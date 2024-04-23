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
import javassist.runtime.DotClass;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import ch.uzh.ifi.hase.soprafs24.rest.dto.DrawingDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
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
  public GameGetDTO createRoom(@RequestBody UserPostDTO userPostDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    Game newGame = gameService.createGame(userInput);
    GameGetDTO gameDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(newGame);
    return gameDTO;
  }

  // Post Mapping to join a game room - when testing with Postman, the body should be a JSON object with the key "username" and 'name' as the value
  // tested with postman for the exisiting user and for the new user(201)
  @PostMapping("/gameRooms/join/{submittedPin}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public GameGetDTO joinRoom(@PathVariable Long submittedPin, @RequestBody UserPostDTO userPostDTO) {
      // Convert UserPostDTO to User entity
      User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

      Game game = gameService.joinGame(submittedPin, userInput);

      GameGetDTO gameDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

      return gameDTO;
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
  public GameSettingsDTO getGameSettings(@PathVariable Long gameRoomId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));

    GameSettingsDTO gameSettingsDTO = DTOMapper.INSTANCE.convertEntityToGameSettingsDTO(gameService.getGameSettings(gameRoomId));
    return gameSettingsDTO;
  }

  // Put Mapping to update current settings
  @PutMapping("/gameRooms/{gameRoomId}/settings")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public GameGetDTO updateGameSettings(@PathVariable Long gameRoomId, @RequestBody GameSettingsDTO gameSettingsDTO) { 
    GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOtoEntity(gameSettingsDTO);

    GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(gameService.updateGameSettings(gameRoomId, gameSettings));
    return gameGetDTO;
  }

  // Post Mapping to start a game session
  @PostMapping("/games/{gameId}/start")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public GameGetDTO createGameSession(@PathVariable Long gameId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    gameService.authenticateAdmin(token, userService.getUserById(id));

    GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(gameService.createGameSession(gameId));
    return gameGetDTO;
  }

  // Get Mapping to get a list of all game sessions
  @GetMapping("/games/{gameId}/sessions")
  @ResponseStatus(HttpStatus.OK)
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
  public void createTextPrompt(@PathVariable Long gameSessionId, @PathVariable Long userId, @PathVariable Long previousDrawingId, @RequestBody TextPromptDTO textPromptDTO, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));

    gameService.createTextPrompt(gameSessionId, userId, previousDrawingId, textPromptDTO.getContent());
    }
  
  // Get Mapping to get the text prompt from the user, need to be then assigned to different partecipantes in the game
  @GetMapping("/games/{gameSessionId}/prompts/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public TextPromptDTO getTextPrompt(@PathVariable Long gameSessionId, @PathVariable Long userId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));

    TextPromptDTO textPromptDTO = DTOMapper.INSTANCE.convertEntityToTextPromptDTO(gameService.getTextPrompt(gameSessionId, userId));
    return textPromptDTO;
    }



  // Post mapping to create drawing entity in the database and save the drawing from the user
  @PostMapping("/games/{gameSessionId}/drawings/{userId}/{previousTextPromptId}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void createDrawing(@PathVariable Long gameSessionId, @PathVariable Long userId, @PathVariable Long previousTextPromptId, @RequestBody String drawingBase64, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));
    
    gameService.createDrawing(gameSessionId, userId, previousTextPromptId, drawingBase64);
  }
  
  // Get mapping for assigning drawing to user in game session
  @GetMapping("/games/{gameSessionId}/drawings/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public DrawingDTO getDrawing(@PathVariable Long gameSessionId, @PathVariable Long userId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));

    Drawing newDrawing = gameService.getDrawing(gameSessionId, userId);

    return DTOMapper.INSTANCE.convertEntityToDrawingDTO(newDrawing);
  }

  // mapping to start the next round -> increase roundCounter in current game session
  @PutMapping("games/{gameSessionId}/nextround")
  @ResponseStatus(HttpStatus.OK)
  public void startNextRound(@PathVariable Long gameSessionId) {
      gameService.startNextRound(gameSessionId);
  }
  
  // Get mappings for text prompt and drawing in presentation at the end -> /games/{gameId}/next/text OR /games/{gameId}/next/drawing
  // with the current textPromptId/drawingId in the path to fetch from server
  @GetMapping("/games/{gameSessionId}/next/text/{previousDrawingId}")
  public TextPromptDTO getNextTextPrompt(@PathVariable Long gameSessionId, @PathVariable Long previousDrawingId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
      gameService.authenticateAdmin(token, userService.getUserById(id));
      
      TextPromptDTO textPromptDTO = DTOMapper.INSTANCE.convertEntityToTextPromptDTO(gameService.getNextTextPrompt(gameSessionId, previousDrawingId));

      return textPromptDTO;
  }

  @GetMapping("/games/{gameSessionId}/next/drawing/{previousTextPromptId}")
  public DrawingDTO getNextDrawing(@PathVariable Long gameSessionId, @PathVariable Long previousTextPromptId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
      gameService.authenticateAdmin(token, userService.getUserById(id));
      
      DrawingDTO drawingDTO = DTOMapper.INSTANCE.convertEntityToDrawingDTO(gameService.getNextDrawing(gameSessionId, previousTextPromptId));

      return drawingDTO;
  }
  
  // Get mapping to get one of the first text prompts 
  @GetMapping("/games/{gameSessionId}/presentation")
  public TextPromptDTO getFirstTextPrompt(@PathVariable Long gameSessionId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
      gameService.authenticateAdmin(token, userService.getUserById(id));
      
      TextPromptDTO textPromptDTO = DTOMapper.INSTANCE.convertEntityToTextPromptDTO(gameService.getFirstTextPrompt(gameSessionId));

      return textPromptDTO;
  }
  

  
}