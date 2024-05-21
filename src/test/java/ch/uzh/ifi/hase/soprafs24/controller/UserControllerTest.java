package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Avatar;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.assertTrue;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.mockito.Mockito.when; // Import the necessary Mockito package


/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private UserRepository userRepository;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setNickname("Firstname Lastname");
    user.setStatus(UserStatus.OFFLINE);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].nickname", is(user.getNickname())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setUserId(1L);
    user.setNickname("Test User");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setNickname("Test User");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId", is(user.getUserId().intValue())))
        .andExpect(jsonPath("$.nickname", is(user.getNickname())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  public void signUpUser_validInput_userSignedUp() throws Exception {
      // given
      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setUsername("Test User");
      userPostDTO.setPassword("password");

      User createdUser = new User();
      createdUser.setUserId(1L);
      createdUser.setUsername(userPostDTO.getUsername());
      createdUser.setStatus(UserStatus.OFFLINE);
      createdUser.setPersistent(true);
      createdUser.setToken("1");

      given(userService.signUpUser(Mockito.any())).willReturn(createdUser);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder postRequest = post("/signUp")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPostDTO));

      // then
      mockMvc.perform(postRequest)
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.userId").exists())
          .andExpect(jsonPath("$.nickname").doesNotExist()) // Assuming it's null or not set in the response
          .andExpect(jsonPath("$.status").value("OFFLINE"));
  }

  @Test
  public void signUpUser_invalidInput_UsernameAlreadyExists() throws Exception {
      // given
      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setUsername("Test User");
      userPostDTO.setPassword("password");

      User createdUser = new User();
      createdUser.setUserId(1L);
      createdUser.setUsername(userPostDTO.getUsername());
      createdUser.setStatus(UserStatus.OFFLINE);
      createdUser.setPersistent(true);
      createdUser.setToken("1");

      given(userService.signUpUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists."));

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder postRequest = post("/signUp")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPostDTO));

      // then
      mockMvc.perform(postRequest)
          .andExpect(status().isConflict());   
}

  @Test
  public void loginUser_validInput_userLoggedIn() throws Exception {
    // given
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("Test User");
    userPostDTO.setPassword("password");

    User loggedInUser = new User();
    loggedInUser.setUserId(1L);
    loggedInUser.setUsername(userPostDTO.getUsername());
    loggedInUser.setStatus(UserStatus.ONLINE);
    loggedInUser.setToken("1");

    given(userService.loginUser(Mockito.any())).willReturn(loggedInUser);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/logIn")
    .contentType(MediaType.APPLICATION_JSON)
    .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").exists())
        .andExpect(jsonPath("$.nickname").doesNotExist()) // Assuming it's null or not set in the response
        .andExpect(jsonPath("$.status").value("ONLINE"));
}

  @Test
  public void loginUser_invalidInput_UserNotFound() throws Exception {
      // given
      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setUsername("Test User");
      userPostDTO.setPassword("password");

      given(userService.loginUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder postRequest = post("/logIn")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPostDTO));

      // then
      mockMvc.perform(postRequest)
          .andExpect(status().isNotFound());

  }
    
  @Test
  public void logoutUser_validInput_userLoggedOut() throws Exception {
      // given
      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setUsername("Test User");
      userPostDTO.setPassword("password");

      User loggedInUser = new User();
      loggedInUser.setUserId(1L);
      loggedInUser.setUsername(userPostDTO.getUsername());
      loggedInUser.setStatus(UserStatus.ONLINE);
      loggedInUser.setToken("1");

      given(userService.loginUser(Mockito.any())).willReturn(loggedInUser);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder postRequest = post("/logOut")
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", "1");

      // then
      mockMvc.perform(postRequest)
          .andExpect(status().isOk());

  }

  @Test
  public void deleteUser_validInput_userDeleted() throws Exception {
      // given
      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setUsername("Test User");
      userPostDTO.setPassword("password");

      User loggedInUser = new User();
      loggedInUser.setUserId(1L);
      loggedInUser.setUsername(userPostDTO.getUsername());
      loggedInUser.setStatus(UserStatus.ONLINE);
      loggedInUser.setToken("1");

      given(userService.loginUser(Mockito.any())).willReturn(loggedInUser);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder deleteRequest = delete("/users/{userId}", loggedInUser.getUserId())
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", "1")
      .header("X-User-ID", "1");

      // then
      mockMvc.perform(deleteRequest)
        .andExpect(status().isOk());
  }

  @Test
  public void updateUser_validInput() throws Exception {
    // given
    User user = new User();
    user.setUserId(1L);
    user.setNickname("Test User");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);
    
    User updatedUser = new User();
    updatedUser.setUserId(1L);
    updatedUser.setNickname("Test User");
    updatedUser.setToken("1");
    updatedUser.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setNickname("new nickname");

    given(userService.updateUser(Mockito.anyLong(), Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder putRequest = put(String.format("/users/%x", user.getUserId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(updatedUser.getUserId().intValue())))
        .andExpect(jsonPath("$.nickname", is(updatedUser.getNickname())))
        .andExpect(jsonPath("$.status", is(updatedUser.getStatus().toString())));
  }

  @Test
  public void getUser_validInput() throws Exception {
      // given

      User loggedInUser = new User();
      loggedInUser.setUserId(1L);
      loggedInUser.setUsername("test nickname");
      loggedInUser.setStatus(UserStatus.ONLINE);
      loggedInUser.setToken("1");

      given(userService.getUserById(Mockito.anyLong())).willReturn(loggedInUser);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder getRequest = get("/users/{userId}", loggedInUser.getUserId())
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", "1")
      .header("X-User-ID", "1");

      // then
      mockMvc.perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(loggedInUser.getUserId().intValue())))
        .andExpect(jsonPath("$.nickname", is(loggedInUser.getNickname())));
  }

  @Test
  public void createAvatar_validInput() throws Exception {
    // given
    User user = new User();
    user.setUserId(1L);
    user.setNickname("Test User");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);
    
    Avatar avatar = new Avatar();
    avatar.setAvatarId(1L);
    avatar.setEncodedImage("test encoded image");

    given(userService.createAvatar(Mockito.anyLong(), Mockito.any())).willReturn(avatar);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post(String.format("/users/%x/avatar/create", user.getUserId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content("test encoded image");

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.avatarId", is(avatar.getAvatarId().intValue())))
        .andExpect(jsonPath("$.encodedImage", is(avatar.getEncodedImage())));
  }

  @Test
  public void getAvatar_validInput() throws Exception {
    // given
    User user = new User();
    user.setUserId(1L);
    user.setNickname("Test User");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);
    
    Avatar avatar = new Avatar();
    avatar.setAvatarId(1L);
    avatar.setEncodedImage("test encoded image");

    given(userService.getAvatar(Mockito.anyLong())).willReturn(avatar);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get(String.format("/users/avatar/%x", avatar.getAvatarId()))
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "1")
        .header("X-User-ID", "1");

    // then
    mockMvc.perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.avatarId", is(avatar.getAvatarId().intValue())))
        .andExpect(jsonPath("$.encodedImage", is(avatar.getEncodedImage())));
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