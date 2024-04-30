package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameLoopStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Drawing;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.DrawingRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameSessionRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameSettingsRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TextPromptRepository;
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
import java.time.LocalDateTime;
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
  private GameSettingsRepository gameSettingsRepository;

  @Autowired
  private DrawingRepository drawingRepository;

  @Autowired
  private TextPromptRepository textPromptRepository;

  @Autowired
  private GameService gameService;

  @Autowired
  private UserService userService;

  @Autowired
  private EntityManager entityManager;

  @BeforeEach
  public void setup() {
    gameSessionRepository.deleteAll();
    gameRepository.deleteAll();
    userRepository.deleteAll();
    gameSettingsRepository.deleteAll();
    drawingRepository.deleteAll();
    textPromptRepository.deleteAll();
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

    User createdAdmin = userService.createUser(admin);

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSettingsId(2L);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());

    gameRepository.save(game);
    gameRepository.flush();

    // when
    Game createdGame = gameService.createGame(createdAdmin.getUserId());

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

    User createdAdmin = userService.createUser(admin);

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

    Game game = gameService.createGame(createdAdmin.getUserId());
    game.setGameSessions(gameSessions);

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

  @Test
  public void getGameByGamePin_validInputs_success() {

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
    Game foundGame = gameService.getGameByGamePIN(game.getGamePin());

    // then
    assertEquals(game.getGamePin(), foundGame.getGamePin());
    assertEquals(game.getGameToken(), foundGame.getGameToken());
    assertEquals(game.getStatus(), foundGame.getStatus());
    assertEquals(game.getAdmin(), foundGame.getAdmin());
    assertEquals(game.getGameSettingsId(), foundGame.getGameSettingsId());
  }

  @Test
  public void cleanUpGame_noGame_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.gameroomCleanUp(2L));
  }

  @Transactional
  @Test
  public void cleanUpGame_success_Active() {

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

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    Game cleanGame = gameService.gameroomCleanUp(foundGame.getGameId());
    entityManager.flush();

    assertEquals(cleanGame.getStatus(), GameStatus.OPEN);

  }

  @Transactional
  @Test
  public void cleanUpGame_success_Inactive() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setRole("admin");
    admin.setStatus(UserStatus.ONLINE);

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSettingsId(2L);

    List<User> users = new ArrayList<User>();
    users.add(admin);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    Game cleanGame = gameService.gameroomCleanUp(foundGame.getGameId());
    entityManager.flush();

    assertEquals(cleanGame.getStatus(), GameStatus.CLOSED);

  }

  @Test
  public void getGameRoomUsers_noGame_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.getGameRoomUsers(2L));

  }

  @Transactional
  @Test
  public void getGameRoomUsers_success() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token 3");
    admin.setRole("admin");
    admin.setStatus(UserStatus.ONLINE);

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSettingsId(2L);

    List<User> users = new ArrayList<User>();
    users.add(createdAdmin);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    List<User> gameRoomUsers = gameService.getGameRoomUsers(foundGame.getGameId());

    assertEquals(gameRoomUsers.size(), users.size());
    assertEquals(gameRoomUsers.get(0), users.get(0));

  }

  @Transactional
  @Test
  public void getGameSettings_success() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token 3");
    admin.setRole("admin");
    admin.setStatus(UserStatus.ONLINE);

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setGameSpeed(40);
    gameSettings.setNumCycles(4);

    GameSettings createdGameSettings = gameSettingsRepository.save(gameSettings);
    gameSettingsRepository.flush();

    List<User> users = new ArrayList<User>();
    users.add(createdAdmin);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    GameSettings foundGameSettings = gameService.getGameSettings(foundGame.getGameId());

    assertEquals(foundGameSettings.getEnableTextToSpeech(), gameSettings.getEnableTextToSpeech());
    assertEquals(gameSettings.getNumCycles(), foundGameSettings.getNumCycles());
    assertEquals(gameSettings.getGameSpeed(), foundGameSettings.getGameSpeed());

  }

  @Transactional
  @Test
  public void updateGameSettings_success() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token 3");
    admin.setRole("admin");
    admin.setStatus(UserStatus.ONLINE);

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setGameSpeed(40);
    gameSettings.setNumCycles(4);

    GameSettings createdGameSettings = gameSettingsRepository.save(gameSettings);
    gameSettingsRepository.flush();

    GameSettings update = new GameSettings();
    update.setEnableTextToSpeech(false);
    update.setGameSpeed(50);
    update.setNumCycles(2);

    List<User> users = new ArrayList<User>();
    users.add(createdAdmin);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(gameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    Game updatedGame = gameService.updateGameSettings(foundGame.getGameId(), update);
    GameSettings updatedGameSettings = gameSettingsRepository.findByGameSettingsId(updatedGame.getGameSettingsId());

    assertEquals(update.getEnableTextToSpeech(), updatedGameSettings.getEnableTextToSpeech());
    assertEquals(update.getNumCycles(), updatedGameSettings.getNumCycles());
    assertEquals(update.getGameSpeed(), updatedGameSettings.getGameSpeed());;

  }

  @Test
  public void createTextPrompt_noGameSession_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.createTextPrompt(2L, 1L, 1L, "content"));

  }

  @Test
  public void createTextPrompt_noUser_throwsException() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token 3");
    admin.setRole("admin");
    admin.setStatus(UserStatus.ONLINE);

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(2L);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    assertThrows(ResponseStatusException.class, () -> gameService.createTextPrompt(foundGame.getGameId(), 178L, 1L, "content"));

  }

  @Transactional
  @Test
  public void createTextPrompt_Success() {

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

    List<Long> usersInSession = new ArrayList<Long>();
    usersInSession.add(createdAdmin.getUserId());
    usersInSession.add(createdPlayer.getUserId());

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setToken("testtokens");
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
    gameSession.setUsersInSession(usersInSession);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    Drawing drawing = new Drawing();
    drawing.setEncodedImage("encoded image");
    drawing.setCreationDateTime(LocalDateTime.now());

    Drawing createdDrawing = drawingRepository.save(drawing);
    drawingRepository.flush();

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
    TextPrompt text = gameService.createTextPrompt(createdGameSession.getGameSessionId(), createdAdmin.getUserId(), createdDrawing.getDrawingId(), "test content");
    gameRepository.flush();
    entityManager.flush();

    assertEquals(text.getContent(), "test content");
    assertEquals(text.getPreviousDrawingId(), createdDrawing.getDrawingId());
    assertEquals(text.getCreator(), createdAdmin);;
  }

  @Test
  public void getTextPrompt_noGameSession_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.getTextPrompt(2L, 1L));

  }

  @Test
  public void getTextPrompt_noUser_throwsException() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token 3");
    admin.setRole("admin");
    admin.setStatus(UserStatus.ONLINE);

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(2L);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    assertThrows(ResponseStatusException.class, () -> gameService.getTextPrompt(foundGame.getGameId(), 178L));

  }

  @Transactional
  @Test
  public void getTextPrompt_Success() {

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

    List<Long> usersInSession = new ArrayList<Long>();
    usersInSession.add(createdAdmin.getUserId());
    usersInSession.add(createdPlayer.getUserId());

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setToken("testtokens");
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
    gameSession.setUsersInSession(usersInSession);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    TextPrompt textPromptAdmin = new TextPrompt();
    textPromptAdmin.setContent("Admin content");
    textPromptAdmin.setCreator(createdAdmin);
    textPromptAdmin.setGameSession(createdGameSession);

    TextPrompt textPromptPlayer = new TextPrompt();
    textPromptPlayer.setContent("Player content");
    textPromptPlayer.setCreator(createdPlayer);
    textPromptPlayer.setGameSession(createdGameSession);

    TextPrompt createdTextPromptAdmin = textPromptRepository.save(textPromptAdmin);
    TextPrompt createdTextPromptPlayer = textPromptRepository.save(textPromptPlayer);
    textPromptRepository.flush();

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
    TextPrompt text = gameService.getTextPrompt(createdGameSession.getGameSessionId(), createdAdmin.getUserId());
    gameRepository.flush();
    entityManager.flush();

    assertEquals(text.getContent(), "Player content");
    assertEquals(text.getCreator(), createdPlayer);
    assertEquals(text.getAssignedTo(), createdAdmin.getUserId());
  }

  @Test
  public void createDrawing_noGameSession_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.createDrawing(2L, 1L, 1L, "test content"));

  }

  @Test
  public void createDrawing_noUser_throwsException() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token 3");
    admin.setRole("admin");
    admin.setStatus(UserStatus.ONLINE);

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(2L);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    assertThrows(ResponseStatusException.class, () -> gameService.createDrawing(foundGame.getGameId(), 178L, 1L, "content"));

  }

  @Transactional
  @Test
  public void createDrawing_Success() {

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
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setNumCycles(4);
    gameSettings.setGameSpeed(40);
    GameSettings createdGameSettings = gameSettingsRepository.save(gameSettings);

    List<User> users = new ArrayList<User>();
    users.add(admin);
    users.add(createdPlayer);

    List<Long> usersInSession = new ArrayList<Long>();
    usersInSession.add(createdAdmin.getUserId());
    usersInSession.add(createdPlayer.getUserId());

    TextPrompt text = new TextPrompt();
    text.setContent("test content");

    TextPrompt createdText = textPromptRepository.save(text);
    textPromptRepository.flush();

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(createdGameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setToken("testtokens");
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
    gameSession.setUsersInSession(usersInSession);
    gameSession.setGame(foundGame);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    foundGame.setGameSessions(gameSessions);

    

    Drawing drawing = gameService.createDrawing(createdGameSession.getGameSessionId(), createdAdmin.getUserId(), createdText.getTextPromptId(), "test encoded image");
    gameRepository.flush();
    entityManager.flush();

    assertEquals(drawing.getEncodedImage(), "test encoded image");
    assertEquals(drawing.getPreviousTextPrompt(), createdText.getTextPromptId());
    assertEquals(drawing.getCreator(), createdAdmin);
  }

  @Test
  public void getDrawing_noGameSession_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.getDrawing(2L, 1L));

  }

  @Test
  public void getDrawing_noUser_throwsException() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token 3");
    admin.setRole("admin");
    admin.setStatus(UserStatus.ONLINE);

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(2L);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    assertThrows(ResponseStatusException.class, () -> gameService.getDrawing(foundGame.getGameId(), 178L));

  }

  @Transactional
  @Test
  public void getDrawing_Success() {

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
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setGameSpeed(40);
    gameSettings.setNumCycles(4);

    GameSettings createdGameSettings = gameSettingsRepository.save(gameSettings);
    gameSettingsRepository.flush();

    List<User> users = new ArrayList<User>();
    users.add(admin);
    users.add(createdPlayer);

    List<Long> usersInSession = new ArrayList<Long>();
    usersInSession.add(createdAdmin.getUserId());
    usersInSession.add(createdPlayer.getUserId());

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(createdGameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setToken("testtokens");
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
    gameSession.setUsersInSession(usersInSession);
    gameSession.setGame(foundGame);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    game.setGameSessions(gameSessions);

    TextPrompt textPrompt1 = new TextPrompt();
    textPrompt1.setContent("Admin content");
    textPrompt1.setCreator(createdAdmin);
    textPrompt1.setGameSession(createdGameSession);

    TextPrompt textPrompt2 = new TextPrompt();
    textPrompt2.setContent("Player content");
    textPrompt2.setCreator(createdPlayer);
    textPrompt2.setGameSession(createdGameSession);

    TextPrompt createdTextPromptAdmin = textPromptRepository.save(textPrompt1);
    TextPrompt createdTextPromptPlayer = textPromptRepository.save(textPrompt2);
    textPromptRepository.flush();

    Drawing drawingAdmin = new Drawing();
    drawingAdmin.setEncodedImage("test encoded");
    drawingAdmin.setCreator(createdAdmin);
    drawingAdmin.setGameSessionId(createdGameSession.getGameSessionId());
    drawingAdmin.setCreationDateTime(LocalDateTime.now());
    drawingAdmin.setPreviousTextPrompt(textPrompt1.getTextPromptId());

    Drawing drawingPlayer = new Drawing();
    drawingPlayer.setEncodedImage("test encoded");
    drawingPlayer.setCreator(createdPlayer);
    drawingPlayer.setGameSessionId(createdGameSession.getGameSessionId());
    drawingPlayer.setCreationDateTime(LocalDateTime.now());
    drawingPlayer.setPreviousTextPrompt(textPrompt2.getTextPromptId());
    
    Drawing createdDrawingAdmin = drawingRepository.save(drawingAdmin);
    Drawing createdDrawingPlayer = drawingRepository.save(drawingPlayer);
    drawingRepository.flush();

    Drawing drawing = gameService.getDrawing(createdGameSession.getGameSessionId(), createdAdmin.getUserId());
    entityManager.flush();

    assertEquals(drawing.getEncodedImage(), "test encoded");
    assertEquals(drawing.getCreator(), createdPlayer);
    assertEquals(drawing.getAssignedTo(), createdAdmin.getUserId());
    assertNotEquals(textPromptRepository.findByTextPromptId(drawing.getPreviousTextPrompt()).getCreator(), createdAdmin);

  }

  @Transactional
  @Test
  public void getDrawing_AlreadyAssigned_Success() {

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
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setGameSpeed(40);
    gameSettings.setNumCycles(4);

    GameSettings createdGameSettings = gameSettingsRepository.save(gameSettings);
    gameSettingsRepository.flush();

    List<User> users = new ArrayList<User>();
    users.add(admin);
    users.add(createdPlayer);

    List<Long> usersInSession = new ArrayList<Long>();
    usersInSession.add(createdAdmin.getUserId());
    usersInSession.add(createdPlayer.getUserId());

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(createdGameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setToken("testtokens");
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
    gameSession.setUsersInSession(usersInSession);
    gameSession.setGame(foundGame);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    game.setGameSessions(gameSessions);

    TextPrompt textPrompt1 = new TextPrompt();
    textPrompt1.setContent("Admin content");
    textPrompt1.setCreator(createdAdmin);
    textPrompt1.setGameSession(createdGameSession);

    TextPrompt textPrompt2 = new TextPrompt();
    textPrompt2.setContent("Player content");
    textPrompt2.setCreator(createdPlayer);
    textPrompt2.setGameSession(createdGameSession);

    TextPrompt createdTextPromptAdmin = textPromptRepository.save(textPrompt1);
    TextPrompt createdTextPromptPlayer = textPromptRepository.save(textPrompt2);
    textPromptRepository.flush();

    Drawing drawingAdmin = new Drawing();
    drawingAdmin.setEncodedImage("test encoded");
    drawingAdmin.setCreator(createdAdmin);
    drawingAdmin.setGameSessionId(createdGameSession.getGameSessionId());
    drawingAdmin.setCreationDateTime(LocalDateTime.now());
    drawingAdmin.setAssignedTo(createdPlayer.getUserId());
    drawingAdmin.setPreviousTextPrompt(textPrompt1.getTextPromptId());

    Drawing drawingPlayer = new Drawing();
    drawingPlayer.setEncodedImage("test encoded");
    drawingPlayer.setCreator(createdPlayer);
    drawingPlayer.setGameSessionId(createdGameSession.getGameSessionId());
    drawingPlayer.setCreationDateTime(LocalDateTime.now());
    drawingPlayer.setPreviousTextPrompt(textPrompt2.getTextPromptId());
    
    Drawing createdDrawingAdmin = drawingRepository.save(drawingAdmin);
    Drawing createdDrawingPlayer = drawingRepository.save(drawingPlayer);
    drawingRepository.flush();

    Drawing drawing = gameService.getDrawing(createdGameSession.getGameSessionId(), createdAdmin.getUserId());
    entityManager.flush();

    assertEquals(drawing.getEncodedImage(), "test encoded");
    assertEquals(drawing.getCreator(), createdPlayer);
    assertEquals(drawing.getAssignedTo(), createdAdmin.getUserId());
    assertNotEquals(textPromptRepository.findByTextPromptId(drawing.getPreviousTextPrompt()).getCreator(), createdAdmin);

  }

  @Transactional
  @Test
  public void getDrawing_LastDrawingOwn_Success() {

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

    User player2 = new User();
    player2.setNickname("testNickname");
    player2.setCreationDate(LocalDate.now());
    player2.setToken("test token 2");
    player2.setRole("player");
    player2.setStatus(UserStatus.ONLINE);

    User createdPlayer2 = userRepository.save(player);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setGameSpeed(40);
    gameSettings.setNumCycles(4);

    GameSettings createdGameSettings = gameSettingsRepository.save(gameSettings);
    gameSettingsRepository.flush();

    List<User> users = new ArrayList<User>();
    users.add(admin);
    users.add(createdPlayer);

    List<Long> usersInSession = new ArrayList<Long>();
    usersInSession.add(createdAdmin.getUserId());
    usersInSession.add(createdPlayer.getUserId());

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(createdGameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setToken("testtokens");
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
    gameSession.setUsersInSession(usersInSession);
    gameSession.setGame(foundGame);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    game.setGameSessions(gameSessions);

    TextPrompt textPrompt1 = new TextPrompt();
    textPrompt1.setContent("Admin content");
    textPrompt1.setCreator(createdAdmin);
    textPrompt1.setGameSession(createdGameSession);

    TextPrompt textPrompt2 = new TextPrompt();
    textPrompt2.setContent("Player content");
    textPrompt2.setCreator(createdPlayer);
    textPrompt2.setGameSession(createdGameSession);

    TextPrompt textPrompt3 = new TextPrompt();
    textPrompt3.setContent("Player content");
    textPrompt3.setCreator(createdPlayer);
    textPrompt3.setGameSession(createdGameSession);

    TextPrompt createdTextPromptAdmin = textPromptRepository.save(textPrompt1);
    TextPrompt createdTextPromptPlayer = textPromptRepository.save(textPrompt2);
    TextPrompt createdTextPromptPlayer2 = textPromptRepository.save(textPrompt3);
    textPromptRepository.flush();

    Drawing drawingAdmin = new Drawing();
    drawingAdmin.setEncodedImage("test encoded");
    drawingAdmin.setCreator(createdAdmin);
    drawingAdmin.setGameSessionId(createdGameSession.getGameSessionId());
    drawingAdmin.setCreationDateTime(LocalDateTime.now());
    drawingAdmin.setPreviousTextPrompt(textPrompt1.getTextPromptId());

    Drawing drawingPlayer = new Drawing();
    drawingPlayer.setEncodedImage("test encoded 1");
    drawingPlayer.setCreator(createdPlayer);
    drawingPlayer.setGameSessionId(createdGameSession.getGameSessionId());
    drawingPlayer.setCreationDateTime(LocalDateTime.now());
    drawingPlayer.setAssignedTo(createdPlayer2.getUserId());
    drawingPlayer.setPreviousTextPrompt(textPrompt2.getTextPromptId());

    Drawing drawingPlayer2 = new Drawing();
    drawingPlayer2.setEncodedImage("test encoded 1");
    drawingPlayer2.setCreator(createdPlayer);
    drawingPlayer2.setGameSessionId(createdGameSession.getGameSessionId());
    drawingPlayer2.setCreationDateTime(LocalDateTime.now());
    drawingPlayer2.setAssignedTo(createdPlayer.getUserId());
    drawingPlayer2.setPreviousTextPrompt(textPrompt3.getTextPromptId());
    
    Drawing createdDrawingAdmin = drawingRepository.save(drawingAdmin);
    Drawing createdDrawingPlayer = drawingRepository.save(drawingPlayer);
    Drawing createdDrawingPlayer2 = drawingRepository.save(drawingPlayer2);
    drawingRepository.flush();

    Drawing drawing = gameService.getDrawing(createdGameSession.getGameSessionId(), createdAdmin.getUserId());
    entityManager.flush();

    assertEquals(drawing.getEncodedImage(), "test encoded 1");
    assertNotEquals(textPromptRepository.findByTextPromptId(drawing.getPreviousTextPrompt()).getCreator(), createdAdmin);
  }
  

  @Transactional
  @Test
  public void startNextRound_Success_Presentation() {

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
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setGameSpeed(40);
    gameSettings.setNumCycles(4);

    GameSettings createdGameSettings = gameSettingsRepository.save(gameSettings);
    gameSettingsRepository.flush();

    List<User> users = new ArrayList<User>();
    users.add(admin);
    users.add(createdPlayer);

    List<Long> usersInSession = new ArrayList<Long>();
    usersInSession.add(createdAdmin.getUserId());
    usersInSession.add(createdPlayer.getUserId());

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(createdGameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setToken("testtokens");
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
    gameSession.setUsersInSession(usersInSession);
    gameSession.setGame(foundGame);
    gameSession.setRoundCounter(8);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    game.setGameSessions(gameSessions);

    gameService.startNextRound(createdGameSession.getGameSessionId());
    entityManager.flush();

    assertEquals(createdGameSession.getGameLoopStatus(), GameLoopStatus.PRESENTATION);
  }

  @Transactional
  @Test
  public void startNextRound_Success_increaseRoundCounter() {

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
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setGameSpeed(40);
    gameSettings.setNumCycles(4);

    GameSettings createdGameSettings = gameSettingsRepository.save(gameSettings);
    gameSettingsRepository.flush();

    List<User> users = new ArrayList<User>();
    users.add(admin);
    users.add(createdPlayer);

    List<Long> usersInSession = new ArrayList<Long>();
    usersInSession.add(createdAdmin.getUserId());
    usersInSession.add(createdPlayer.getUserId());

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(createdGameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setToken("testtokens");
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
    gameSession.setUsersInSession(usersInSession);
    gameSession.setGame(foundGame);
    gameSession.setRoundCounter(6);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    game.setGameSessions(gameSessions);

    gameService.startNextRound(createdGameSession.getGameSessionId());
    entityManager.flush();

    assertEquals(createdGameSession.getGameLoopStatus(), GameLoopStatus.TEXTPROMPT);
    assertEquals(createdGameSession.getRoundCounter(), 7);
  }

  @Transactional
  @Test
  public void getAllGames_Success() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setRole("admin");
    admin.setStatus(UserStatus.ONLINE);

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    GameSettings gameSettings = new GameSettings();
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setGameSpeed(40);
    gameSettings.setNumCycles(4);

    GameSettings createdGameSettings = gameSettingsRepository.save(gameSettings);
    gameSettingsRepository.flush();

    List<User> users = new ArrayList<User>();
    users.add(admin);

    List<Long> usersInSession = new ArrayList<Long>();
    usersInSession.add(createdAdmin.getUserId());

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(createdGameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setToken("testtokens");
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
    gameSession.setUsersInSession(usersInSession);
    gameSession.setGame(foundGame);
    gameSession.setRoundCounter(6);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    game.setGameSessions(gameSessions);

    List<Game> games = gameService.getAllGames();
    entityManager.flush();

    assertEquals(games.get(0), foundGame);
    assertEquals(games.size(), 1);
  }

  @Transactional
  @Test
  public void getSequence_Success() {

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
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setNumCycles(4);
    gameSettings.setGameSpeed(40);
    GameSettings createdGameSettings = gameSettingsRepository.save(gameSettings);

    List<User> users = new ArrayList<User>();
    users.add(admin);
    users.add(createdPlayer);

    List<Long> usersInSession = new ArrayList<Long>();
    usersInSession.add(createdAdmin.getUserId());
    usersInSession.add(createdPlayer.getUserId());

    

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(createdGameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setToken("testtokens");
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
    gameSession.setUsersInSession(usersInSession);
    gameSession.setGame(foundGame);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    TextPrompt text = new TextPrompt();
    text.setContent("test content");
    text.setPreviousDrawingId(777L);
    text.setGameSession(createdGameSession);

    TextPrompt createdText = textPromptRepository.save(text);
    textPromptRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    foundGame.setGameSessions(gameSessions);

    Drawing drawing = gameService.createDrawing(createdGameSession.getGameSessionId(), createdAdmin.getUserId(), createdText.getTextPromptId(), "test encoded image");

    List<Object> sequence = new ArrayList<Object>();
    sequence.add(createdText);
    sequence.add(drawing);

    List<Object> sequenceCall = gameService.getSequence(createdGameSession.getGameSessionId());

    entityManager.flush();

    assertEquals(sequence.get(0), sequenceCall.get(0));
    assertEquals(sequence.get(1), sequenceCall.get(1));

  }

  @Transactional
  @Test
  public void getCurrentIndex_IncreaseIndex_Success() {

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
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setNumCycles(4);
    gameSettings.setGameSpeed(40);
    GameSettings createdGameSettings = gameSettingsRepository.save(gameSettings);

    List<User> users = new ArrayList<User>();
    users.add(admin);
    users.add(createdPlayer);

    List<Long> usersInSession = new ArrayList<Long>();
    usersInSession.add(createdAdmin.getUserId());
    usersInSession.add(createdPlayer.getUserId());

    

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(createdGameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setToken("testtokens");
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
    gameSession.setUsersInSession(usersInSession);
    gameSession.setGame(foundGame);
    gameSession.setCurrentIndex(3);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    foundGame.setGameSessions(gameSessions);

    gameService.increaseCurrentIndex(createdGameSession.getGameSessionId());

    entityManager.flush();

    assertEquals(gameService.getCurrentIndex(createdGameSession.getGameSessionId()), 4);

  }

  @Test
  public void endGameSession_NotFound_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.endGameSessionAndDeleteTextPrompts(2L));

  }

  @Transactional
  @Test
  public void endGameSession_Success() {

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

    List<Long> usersInSession = new ArrayList<Long>();
    usersInSession.add(createdAdmin.getUserId());
    usersInSession.add(createdPlayer.getUserId());

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setToken("testtokens");
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
    gameSession.setUsersInSession(usersInSession);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    TextPrompt textPromptAdmin = new TextPrompt();
    textPromptAdmin.setContent("Admin content");
    textPromptAdmin.setCreator(createdAdmin);
    textPromptAdmin.setGameSession(createdGameSession);

    TextPrompt textPromptPlayer = new TextPrompt();
    textPromptPlayer.setContent("Player content");
    textPromptPlayer.setCreator(createdPlayer);
    textPromptPlayer.setGameSession(createdGameSession);

    TextPrompt createdTextPromptAdmin = textPromptRepository.save(textPromptAdmin);
    TextPrompt createdTextPromptPlayer = textPromptRepository.save(textPromptPlayer);
    textPromptRepository.flush();

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

    


    gameService.endGameSessionAndDeleteTextPrompts(createdGameSession.getGameSessionId());

    entityManager.flush();

    assertEquals(textPromptRepository.findAll().size(), 0);
  }

  @Test
  public void increaseTextPromptVotes_noGameSession_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.increasePromptVotes(2L, 1L));

  }

  @Test
  public void increasePromptVotes_noTextPrompt_throwsException() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token 3");
    admin.setRole("admin");
    admin.setStatus(UserStatus.ONLINE);

    User createdAdmin = userRepository.save(admin);
    userRepository.flush();

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(2L);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    assertThrows(ResponseStatusException.class, () -> gameService.increasePromptVotes(foundGame.getGameId(), 178L));

  }

  @Transactional
  @Test
  public void increaseTextPromptVotes_Success_increaseRoundCounter() {

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
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setGameSpeed(40);
    gameSettings.setNumCycles(4);

    GameSettings createdGameSettings = gameSettingsRepository.save(gameSettings);
    gameSettingsRepository.flush();

    List<User> users = new ArrayList<User>();
    users.add(admin);
    users.add(createdPlayer);

    List<Long> usersInSession = new ArrayList<Long>();
    usersInSession.add(createdAdmin.getUserId());
    usersInSession.add(createdPlayer.getUserId());

    Game game = new Game();
    game.setGamePin(777777L);
    game.setGameToken("test token");
    game.setStatus(GameStatus.OPEN);
    game.setAdmin(createdAdmin.getUserId());
    game.setGameSettingsId(createdGameSettings.getGameSettingsId());
    game.setUsers(users);

    Game foundGame = gameRepository.save(game);
    gameRepository.flush();

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setToken("testtokens");
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
    gameSession.setUsersInSession(usersInSession);
    gameSession.setGame(foundGame);
    gameSession.setRoundCounter(6);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    game.setGameSessions(gameSessions);

    TextPrompt text = new TextPrompt();
    text.setContent("test content");
    text.setCreator(createdPlayer);
    text.setGameSession(createdGameSession);

    TextPrompt createdText = textPromptRepository.save(text);
    textPromptRepository.flush();

    gameService.increasePromptVotes(createdGameSession.getGameSessionId(), createdText.getTextPromptId());;
    entityManager.flush();

    assertEquals(createdText.getNumVotes(), 1);
  }

}

