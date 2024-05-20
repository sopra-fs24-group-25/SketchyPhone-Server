package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Drawing;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TextPromptDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.Game.GameService;
import ch.uzh.ifi.hase.soprafs24.service.Game.HistoryService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * GameControllerTest
 * This is a WebMvcTest which allows to test the GameController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the GameController works.
 */
@WebMvcTest(GameController.class)
public class GameControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GameService gameService;

  @MockBean
  private UserService userService;

  @MockBean
  private HistoryService historyService;


  @Test
  public void createGameRoomValidInput() throws Exception {
    // given
    Game game = new Game();
    game.setAdmin(2L);
    game.setGameId(1L);
    game.setGamePin(666666L);

    User admin = new User();
    admin.setUserId(2L);
    admin.setNickname("Testuser");

    // this mocks the gameService -> we define above what the gameService should
    // return when getAllGames() is called
    given(gameService.createGame(Mockito.any())).willReturn(game);

    // when
    MockHttpServletRequestBuilder postRequest = post(String.format("/gameRooms/create/%x", admin.getUserId()))
      .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(postRequest)
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.admin", is(game.getAdmin().intValue())))
      .andExpect(jsonPath("$.gamePin", is(game.getGamePin().intValue())));
  }

  @Test
  public void joinGameRoomValidInput() throws Exception{
    //given
    User admin = new User();
    admin.setNickname("TestAdmin");

    User createdAdmin = userService.createUser(admin);

    List<User> users = new ArrayList<User>();
    users.add(createdAdmin);

    Game game = new Game();
    game.setAdmin(1L);
    game.setGameId(1L);
    game.setGamePin(666666L);
    game.setUsers(users);

    UserPostDTO user = new UserPostDTO();
    user.setNickname("Testuser");

    User newUser = new User();
    newUser.setUserId(1L);
    newUser.setNickname("TestUser");
    newUser.setToken("Test token");


    User createdNewUser = userService.createUser(newUser);

    users.add(createdNewUser);

    given(gameService.joinGame(Mockito.any(), Mockito.any())).willReturn(game);

    // when
    MockHttpServletRequestBuilder postRequest = post(String.format("/gameRooms/join/666666/%x", newUser.getUserId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", newUser.getToken())
      .header("X-User-ID", String.valueOf(newUser.getUserId()));

    // then
    mockMvc.perform(postRequest)
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.users", is(users)));
  }

  @Test
  public void leaveGameRoomValidInput() throws Exception {
    //given
    User admin = new User();
    admin.setNickname("TestAdmin");

    User createdAdmin = userService.createUser(admin);

    User newUser = new User();
    newUser.setNickname("Test User");
    newUser.setToken("Test token");
    newUser.setUserId(2L);

    User createdNewUser = userService.createUser(newUser);

    List<User> users = new ArrayList<User>();
    users.add(createdAdmin);
    users.add(createdNewUser);

    Game game = new Game();
    game.setAdmin(1L);
    game.setGameId(1L);
    game.setGamePin(666666L);
    game.setUsers(users);

    users.remove(1);

    // when
    MockHttpServletRequestBuilder deleteRequest = delete(String.format("/games/%x/leave/%x", 1L,2L))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", newUser.getToken())
      .header("X-User-ID", String.valueOf(newUser.getUserId()));

    // then
    mockMvc.perform(deleteRequest)
      .andExpect(status().isOk());
  }
  
  @Test
  public void getUsersInGameRoomValidInput() throws Exception {
    //given
    Game game = new Game();
    game.setAdmin(1L);
    game.setGameId(1L);
    game.setGamePin(666666L);
    

    User admin = new User();
    admin.setNickname("TestAdmin");

    User createdAdmin = userService.createUser(admin);

    User newUser = new User();
    newUser.setNickname("Test User");
    newUser.setToken("Test token");
    newUser.setUserId(2L);

    User createdNewUser = userService.createUser(newUser);

    List<User> users = new ArrayList<User>();
    users.add(createdAdmin);
    users.add(createdNewUser);

    game.setUsers(users);

    given(gameService.getGameRoomUsers(Mockito.any())).willReturn(users);


    // when
    MockHttpServletRequestBuilder getRequest = get(String.format("/gameRooms/%x/users", game.getGameId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", newUser.getToken())
      .header("X-User-ID", String.valueOf(newUser.getUserId()));

    // then
    mockMvc.perform(getRequest)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void getGameSettingsValidInput() throws Exception{
    //given
    Game game = new Game();
    game.setAdmin(1L);
    game.setGameId(1L);
    game.setGamePin(666666L);
    

    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(2L);

    GameSettings gameSettings = new GameSettings();
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setGameSettingsId(1L);
    gameSettings.setGameSpeed(40);
    gameSettings.setNumCycles(5);

    game.setGameSettingsId(gameSettings.getGameSettingsId());

    given(gameService.getGameSettings(Mockito.any())).willReturn(gameSettings);

    // when
    MockHttpServletRequestBuilder getRequest = get(String.format("/gameRooms/%x/settings", game.getGameId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(getRequest)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.enableTextToSpeech", is(true)))
      .andExpect(jsonPath("$.gameSpeed", is(40)))
      .andExpect(jsonPath("$.numCycles", is(5)));
  }

  @Test
  public void updateGameSettingsValidInput() throws Exception{
    //given
    Game game = new Game();
    game.setAdmin(1L);
    game.setGameId(1L);
    game.setGamePin(666666L);
    

    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(2L);

    GameSettings gameSettings = new GameSettings();
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setGameSettingsId(1L);
    gameSettings.setGameSpeed(40);
    gameSettings.setNumCycles(5);

    game.setGameSettingsId(gameSettings.getGameSettingsId());

    GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
    gameSettingsDTO.setEnableTextToSpeech(false);
    gameSettingsDTO.setGameSpeed(50);
    gameSettingsDTO.setNumCycles(4);

    given(gameService.updateGameSettings(Mockito.any(), Mockito.any())).willReturn(game);

    // when
    MockHttpServletRequestBuilder putRequest = put(String.format("/gameRooms/%x/settings", game.getGameId()))
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(gameSettingsDTO));

    // then
    mockMvc.perform(putRequest)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.gameSettingsId", is(gameSettings.getGameSettingsId().intValue())));
  }

  @Test
  public void createGameSessionValidInput() throws Exception{
    //given
    Game game = new Game();
    game.setAdmin(1L);
    game.setGameId(1L);
    game.setGamePin(666666L);
    

    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(2L);

    GameSession gameSession = new GameSession();
    gameSession.setGame(game);
    gameSession.setGameSessionId(1L);

    List<GameSession> listGameSessions = new ArrayList<GameSession>();
    listGameSessions.add(gameSession);

    game.setGameSessions(listGameSessions);

    given(gameService.createGameSession(Mockito.any())).willReturn(game);

    // when
    MockHttpServletRequestBuilder postRequest = post(String.format("/games/%x/start", game.getGameId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(postRequest)
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.gameSessions", hasSize(1)));
  }

  @Test
  public void createTextPromptValidInput() throws Exception{
    //given
    Game game = new Game();
    game.setAdmin(1L);
    game.setGameId(1L);
    game.setGamePin(666666L);
    

    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(2L);

    GameSession gameSession = new GameSession();
    gameSession.setGame(game);
    gameSession.setGameSessionId(1L);

    TextPromptDTO textPromptDTO = new TextPromptDTO();
    textPromptDTO.setTextPromptId(1L);
    textPromptDTO.setContent("Test content");

    // when
    MockHttpServletRequestBuilder postRequest = post(String.format("/games/%x/prompts/%x/%x", gameSession.getGameSessionId(), admin.getUserId(), 777L))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()))
      .content(asJsonString(textPromptDTO));

    // then
    mockMvc.perform(postRequest)
      .andExpect(status().isCreated());
  }

  @Test
  public void getTextPromptValidInput() throws Exception{
    //given
    Game game = new Game();
    game.setAdmin(1L);
    game.setGameId(1L);
    game.setGamePin(666666L);

    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);

    GameSession gameSession = new GameSession();
    gameSession.setGame(game);
    gameSession.setGameSessionId(1L);

    User user = new User();
    user.setUserId(2L);

    TextPrompt textPrompt = new TextPrompt();
    textPrompt.setCreator(user);
    textPrompt.setContent("Test content");
    textPrompt.setAssignedTo(admin.getUserId());

    given(gameService.getTextPrompt(Mockito.any(), Mockito.any())).willReturn(textPrompt);

    // when
    MockHttpServletRequestBuilder getRequest = get(String.format("/games/%x/prompts/%x", gameSession.getGameSessionId(), admin.getUserId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(getRequest)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.assignedTo", is(admin.getUserId().intValue())))
      .andExpect(jsonPath("$.content", is(textPrompt.getContent())));
  }

  @Test
  public void createDrawingValidInput() throws Exception{
    // given
    GameSession gameSession = new GameSession();
    gameSession.setGameSessionId(1L);
    gameSession.setStatus(GameStatus.IN_PLAY);

    TextPrompt previousTextPrompt = new TextPrompt();
    previousTextPrompt.setTextPromptId(1L);

    String encodedImage = "iVBORw0KGgoAAAANSUhEUgAAADwAAAA8AgMAAABHkjHhAAAACVBMVEX///8AAAAAgP9o0N6bAAAAb0lEQVR4nO3NwQ2AIAwF0JLAnQPsUzfgQC+OwDwuQacUUEtZQf2HJi+/aQH+vCGRsqYrXFF5Z+ZDbZfFkRo5iUNkruTF1pWaQdn2b9PG9jlNw1a8jSqI0a99WnpDl5975q5w2vUnMG0yECq3G/CNnKe3FUPOg+gYAAAAAElFTkSuQmCC";

    User user = new User();
    user.setNickname("TestUser");
    user.setUserId(1L);
    user.setToken("test token");

    User createdUser = userService.createUser(user);

    Drawing drawing = new Drawing();
    drawing.setCreator(user);
    drawing.setEncodedImage(encodedImage);
    drawing.setPreviousTextPrompt(previousTextPrompt.getTextPromptId());

    // mock behaviour

    given(gameService.createDrawing(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).willReturn(drawing);

    // when
    MockHttpServletRequestBuilder postRequest = post(String.format("/games/%x/drawings/%x/%x", 1L, 1L, 1L))
    .contentType(MediaType.APPLICATION_JSON)
    .header("Authorization", user.getToken())
    .header("X-User-ID", String.valueOf(user.getUserId()))
    .content(encodedImage);

    // then
    mockMvc.perform(postRequest)
      .andExpect(status().isCreated());
  }

  @Test
  public void getDrawingValidInput() throws Exception{
    //given
    Game game = new Game();
    game.setAdmin(1L);
    game.setGameId(1L);
    game.setGamePin(666666L);

    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);

    GameSession gameSession = new GameSession();
    gameSession.setGame(game);
    gameSession.setGameSessionId(1L);

    User user = new User();
    user.setUserId(2L);

    Drawing drawing = new Drawing();
    drawing.setAssignedTo(admin.getUserId());
    drawing.setCreator(user);
    drawing.setEncodedImage("iVBORw0KGgoAAAANSUhEUgAAADwAAAA8AgMAAABHkjHhAAAACVBMVEX///8AAAAAgP9o0N6bAAAAb0lEQVR4nO3NwQ2AIAwF0JLAnQPsUzfgQC+OwDwuQacUUEtZQf2HJi+/aQH+vCGRsqYrXFF5Z+ZDbZfFkRo5iUNkruTF1pWaQdn2b9PG9jlNw1a8jSqI0a99WnpDl5975q5w2vUnMG0yECq3G/CNnKe3FUPOg+gYAAAAAElFTkSuQmCC");

    given(gameService.getDrawing(Mockito.any(), Mockito.any())).willReturn(drawing);

    // when
    MockHttpServletRequestBuilder getRequest = get(String.format("/games/%x/drawings/%x", gameSession.getGameSessionId(), admin.getUserId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(getRequest)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.assignedTo", is(admin.getUserId().intValue())))
      .andExpect(jsonPath("$.encodedImage", is(drawing.getEncodedImage())));
  }

  @Test
  public void startNextRoundValidInput() throws Exception{
    //given
    Game game = new Game();
    game.setAdmin(1L);
    game.setGameId(1L);
    game.setGamePin(666666L);

    GameSession gameSession = new GameSession();
    gameSession.setGame(game);
    gameSession.setGameSessionId(1L);

    // when
    MockHttpServletRequestBuilder putRequest = put(String.format("/games/%x/nextround", gameSession.getGameSessionId()))
      .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(putRequest)
      .andExpect(status().isOk());
  }

  @Test
  public void getSequenceValidInput() throws Exception{
    //given
    Game game = new Game();
    game.setAdmin(1L);
    game.setGameId(1L);
    game.setGamePin(666666L);

    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);

    GameSession gameSession = new GameSession();
    gameSession.setGame(game);
    gameSession.setGameSessionId(1L);

    TextPrompt textPrompt = new TextPrompt();
    textPrompt.setTextPromptId(1L);
    textPrompt.setPreviousDrawingId(777L);

    Drawing drawing = new Drawing();
    drawing.setPreviousTextPrompt(textPrompt.getTextPromptId());

    List<Object> sequence = new ArrayList<>();
    sequence.add(textPrompt);
    sequence.add(drawing);

    given(gameService.getSequence(Mockito.any())).willReturn(sequence);

    // when
    MockHttpServletRequestBuilder getRequest = get(String.format("/games/%x/sequence", gameSession.getGameSessionId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(getRequest)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void getCurrentIndexValidInput() throws Exception{
    //given

    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);

    GameSession gameSession = new GameSession();
    gameSession.setGameSessionId(1L);

    int index = 0;

    given(gameService.getCurrentIndex(Mockito.any())).willReturn(index);

    // when
    MockHttpServletRequestBuilder getRequest = get(String.format("/games/%x/presentation/next", gameSession.getGameSessionId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(getRequest)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", is(index)));
  }

  @Test
  public void increaseCurrentIndexValidInput() throws Exception{
    //given

    User admin = new User();
    admin.setRole("admin");
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);

    GameSession gameSession = new GameSession();
    gameSession.setGameSessionId(1L);

    int index = 0;

    given(gameService.increaseCurrentIndex(Mockito.any())).willReturn(index+1);

    // when
    MockHttpServletRequestBuilder putRequest = put(String.format("/games/%x/presentation/next", gameSession.getGameSessionId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(putRequest)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", is(1)));
  }
  
  @Test
  public void getGameValidInput() throws Exception {
    // given
    Game game = new Game();
    game.setAdmin(2L);
    game.setGameId(1L);
    game.setGamePin(666666L);

    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);

    given(gameService.getGame(Mockito.any())).willReturn(game);

    // when
    MockHttpServletRequestBuilder getRequest = get(String.format("/games/%x", game.getGameId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(getRequest)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.admin", is(game.getAdmin().intValue())))
      .andExpect(jsonPath("$.gamePin", is(game.getGamePin().intValue())));
  }

  @Test
  public void increaseNumVotesTextPromptValidInput() throws Exception{
    //given

    User admin = new User();
    admin.setRole("admin");
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);

    GameSession gameSession = new GameSession();
    gameSession.setGameSessionId(1L);

    TextPrompt textPrompt = new TextPrompt();
    textPrompt.setTextPromptId(2L);

    // when
    MockHttpServletRequestBuilder putRequest = put(String.format("/games/%x/prompt/%x/vote", gameSession.getGameSessionId(), textPrompt.getTextPromptId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(putRequest)
      .andExpect(status().isOk());
  }

  @Test
  public void increaseNumVotesDrawingValidInput() throws Exception{
    //given

    User admin = new User();
    admin.setRole("admin");
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);

    GameSession gameSession = new GameSession();
    gameSession.setGameSessionId(1L);

    Drawing drawing = new Drawing();
    drawing.setDrawingId(2L);

    // when
    MockHttpServletRequestBuilder putRequest = put(String.format("/games/%x/drawing/%x/vote", gameSession.getGameSessionId(), drawing.getDrawingId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(putRequest)
      .andExpect(status().isOk());
  }

  @Test
  public void decreaseNumVotesTextPromptValidInput() throws Exception{
    //given

    User admin = new User();
    admin.setRole("admin");
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);

    GameSession gameSession = new GameSession();
    gameSession.setGameSessionId(1L);

    TextPrompt textPrompt = new TextPrompt();
    textPrompt.setTextPromptId(2L);

    // when
    MockHttpServletRequestBuilder putRequest = put(String.format("/games/%x/prompt/%x/unvote", gameSession.getGameSessionId(), textPrompt.getTextPromptId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(putRequest)
      .andExpect(status().isOk());
  }

  @Test
  public void decreaseNumVotesDrawingValidInput() throws Exception{
    //given

    User admin = new User();
    admin.setRole("admin");
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);

    GameSession gameSession = new GameSession();
    gameSession.setGameSessionId(1L);

    Drawing drawing = new Drawing();
    drawing.setDrawingId(2L);

    // when
    MockHttpServletRequestBuilder putRequest = put(String.format("/games/%x/drawing/%x/unvote", gameSession.getGameSessionId(), drawing.getDrawingId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(putRequest)
      .andExpect(status().isOk());
  }

  @Test
  public void savehistory_successful() throws Exception {
    // given
    Game game = new Game();
    game.setAdmin(2L);
    game.setGameId(1L);
    game.setGamePin(666666L);
    
    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);
    
    GameSession gameSession = new GameSession();
    gameSession.setGame(game);
    gameSession.setGameSessionId(1L);

    TextPrompt textPrompt = new TextPrompt();
    textPrompt.setTextPromptId(2L);

    Drawing drawing = new Drawing();
    drawing.setDrawingId(2L);

    // Create a list of objects to return
    List<Object> historyList = new ArrayList<>();
    historyList.add(textPrompt);
    historyList.add(drawing);

    given(historyService.saveHistory(Mockito.anyLong())).willReturn(historyList);

    // when
    MockHttpServletRequestBuilder postRequest = post(String.format("/games/%x/savehistory", gameSession.getGameSessionId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));
    
    // then
    mockMvc.perform(postRequest)
      .andExpect(status().isCreated());
   }
    
  @Test  
  public void getTopThreeTextPrompts() throws Exception {
    // given
    Game game = new Game();
    game.setAdmin(2L);
    game.setGameId(1L);
    game.setGamePin(666666L);
    
    GameSession gameSession = new GameSession();
    gameSession.setGameSessionId(1L);
    gameSession.setGame(game);
    
    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);
    
    TextPrompt t1 = new TextPrompt();
    t1.setContent("test");
    t1.setGameSession(gameSession);
    TextPrompt t2 = new TextPrompt();
    t2.setContent("test");
    t2.setNumVotes(1);
    t2.setGameSession(gameSession);
    TextPrompt t3 = new TextPrompt();
    t3.setContent("test");
    t3.setNumVotes(2);
    t3.setGameSession(gameSession);

    List<TextPrompt> texts = new ArrayList<TextPrompt>();
    texts.add(t3);
    texts.add(t2);
    texts.add(t1);

    given(gameService.getTopThreeTextPrompts(Mockito.any())).willReturn(texts);

    // when
    MockHttpServletRequestBuilder getRequest = get(String.format("/games/%x/top/text", gameSession.getGameSessionId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));
    
    // then
    mockMvc.perform(getRequest)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].numVotes", is(2)))
      .andExpect(jsonPath("$[1].numVotes", is(1)));
  }

  @Test
  public void gethistory_successful() throws Exception {
    // given
    Game game = new Game();
    game.setAdmin(2L);
    game.setGameId(1L);
    game.setGamePin(666666L);
    
    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);
    
    GameSession gameSession = new GameSession();
    gameSession.setGame(game);
    gameSession.setGameSessionId(1L);

    TextPrompt textPrompt = new TextPrompt();
    textPrompt.setTextPromptId(2L);

    Drawing drawing = new Drawing();
    drawing.setDrawingId(2L);

    // Create a list of objects to return
    List<Object> historyList = new ArrayList<>();
    historyList.add(textPrompt);
    historyList.add(drawing);

    given(historyService.getHistory(Mockito.anyLong())).willReturn(historyList);

    // when
    MockHttpServletRequestBuilder getRequest = get(String.format("/games/%x/history", gameSession.getGameSessionId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(getRequest)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void getTopThreeDrawings() throws Exception {
    // given
    Game game = new Game();
    game.setAdmin(2L);
    game.setGameId(1L);
    game.setGamePin(666666L);

    GameSession gameSession = new GameSession();
    gameSession.setGameSessionId(1L);
    gameSession.setGame(game);

    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);

    Drawing d1 = new Drawing();
    d1.setEncodedImage("test");
    d1.setGameSession(gameSession);
    Drawing d2 = new Drawing();
    d2.setEncodedImage("test");
    d2.setNumVotes(1);
    d2.setGameSession(gameSession);
    Drawing d3 = new Drawing();
    d3.setEncodedImage("test");
    d3.setNumVotes(2);
    d3.setGameSession(gameSession);

    List<Drawing> drawings = new ArrayList<Drawing>();
    drawings.add(d3);
    drawings.add(d2);
    drawings.add(d1);

    given(gameService.getTopThreeDrawings(Mockito.any())).willReturn(drawings);

    // when
    MockHttpServletRequestBuilder getRequest = get(String.format("/games/%x/top/drawing", gameSession.getGameSessionId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(getRequest)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].numVotes", is(2)))
      .andExpect(jsonPath("$[1].numVotes", is(1)));
  }

    
  @Test
  public void openGameValidInput() throws Exception {
    // given
    Game game = new Game();
    game.setAdmin(2L);
    game.setGameId(1L);
    game.setGamePin(666666L);
    game.setStatus(GameStatus.OPEN);

    User admin = new User();
    admin.setNickname("TestAdmin");
    admin.setToken("Test token");
    admin.setUserId(1L);

    gameService.openGame(Mockito.any());

    // when
    MockHttpServletRequestBuilder putRequest = put(String.format("/games/%x", game.getGameId()))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", admin.getToken())
      .header("X-User-ID", String.valueOf(admin.getUserId()));

    // then
    mockMvc.perform(putRequest)
      .andExpect(status().isOk());
  }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}