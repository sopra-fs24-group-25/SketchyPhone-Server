package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Drawing;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
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

  @Test
  public void createGameRoomValidInput() throws Exception {
    // given
    Game game = new Game();
    game.setAdmin(2L);
    game.setGameId(1L);
    game.setGamePin(666666L);

    User user = new User();
    user.setName("Testuser");

    // this mocks the gameService -> we define above what the gameService should
    // return when getAllGames() is called
    given(gameService.createGame(Mockito.any())).willReturn(game);

    // when
    MockHttpServletRequestBuilder postRequest = post("/gameRooms/create")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(user));

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
    admin.setName("TestAdmin");

    User createdAdmin = userService.createUser(admin);

    List<User> users = new ArrayList<User>();
    users.add(createdAdmin);

    Game game = new Game();
    game.setAdmin(1L);
    game.setGameId(1L);
    game.setGamePin(666666L);
    game.setUsers(users);

    UserPostDTO user = new UserPostDTO();
    user.setName("Testuser");

    User newUser = new User();
    user.setName("TestUser");

    User createdNewUser = userService.createUser(newUser);

    users.add(createdNewUser);

    given(gameService.joinGame(Mockito.any(), Mockito.any())).willReturn(game);

    // when
    MockHttpServletRequestBuilder postRequest = post("/gameRooms/join/666666")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(user));

    // then
    mockMvc.perform(postRequest)
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.users", is(users)));
  }

  @Test
  public void leaveGameRoomValidInput() throws Exception {
    //given
    User admin = new User();
    admin.setName("TestAdmin");

    User createdAdmin = userService.createUser(admin);

    User newUser = new User();
    newUser.setName("Test User");
    newUser.setToken("Test token");
    newUser.setId(2L);

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
      .header("X-User-ID", String.valueOf(newUser.getId()));

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
    admin.setName("TestAdmin");

    User createdAdmin = userService.createUser(admin);

    User newUser = new User();
    newUser.setName("Test User");
    newUser.setToken("Test token");
    newUser.setId(2L);

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
      .header("X-User-ID", String.valueOf(newUser.getId()));

    // then
    mockMvc.perform(getRequest)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(2)));
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
    user.setName("TestUser");
    user.setId(1L);
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
    .header("X-User-ID", String.valueOf(user.getId()))
    .content(encodedImage);

    // then
    mockMvc.perform(postRequest)
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.creator.id", is(user.getId().intValue())))
      .andExpect(jsonPath("$.encodedImage", is(encodedImage)))
      .andExpect(jsonPath("$.previousTextPrompt", is(drawing.getPreviousTextPrompt().intValue())));
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