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

import org.apache.catalina.connector.Response;
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
import java.time.Instant;
import java.time.Duration;
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
  public void joinGame_validInputs_MaxPlayers_success() {
    // create admin and create 7 players
    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setStatus(UserStatus.ONLINE);

    admin = userService.createUser(admin);

    User player1 = new User();
    player1.setNickname("testNickname");
    player1.setCreationDate(LocalDate.now());
    player1.setToken("test token");
    player1.setStatus(UserStatus.ONLINE);

    player1 = userService.createUser(player1);

    User player2 = new User();
    player2.setNickname("testNickname");
    player2.setCreationDate(LocalDate.now());
    player2.setToken("test token");
    player2.setStatus(UserStatus.ONLINE);

    player2 = userService.createUser(player2);

    User player3 = new User();
    player3.setNickname("testNickname");
    player3.setCreationDate(LocalDate.now());
    player3.setToken("test token");
    player3.setStatus(UserStatus.ONLINE);

    player3 = userService.createUser(player3);

    User player4 = new User();
    player4.setNickname("testNickname");
    player4.setCreationDate(LocalDate.now());
    player4.setToken("test token");
    player4.setStatus(UserStatus.ONLINE);

    player4 = userService.createUser(player4);

    User player5 = new User();
    player5.setNickname("testNickname");
    player5.setCreationDate(LocalDate.now());
    player5.setToken("test token");
    player5.setStatus(UserStatus.ONLINE);

    player5 = userService.createUser(player5);

    User player6 = new User();
    player6.setNickname("testNickname");
    player6.setCreationDate(LocalDate.now());
    player6.setToken("test token");
    player6.setStatus(UserStatus.ONLINE);

    player6 = userService.createUser(player6);

    List<User> users = new ArrayList<User>();
    users.add(admin);
    users.add(player1);
    users.add(player2);
    users.add(player3);
    users.add(player4);
    users.add(player5);
    users.add(player6);

    Game game = gameService.createGame(admin.getUserId());
    game = gameService.joinGame(game.getGamePin(), player1.getUserId());
    game = gameService.joinGame(game.getGamePin(), player2.getUserId());
    game = gameService.joinGame(game.getGamePin(), player3.getUserId());
    game = gameService.joinGame(game.getGamePin(), player4.getUserId());
    game = gameService.joinGame(game.getGamePin(), player5.getUserId());
    game = gameService.joinGame(game.getGamePin(), player6.getUserId());

    User user = new User();
    user.setNickname("testNickname");
    user.setCreationDate(LocalDate.now());
    user.setToken("test token 2");
    user.setStatus(UserStatus.ONLINE);

    user = userService.createUser(user);

    // when
    game = gameService.joinGame(game.getGamePin(), user.getUserId());

    // then
    assertEquals(game.getUsers().size(), 8);
    assertEquals(game.getStatus(), GameStatus.CLOSED);
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

    User createdAdmin = userService.createUser(admin);

    Game game = gameService.createGame(createdAdmin.getUserId());
    game.setStatus(GameStatus.CLOSED);

    assertThrows(ResponseStatusException.class, () -> gameService.joinGame(createdAdmin.getUserId(), gameRepository.findByGameId(game.getGameId()).getGameId()));
  }

  @Test
  public void joinGame_GameInPlay_throwsException() {

    User admin = new User();
    admin.setNickname("testNickname");
    
    User createdAdmin = userService.createUser(admin);

    Game game = gameService.createGame(createdAdmin.getUserId());
    game.setStatus(GameStatus.IN_PLAY);

    assertThrows(ResponseStatusException.class, () -> gameService.joinGame(admin.getUserId(), gameRepository.findByGameId(game.getGameId()).getGameId()));
  }

  @Test
  public void leaveGame_noGame_throwsException() {

    // attempt to remove user that hasn't been created
    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> gameService.leaveRoom(1L, 34L));
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

  @Transactional
  @Test
  public void leaveGame_Success_BelowMaxPlayers_NoReassignAdmin() {

    // create admin and create 7 players
    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setStatus(UserStatus.ONLINE);

    admin = userService.createUser(admin);

    User player1 = new User();
    player1.setNickname("testNickname");
    player1.setCreationDate(LocalDate.now());
    player1.setToken("test token");
    player1.setStatus(UserStatus.ONLINE);

    player1 = userService.createUser(player1);

    User player2 = new User();
    player2.setNickname("testNickname");
    player2.setCreationDate(LocalDate.now());
    player2.setToken("test token");
    player2.setStatus(UserStatus.ONLINE);

    player2 = userService.createUser(player2);

    User player3 = new User();
    player3.setNickname("testNickname");
    player3.setCreationDate(LocalDate.now());
    player3.setToken("test token");
    player3.setStatus(UserStatus.ONLINE);

    player3 = userService.createUser(player3);

    User player4 = new User();
    player4.setNickname("testNickname");
    player4.setCreationDate(LocalDate.now());
    player4.setToken("test token");
    player4.setStatus(UserStatus.ONLINE);

    player4 = userService.createUser(player4);

    User player5 = new User();
    player5.setNickname("testNickname");
    player5.setCreationDate(LocalDate.now());
    player5.setToken("test token");
    player5.setStatus(UserStatus.ONLINE);

    player5 = userService.createUser(player5);

    User player6 = new User();
    player6.setNickname("testNickname");
    player6.setCreationDate(LocalDate.now());
    player6.setToken("test token");
    player6.setStatus(UserStatus.ONLINE);

    player6 = userService.createUser(player6);

    User player7 = new User();
    player7.setNickname("testNickname");
    player7.setCreationDate(LocalDate.now());
    player7.setToken("test token");
    player7.setStatus(UserStatus.ONLINE);

    player7 = userService.createUser(player7);

    Game game = gameService.createGame(admin.getUserId());
    game = gameService.joinGame(game.getGamePin(), player1.getUserId());
    game = gameService.joinGame(game.getGamePin(), player2.getUserId());
    game = gameService.joinGame(game.getGamePin(), player3.getUserId());
    game = gameService.joinGame(game.getGamePin(), player4.getUserId());
    game = gameService.joinGame(game.getGamePin(), player5.getUserId());
    game = gameService.joinGame(game.getGamePin(), player6.getUserId());
    game = gameService.joinGame(game.getGamePin(), player7.getUserId());

    // check if game status is correctly CLOSED
    assertEquals(game.getStatus(), GameStatus.CLOSED);

    // call function should remove user from users and set status back to OPEN
    gameService.leaveRoom(game.getGameId(), player7.getUserId());

    assertEquals(game.getUsers().size(), 7);
    assertEquals(game.getStatus(), GameStatus.OPEN);

  }

  @Test
  public void leaveGame_Success_DeleteGame() {
    User admin = new User();
    admin.setNickname("test nickname");

    admin = userService.createUser(admin);

    Game game = gameService.createGame(admin.getUserId());

    gameService.leaveRoom(game.getGameId(), admin.getUserId());
    Long id = game.getGameId();

    assertThrows(ResponseStatusException.class, () -> gameService.getGame(id));
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
  public void cleanUpGame_ActivityToday(){
    User admin = new User();
    admin.setNickname("test nickname");
    admin = userService.createUser(admin);

    Game game = gameService.createGame(admin.getUserId());
    game.setLastActivity(Instant.now());

    User player1 = new User();
    player1.setNickname("test nickname");
    player1 = userService.createUser(player1);

    gameService.gameroomCleanUp(game.getGameId());
    assertEquals(game.getStatus(), GameStatus.OPEN);
  }

  @Test
  public void cleanUpGame_ActivityBeforeYesterday(){
    User admin = new User();
    admin.setNickname("test nickname");
    admin = userService.createUser(admin);

    Game game = gameService.createGame(admin.getUserId());
    game.setLastActivity(Instant.now().minus(Duration.ofDays(2)));

    User player1 = new User();
    player1.setNickname("test nickname");
    player1 = userService.createUser(player1);

    gameService.gameroomCleanUp(game.getGameId());
    assertNotNull(game.getLastActivity());
    assertTrue(game.getLastActivity().isBefore(Instant.now().minus(Duration.ofDays(1))));
    assertEquals(gameRepository.findByGameId(game.getGameId()).getStatus(), GameStatus.CLOSED);
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

  @Transactional
  @Test
  public void createTextPrompt_Success_V2() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin = userService.createUser(admin);

    User player = new User();
    player.setNickname("testNickname");
    player = userService.createUser(player);

    Game game = gameService.createGame(admin.getUserId());
    gameService.joinGame(game.getGamePin(), player.getUserId());

    gameService.createGameSession(game.getGameId());

    Drawing drawing = new Drawing();
    drawing.setEncodedImage("encoded image");
    drawing.setCreationDateTime(LocalDateTime.now());

    Drawing createdDrawing = drawingRepository.save(drawing);
    drawingRepository.flush();

    // call function should reassign admin role to createdPlayer
    TextPrompt text = gameService.createTextPrompt(game.getGameSessions().get(0).getGameSessionId(), admin.getUserId(), 777L, "test content");
    TextPrompt text2 = gameService.createTextPrompt(game.getGameSessions().get(0).getGameSessionId(), player.getUserId(), 777L, "test content");
    text.setAssignedTo(player.getUserId());
    gameRepository.flush();
    entityManager.flush();

    assertEquals(text.getContent(), "test content");
    assertEquals(text.getPreviousDrawingId(), 777L);
    assertEquals(text.getCreator(), admin);
    assertEquals(game.getGameSessions().get(0).getGameLoopStatus(), GameLoopStatus.DRAWING);
  }

  @Transactional
  @Test
  public void createTextPrompt_Success_PRESENTATION() {

    User admin = new User();
    admin.setNickname("testNickname");
    admin = userService.createUser(admin);

    User player = new User();
    player.setNickname("testNickname");
    player = userService.createUser(player);

    Game game = gameService.createGame(admin.getUserId());
    gameService.joinGame(game.getGamePin(), player.getUserId());

    gameService.createGameSession(game.getGameId());

    Drawing drawing = new Drawing();
    drawing.setEncodedImage("encoded image");
    drawing.setCreationDateTime(LocalDateTime.now());

    Drawing createdDrawing = drawingRepository.save(drawing);
    drawingRepository.flush();

    game.getGameSessions().get(0).setGameLoopStatus(GameLoopStatus.PRESENTATION);

    // call function should reassign admin role to createdPlayer
    TextPrompt text = gameService.createTextPrompt(game.getGameSessions().get(0).getGameSessionId(), admin.getUserId(), 777L, "test content");
    TextPrompt text2 = gameService.createTextPrompt(game.getGameSessions().get(0).getGameSessionId(), player.getUserId(), 777L, "test content");
    text.setAssignedTo(player.getUserId());
    gameRepository.flush();
    entityManager.flush();

    assertEquals(text.getContent(), "test content");
    assertEquals(text.getPreviousDrawingId(), 777L);
    assertEquals(text.getCreator(), admin);;
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

    assertThrows(ResponseStatusException.class, () -> gameService.increasePromptVotes(2L, 1L, 1L));

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

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setToken("test token");

    gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    assertThrows(ResponseStatusException.class, () -> gameService.increasePromptVotes(gameSession.getGameSessionId(), 178L, admin.getUserId()));

  }

  @Transactional
  @Test
  public void increaseTextPromptVotes_Success() {

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

    gameService.increasePromptVotes(createdGameSession.getGameSessionId(), createdText.getTextPromptId(), admin.getUserId());
    entityManager.flush();

    assertEquals(createdText.getNumVotes(), 1);
  }

  @Test
  public void increaseDrawingVotes_noGameSession_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.increaseDrawingVotes(2L, 1L, 1L));

  }

  @Test
  public void increaseDrawingVotes_noDrawing_throwsException() {

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

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setToken("test token");

    gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    assertThrows(ResponseStatusException.class, () -> gameService.increasePromptVotes(gameSession.getGameSessionId(), 178L, admin.getUserId()));

  }

  @Transactional
  @Test
  public void increaseDrawingVotes_Success() {

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

    Drawing drawing = new Drawing();
    drawing.setEncodedImage("test content");
    drawing.setCreator(createdPlayer);
    drawing.setGameSessionId(createdGameSession.getGameSessionId());
    drawing.setCreationDateTime(LocalDateTime.now());
    drawing.setNumVotes(5);

    drawingRepository.save(drawing);
    drawingRepository.flush();

    gameService.increaseDrawingVotes(createdGameSession.getGameSessionId(), drawing.getDrawingId(), admin.getUserId());;
    entityManager.flush();

    assertEquals(drawing.getNumVotes(), 6);
  }

  @Test
  public void decreaseTextPromptVotes_noGameSession_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.decreasePromptVotes(2L, 1L, 1L));

  }

  @Test
  public void decreasePromptVotes_noTextPrompt_throwsException() {

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

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setToken("test token");

    gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    assertThrows(ResponseStatusException.class, () -> gameService.decreasePromptVotes(gameSession.getGameSessionId(), 178L, 1L));

  }

  @Transactional
  @Test
  public void decreaseTextPromptVotes_Success() {

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
    text.setNumVotes(2);

    TextPrompt createdText = textPromptRepository.save(text);
    textPromptRepository.flush();

    gameService.decreasePromptVotes(createdGameSession.getGameSessionId(), createdText.getTextPromptId(), admin.getUserId());;
    entityManager.flush();

    assertEquals(createdText.getNumVotes(), 1);
  }

  @Test
  public void decreaseDrawingVotes_noGameSession_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.decreaseDrawingVotes(2L, 1L, 1L));

  }

  @Test
  public void decreaseDrawingVotes_noDrawing_throwsException() {

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

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDate.now());
    gameSession.setStatus(GameStatus.IN_PLAY);
    gameSession.setToken("test token");

    gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    assertThrows(ResponseStatusException.class, () -> gameService.decreasePromptVotes(gameSession.getGameSessionId(), 178L, admin.getUserId()));

  }

  @Transactional
  @Test
  public void decreaseDrawingVotes_Success() {

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

    Drawing drawing = new Drawing();
    drawing.setEncodedImage("test content");
    drawing.setCreator(createdPlayer);
    drawing.setGameSessionId(createdGameSession.getGameSessionId());
    drawing.setCreationDateTime(LocalDateTime.now());
    drawing.setNumVotes(5);

    drawingRepository.save(drawing);
    drawingRepository.flush();

    gameService.decreaseDrawingVotes(createdGameSession.getGameSessionId(), drawing.getDrawingId(), admin.getUserId());;
    entityManager.flush();

    assertEquals(drawing.getNumVotes(), 4);
  }

  @Test
  public void getTopThreeTextPrompts_noGameSession_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.getTopThreeTextPrompts(2L));

  }

  @Transactional
  @Test
  public void getTopThreeTextPrompts_Success() {

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

    TextPrompt text2 = new TextPrompt();
    text2.setContent("test content");
    text2.setCreator(createdAdmin);
    text2.setGameSession(createdGameSession);

    TextPrompt createdText2 = textPromptRepository.save(text2);
    textPromptRepository.flush();

    TextPrompt text3 = new TextPrompt();
    text3.setContent("test content");
    text3.setCreator(createdPlayer2);
    text3.setGameSession(createdGameSession);

    TextPrompt createdText3 = textPromptRepository.save(text3);
    textPromptRepository.flush();

    gameService.increasePromptVotes(createdGameSession.getGameSessionId(), createdText.getTextPromptId(), admin.getUserId());
    gameService.increasePromptVotes(createdGameSession.getGameSessionId(), createdText2.getTextPromptId(), createdPlayer.getUserId());
    gameService.increasePromptVotes(createdGameSession.getGameSessionId(), createdText2.getTextPromptId(), createdPlayer2.getUserId());

    List <TextPrompt> topThree = gameService.getTopThreeTextPrompts(createdGameSession.getGameSessionId());
    entityManager.flush();

    assertEquals(topThree.get(0), createdText2);
    assertEquals(topThree.get(1), createdText);
    assertEquals(topThree.size(), 2);
    assertEquals(createdGameSession.getGameLoopStatus(), GameLoopStatus.LEADERBOARD);
  }

  @Test
  public void getTopThreeDrawings_noGameSession_throwsException() {

    assertThrows(ResponseStatusException.class, () -> gameService.getTopThreeDrawings(2L));

  }

  @Transactional
  @Test
  public void getTopThreeDrawings_Success() {

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
    gameSession.setRoundCounter(6);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    game.setGameSessions(gameSessions);

    Drawing drawing = new Drawing();
    drawing.setEncodedImage("test content");
    drawing.setCreator(createdPlayer);
    drawing.setGameSessionId(createdGameSession.getGameSessionId());
    drawing.setCreationDateTime(LocalDateTime.now());
    drawing.setNumVotes(5);

    Drawing createdDrawing = drawingRepository.save(drawing);
    drawingRepository.flush();

    Drawing drawing2 = new Drawing();
    drawing2.setEncodedImage("test content");
    drawing2.setCreator(createdAdmin);
    drawing2.setGameSessionId(createdGameSession.getGameSessionId());
    drawing2.setCreationDateTime(LocalDateTime.now());
    drawing2.setNumVotes(5);

    Drawing createdDrawing2 = drawingRepository.save(drawing2);
    drawingRepository.flush();

    Drawing drawing3 = new Drawing();
    drawing3.setEncodedImage("test content");
    drawing3.setCreator(createdPlayer2);
    drawing3.setGameSessionId(createdGameSession.getGameSessionId());
    drawing3.setCreationDateTime(LocalDateTime.now());
    drawing3.setNumVotes(5);

    Drawing createdDrawing3 = drawingRepository.save(drawing3);
    drawingRepository.flush();

    Drawing drawing4 = new Drawing();
    drawing4.setEncodedImage("test content");
    drawing4.setCreator(createdPlayer2);
    drawing4.setGameSessionId(createdGameSession.getGameSessionId());
    drawing4.setCreationDateTime(LocalDateTime.now());
    drawing4.setNumVotes(4);

    Drawing createdDrawing4 = drawingRepository.save(drawing4);
    drawingRepository.flush();

    gameService.increaseDrawingVotes(createdGameSession.getGameSessionId(), createdDrawing.getDrawingId(), admin.getUserId());
    gameService.increaseDrawingVotes(createdGameSession.getGameSessionId(), createdDrawing2.getDrawingId(), createdPlayer.getUserId());
    gameService.increaseDrawingVotes(createdGameSession.getGameSessionId(), createdDrawing2.getDrawingId(), createdPlayer2.getUserId());

    List <Drawing> topThree = gameService.getTopThreeDrawings(createdGameSession.getGameSessionId());
    entityManager.flush();

    assertEquals(topThree.get(0), createdDrawing2);
    assertEquals(topThree.get(1), createdDrawing);
    assertEquals(topThree.get(2), createdDrawing3);
    assertEquals(topThree.size(), 3);
    assertEquals(createdGameSession.getGameLoopStatus(), GameLoopStatus.LEADERBOARD);
  }

  @Transactional
  @Test
  public void getTopThreeDrawings_Success_EmpyList_NoVotes() {

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
    gameSession.setRoundCounter(6);

    GameSession createdGameSession = gameSessionRepository.save(gameSession);
    gameSessionRepository.flush();

    List<GameSession> gameSessions = new ArrayList<GameSession>();
    gameSessions.add(createdGameSession);

    game.setGameSessions(gameSessions);

    Drawing drawing = new Drawing();
    drawing.setEncodedImage("test content");
    drawing.setCreator(createdPlayer);
    drawing.setGameSessionId(createdGameSession.getGameSessionId());
    drawing.setCreationDateTime(LocalDateTime.now());

    Drawing createdDrawing = drawingRepository.save(drawing);
    drawingRepository.flush();

    Drawing drawing2 = new Drawing();
    drawing2.setEncodedImage("test content");
    drawing2.setCreator(createdAdmin);
    drawing2.setGameSessionId(createdGameSession.getGameSessionId());
    drawing2.setCreationDateTime(LocalDateTime.now());

    Drawing createdDrawing2 = drawingRepository.save(drawing2);
    drawingRepository.flush();

    Drawing drawing3 = new Drawing();
    drawing3.setEncodedImage("test content");
    drawing3.setCreator(createdPlayer2);
    drawing3.setGameSessionId(createdGameSession.getGameSessionId());
    drawing3.setCreationDateTime(LocalDateTime.now());

    Drawing createdDrawing3 = drawingRepository.save(drawing3);
    drawingRepository.flush();

    List <Drawing> topThree = gameService.getTopThreeDrawings(createdGameSession.getGameSessionId());
    entityManager.flush();

    assertEquals(createdDrawing.getNumVotes(), 0);
    assertEquals(createdDrawing2.getNumVotes(), 0);
    assertEquals(createdDrawing3.getNumVotes(), 0);
    assertEquals(topThree.size(), 0);
    assertEquals(createdGameSession.getGameLoopStatus(), GameLoopStatus.LEADERBOARD);
  }

  @Test
  public void openGame_NoGame_throwsException() {
  // with no game room should throw an error
    assertThrows(ResponseStatusException.class, () -> gameService.openGame(2L));
  }

  @Test
  public void openGame_ValidInputs_success() {
    // create admin and create 7 players
    User admin = new User();
    admin.setNickname("testNickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    admin.setStatus(UserStatus.ONLINE);

    admin = userService.createUser(admin);

    Game game = gameService.createGame(admin.getUserId());
    game.setStatus(GameStatus.CLOSED);

    // when
    gameService.openGame(game.getGameId());

    // then
    assertEquals(gameRepository.findByGameId(game.getGameId()).getStatus(), GameStatus.OPEN);
  }

}

