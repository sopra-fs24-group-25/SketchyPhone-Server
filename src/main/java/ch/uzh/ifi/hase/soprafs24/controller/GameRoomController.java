package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.GameRoom;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class GameRoomController {

  private final GameRoomService gameRoomService;

  GameRoomController(GameRoomService gameRoomService) {
    this.gameRoomService = gameRoomService;
  }

  // Post Mapping to create a game room
  @PostMapping("/gameRooms/create")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public GameRoom createGameRoom(@RequestBody UserPostDTO userPostDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    GameRoom newRoom = gameRoomService.createGameRoom(userInput);
    return newRoom;
  }
}
