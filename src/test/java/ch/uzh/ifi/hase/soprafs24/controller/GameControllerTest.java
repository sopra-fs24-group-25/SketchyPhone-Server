package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
  public void createGameRoom() throws Exception {
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
  public void joinGameRoom() throws Exception{
    //given
    User admin = new User();
    admin.setName("TestAdmin");
    admin.setId(2L);

    User createdAdmin = userService.createUser(admin);

    List<User> users = new ArrayList<User>();
    users.add(createdAdmin);

    Game game = new Game();
    game.setAdmin(1L);
    game.setGameId(1L);
    game.setUsers(null);
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
      .andExpect(jsonPath("$.users", is(game.getUsers())));
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