package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Drawing;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.SessionHistory;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TextPromptDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;

import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.Game.GameService;
import ch.uzh.ifi.hase.soprafs24.service.Game.HistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

import ch.uzh.ifi.hase.soprafs24.rest.dto.DrawingDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.SessionHistoryDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;




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
  private final HistoryService historyService;

  @Autowired
  public GameController(GameService gameService, UserService userService, HistoryService historyService) {
    this.gameService = gameService;
    this.userService = userService;  
    this.historyService = historyService;
  }


  // Post Mapping to create a game room - when testing with Postman, the body should be a JSON object with the key "username" and 'name' as the value
  @PostMapping("/gameRooms/create/{userId}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public GameGetDTO createRoom(@PathVariable Long userId) {
    Game newGame = gameService.createGame(userId);
    GameGetDTO gameDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(newGame);
    return gameDTO;
  }

  // Post Mapping to join a game room - when testing with Postman, the body should be a JSON object with the key "username" and 'name' as the value
  // tested with postman for the exisiting user and for the new user(201)
  @PostMapping("/gameRooms/join/{submittedPin}/{userId}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public GameGetDTO joinRoom(@PathVariable Long submittedPin, @PathVariable Long userId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));

    Game game = gameService.joinGame(submittedPin, userId);

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
  @PutMapping("/games/{gameSessionId}/nextround")
  @ResponseStatus(HttpStatus.OK)
  public void startNextRound(@PathVariable Long gameSessionId) {
      gameService.startNextRound(gameSessionId);
  }
  
  // get mapping to get a list of one whole sequence
  @GetMapping("/games/{gameSessionId}/sequence")
  public List<Object> getSequence(@PathVariable Long gameSessionId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));

    return gameService.getSequence(gameSessionId);
  }

  // get mapping for users to get index of next item
  @GetMapping("/games/{gameSessionId}/presentation/next")
  public int getCurrentIndex(@PathVariable Long gameSessionId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id)); 

    return gameService.getCurrentIndex(gameSessionId);
  }
  
  // put mapping for users to get index of next item
  @PutMapping("/games/{gameSessionId}/presentation/next")
  public int increaseCurrentIndex(@PathVariable Long gameSessionId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    gameService.authenticateAdmin(token, userService.getUserById(id)); 

    return gameService.increaseCurrentIndex(gameSessionId);
  }

  // get mapping to get current game
  @GetMapping("/games/{gameId}")
  public GameGetDTO getGame(@PathVariable Long gameId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));

    Game game = gameService.getGame(gameId);

    return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
  }

  // put mapping to open current game
  @PutMapping("/games/{gameId}")
  @ResponseStatus(HttpStatus.OK)
  public void openGame(@PathVariable Long gameId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
    userService.authenticateUser(token, userService.getUserById(id));

    gameService.openGame(gameId);
  }

  // put mapping to increase number of votes for given text prompt
  @PutMapping("/games/{gameSessionId}/prompt/{textPromptId}/vote")
  @ResponseStatus(HttpStatus.OK)
  public void increasePromptVotes(@PathVariable Long gameSessionId, @PathVariable Long textPromptId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
      
      userService.authenticateUser(token, userService.getUserById(id));
      
      gameService.increasePromptVotes(gameSessionId, textPromptId, id);
  }

  // put mapping to increase number of votes for given drawing
  @PutMapping("/games/{gameSessionId}/drawing/{drawingId}/vote")
  @ResponseStatus(HttpStatus.OK)
  public void increaseDrawingVotes(@PathVariable Long gameSessionId, @PathVariable Long drawingId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
      
      userService.authenticateUser(token, userService.getUserById(id));
      
      gameService.increaseDrawingVotes(gameSessionId, drawingId, id);
  }
  
  // put mapping to decrease number of votes for given text prompt
  @PutMapping("/games/{gameSessionId}/prompt/{textPromptId}/unvote")
  @ResponseStatus(HttpStatus.OK)
  public void decreasePromptVotes(@PathVariable Long gameSessionId, @PathVariable Long textPromptId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
      
      userService.authenticateUser(token, userService.getUserById(id));
      
      gameService.decreasePromptVotes(gameSessionId, textPromptId, id);
  }
  
  // put mapping to decrease number of votes for given drawing
  @PutMapping("/games/{gameSessionId}/drawing/{drawingId}/unvote")
  @ResponseStatus(HttpStatus.OK)
  public void decreaseDrawingVotes(@PathVariable Long gameSessionId, @PathVariable Long drawingId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {
      
      userService.authenticateUser(token, userService.getUserById(id));
      
      gameService.decreaseDrawingVotes(gameSessionId, drawingId, id);
  }

  // get mapping to get top three text prompts
  @GetMapping("/games/{gameSessionId}/top/text")
  @ResponseStatus(HttpStatus.OK)
  public List<TextPrompt> getTopThreeTextPrompts(@PathVariable Long gameSessionId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {

      userService.authenticateUser(token, userService.getUserById(id));

      return gameService.getTopThreeTextPrompts(gameSessionId);
  }

  // get mapping to get top three text prompts
  @GetMapping("/games/{gameSessionId}/top/drawing")
  @ResponseStatus(HttpStatus.OK)
  public List<Drawing> getTopThreeDrawings(@PathVariable Long gameSessionId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id) {

      userService.authenticateUser(token, userService.getUserById(id));

      return gameService.getTopThreeDrawings(gameSessionId);
  }
  
  // get mapping to get the history of the game session
  // can not be tested yet, because need implementation of persistent user in usercontroller
  @GetMapping
  public ResponseEntity<List<List<Object>>> getAllUserSequences(@PathVariable Long userId) {
      List<List<Object>> allSequences = historyService.getAllUserSequences(userId);
      return ResponseEntity.ok(allSequences);
  }


}