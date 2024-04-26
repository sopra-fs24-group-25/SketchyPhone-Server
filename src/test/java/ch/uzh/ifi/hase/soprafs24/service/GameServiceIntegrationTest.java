package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameSessionRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.service.Game.GameService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import java.util.ArrayList;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {

  @Qualifier("gameRepository")
  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GameSessionRepository gameSessionRepository;

  @Autowired
  private GameService gameService;

  @Autowired
  private EntityManager entityManager;

  @BeforeEach
  public void setup() {
    gameSessionRepository.deleteAll();
    userRepository.deleteAll();
    gameRepository.deleteAll();
  }

  @Test
  public void getGame_validInputs_success() {

    User admin = new User();
    admin.setUserId(1L);

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSettingsId(2L);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(admin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());

    gameRepository.save(game);
    gameRepository.flush();

    // when
    Game foundGame = gameService.getGame(game.getGameId());

    // then
    assertEquals(game.getGamePin(), foundGame.getGamePin());
    assertEquals(game.getGameToken(), foundGame.getGameToken());
    assertEquals(game.getStatus(), foundGame.getStatus());
    assertEquals(game.getAdmin(), foundGame.getAdmin());
    assertEquals(game.getGameSettingsId(), foundGame.getGameSettingsId());
  }

  @Test
  public void getGame_notFound_throwsException() {

    // attempt to get Game that wasn't created
    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> gameService.getGame(1L));;
  }

  @Test
  public void generateGamePin_validInputs_success() {

    // when
    long gamePin = gameService.generateGamePin();

    // then
    // you should always get a six digit number
    assertEquals((String.valueOf(gamePin)).length(), 6);
  }

  @Test
  public void createGame_validInputs_success() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setStatus(UserStatus.ONLINE);

    userRepository.save(admin);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSettingsId(2L);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(admin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());

    gameRepository.save(game);
    gameRepository.flush();

    // when
    Game createdGame = gameService.createGame(admin.getUserId());

    // then
    assertEquals(String.valueOf(game.getGamePin()).length(), 6);
    assertNotNull(createdGame.getGameToken());
    assertEquals(game.getStatus(), createdGame.getStatus());
    assertEquals(game.getAdmin(), createdGame.getAdmin());
    assertNotNull(createdGame.getGameSettingsId());
    assertNotNull(createdGame.getGameId());
    assertEquals(createdGame.getGameCreationDate(), LocalDate.now());
  }

  @Test
  public void createGame_UserNotFound_throwsException() {

    // attempt to get Game that wasn't created
    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> gameService.createGame(1L));;
  }

  @Test
  public void createGameSession_validInputs_success() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setStatus(UserStatus.ONLINE);

    userRepository.save(admin);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSettingsId(2L);

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setToken("test token");

    gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(gameSession);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(admin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());
    game.setGameSessions(gameSessions);

    game = gameRepository.save(game);
    gameRepository.flush();

    // when
    Game createdGame = gameService.createGameSession(game.getGameId());

    // then
    assertEquals(gameSessions.size(), createdGame.getGameSessions().size());
  }

  @Test
  public void createGameSession_validInputs_UsersInGame_success() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setStatus(UserStatus.ONLINE);

    userRepository.save(admin);
    userRepository.flush();

    List<User> users = new ArrayList<User>();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSettingsId(2L);

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setToken("test token");

    gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(gameSession);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(admin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());
    game.setGameSessions(gameSessions);
    game.setUsers(users);

    game = gameRepository.save(game);
    gameRepository.flush();

    // when
    Game createdGame = gameService.createGameSession(game.getGameId());

    // then
    assertEquals(gameSessions.size(), createdGame.getGameSessions().size());
    assertEquals(createdGame.getUsers().size(), createdGame.getGameSessions().get(0).getUsersInSession().size());
  }

  @Test
  public void authenticate_notAdmin_throwsException() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setStatus(UserStatus.ONLINE);
    admin.setRole("player");
    admin.setToken("test token");

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    // attempt to authenticate user that's not an admin
    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> gameService.authenticateAdmin("test token", createdAdmin));
  }

  @Test
  public void authenticateAdmin_success() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setStatus(UserStatus.ONLINE);
    admin.setRole("admin");
    admin.setToken("test token");

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    // attempt to authenticate user that's not an admin
    // check that an error is thrown
    assertDoesNotThrow(() -> gameService.authenticateAdmin("test token", createdAdmin));
  }

  @Test
  public void authenticate_notAdmin_wrongToken_throwsException() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setStatus(UserStatus.ONLINE);
    admin.setRole("admin");
    admin.setToken("test token");

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    // attempt to authenticate user that's not an admin
    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> gameService.authenticateAdmin("test toke", createdAdmin));
  }

  @Test
  public void joinGame_validInputs_success() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setStatus(UserStatus.ONLINE);

    userRepository.save(admin);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSettingsId(2L);

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setToken("test token");

    gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(gameSession);

    List<User> users = new ArrayList<User>();
    users.add(admin);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(admin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());
    game.setGameSessions(gameSessions);
    game.setUsers(users);

    game = gameRepository.save(game);
    gameRepository.flush();

    User user = new User();
    user.setNickname("testNickname");
    user.setCreationDate(LocalDate.now());
    user.setToken("test token 2");
    user.setStatus(UserStatus.ONLINE);

    userRepository.save(user);
    userRepository.flush();

    // when
    Game joinedGame = gameService.joinGame(game.getGamePin(), user.getUserId());

    // then
    assertEquals(joinedGame.getUsers().size(), 2);
  }

  @Test
  public void joinGame_noUser_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.joinGame(2L, 2L));
  }

  @Test
  public void joinGame_noGame_throwsException() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setStatus(UserStatus.ONLINE);

    userRepository.save(admin);
    userRepository.flush();

    assertThrows(ResponseStatusException.class, () -> gameService.joinGame(2L, admin.getUserId()));
  }

  @Test
  public void joinGame_GameClosed_throwsException() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setStatus(UserStatus.ONLINE);

    userRepository.save(admin);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSettingsId(2L);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.CLOSED);
    game.setAdmin(admin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());

    Game createdGame = gameRepository.save(game);
    gameRepository.flush();

    assertThrows(ResponseStatusException.class, () -> gameService.joinGame(admin.getUserId(), createdGame.getGameId()));
  }

  @Test
  public void joinGame_GameInPlay_throwsException() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setStatus(UserStatus.ONLINE);

    userRepository.save(admin);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSettingsId(2L);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.IN_PLAY);
    game.setAdmin(admin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());

    Game createdGame = gameRepository.save(game);
    gameRepository.flush();

    assertThrows(ResponseStatusException.class, () -> gameService.joinGame(admin.getUserId(), createdGame.getGameId()));
  }

  @Test
  public void leaveGame_notCreated_throwsException() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setStatus(UserStatus.ONLINE);

    userRepository.save(admin);
    userRepository.flush();

    User user = new User();
    user.setNickname("testNickname");
    user.setCreationDate(LocalDate.now());
    user.setToken("test token 2");
    user.setStatus(UserStatus.ONLINE);

    userRepository.save(user);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSettingsId(2L);

    List<User> users = new ArrayList<User>();
    users.add(admin);
    users.add(user);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(admin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    // attempt to remove user that hasn't been created
    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> gameService.leaveRoom(foundGame.getGameId(), 34L));
  }

  @Transactional
  @Test
  public void leaveGame_Success_reassignAdmin() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setRole("admin");
    admin.setStatus(UserStatus.ONLINE);

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    User player = new User();
    player.setNickname("testNickname");
    player.setCreationDate(LocalDate.now());
    player.setToken("test token 2");
    player.setRole("player");
    player.setStatus(UserStatus.ONLINE);

    User createdPlayer = userRepository.save(player);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSettingsId(2L);

    List<User> users = new ArrayList<User>();
    users.add(admin);
    users.add(createdPlayer);

    List<GameSession> gameSessions = new ArrayList<GameSession>();

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());
    game.setUsers(users);
    game.setGameSessions(gameSessions);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    

    // call function should reassign admin role to createdPlayer
    gameService.leaveRoom(foundGame.getGameId(), createdAdmin.getUserId());
    gameRepository.flush();
    entityManager.flush();

    assertEquals(foundGame.getUsers().size(), 1);
    assertEquals(foundGame.getAdmin(), createdPlayer.getUserId());
    assertEquals(foundGame.getUsers().get(0).getRole(), "admin");
  }

  @Transactional
  @Test
  public void leaveGame_Success_NoReassignAdmin() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setRole("admin");
    admin.setStatus(UserStatus.ONLINE);

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    User player = new User();
    player.setNickname("testNickname");
    player.setCreationDate(LocalDate.now());
    player.setToken("test token 2");
    player.setRole("player");
    player.setStatus(UserStatus.ONLINE);

    User createdPlayer = userRepository.save(player);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSettingsId(2L);

    List<User> users = new ArrayList<User>();
    users.add(admin);
    users.add(createdPlayer);

    List<Long> userIds = new ArrayList<Long>();
    userIds.add(createdAdmin.getUserId());
    userIds.add(createdPlayer.getUserId());

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setToken("test token");
    gameSession.setUsersInSession(userIds);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());
    game.setUsers(users);
    game.setGameSessions(gameSessions);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    

    // call function should reassign admin role to createdPlayer
    gameService.leaveRoom(foundGame.getGameId(), createdPlayer.getUserId());
    gameRepository.flush();
    entityManager.flush();

    assertEquals(foundGame.getUsers().size(), 1);
    assertEquals(foundGame.getAdmin(), createdAdmin.getUserId());
    assertEquals(gameSession.getUsersInSession().size(), 1);

  }

}
