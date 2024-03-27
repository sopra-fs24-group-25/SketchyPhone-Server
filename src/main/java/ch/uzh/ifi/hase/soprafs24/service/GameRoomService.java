package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.GameRoom;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameRoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class GameRoomService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final GameRoomRepository gameRoomRepository;

  @Autowired
  public GameRoomService(@Qualifier("gameRoomRepository") GameRoomRepository gameRoomRepository) {
    this.gameRoomRepository = gameRoomRepository;
  }

  public List<GameRoom> getGameRooms() {
    return this.gameRoomRepository.findAll();
  }

  public GameRoom createGameRoom(User admin) {
    GameRoom newRoom = new GameRoom();
    if (admin.getName() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Game Room couldn't be created.");
    }
    newRoom.setAdmin(admin);
    newRoom.setToken(UUID.randomUUID().toString());
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newRoom = gameRoomRepository.save(newRoom);
    gameRoomRepository.flush();

    log.debug("Created Information for Game Room: {}", newRoom);

    return newRoom;
  }
}
