package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Avatar;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @BeforeEach
  public void setup() {
    userRepository.deleteAll();
  }

  @Test
  public void createUser_validInputs_success() {

    User testUser = new User();
    testUser.setNickname("testNickname");
    testUser.setCreationDate(LocalDate.now());
    testUser.setStatus(UserStatus.ONLINE);

    // when
    User createdUser = userService.createUser(testUser);

    // then
    assertEquals(testUser.getUserId(), createdUser.getUserId());
    assertEquals(testUser.getNickname(), createdUser.getNickname());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
  }

  @Test
  public void getUsers_validInputs_success() {

    User testUser = new User();
    testUser.setNickname("testNickname");
    testUser.setCreationDate(LocalDate.now());
    testUser.setToken("test token");
    testUser.setStatus(UserStatus.ONLINE);

    User testUser2 = new User();
    testUser2.setNickname("testNickName2");
    testUser2.setCreationDate(LocalDate.now());
    testUser2.setToken("test token 2");

    userRepository.save(testUser);
    userRepository.save(testUser2);
    userRepository.flush();

    List<User> users = new ArrayList<User>();
    users.add(testUser);
    users.add(testUser2);

    // when
    List<User> userList = userService.getUsers();

    // then
    assertEquals(users.size(), userList.size());
  }

  @Test
  public void getUserById_validInputs_success() {

    User testUser = new User();
    testUser.setNickname("testNickname");
    testUser.setCreationDate(LocalDate.now());
    testUser.setToken("test token");
    testUser.setStatus(UserStatus.ONLINE);

    userRepository.save(testUser);
    userRepository.flush();

    // when
    User user = userService.getUserById(testUser.getUserId());

    // then
    assertEquals(user.getNickname(), testUser.getNickname());
    assertEquals(testUser.getCreationDate(), user.getCreationDate());
    assertEquals(testUser.getToken(), user.getToken());
    assertEquals(testUser.getStatus(), user.getStatus());
  }

  @Test
  public void updateUser_validInputs_success() {

    User testUser = new User();
    testUser.setNickname("testNickname");
    testUser.setCreationDate(LocalDate.now());
    testUser.setToken("test token");
    testUser.setStatus(UserStatus.ONLINE);

    userRepository.save(testUser);
    userRepository.flush();

    User update = new User();
    update.setNickname("newNickname");
    update.setPersistent(true);
    update.setEmail("testemail");

    // when
    User user = userService.updateUser(testUser.getUserId(), update);

    // then
    assertEquals(user.getNickname(), update.getNickname());
    assertEquals(testUser.getCreationDate(), user.getCreationDate());
    assertEquals(testUser.getToken(), user.getToken());
    assertEquals(testUser.getStatus(), user.getStatus());
    assertEquals(user.getPersistent(), true);
    assertEquals(user.getEmail(), update.getEmail());
  }

  @Test
  public void authenticateUser_wrongToken_throwsException() {

    User testUser = new User();
    testUser.setNickname("testNickname");
    testUser.setToken("testToken");
    testUser.setCreationDate(LocalDate.now());
    testUser.setStatus(UserStatus.ONLINE);
    User createdUser = userService.createUser(testUser);

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.authenticateUser("testToke", createdUser));
  }

  @Test
  public void createAvatar_validInputs_success() {

    User testUser = new User();
    testUser.setNickname("testNickname");
    testUser.setCreationDate(LocalDate.now());
    testUser.setToken("test token");
    testUser.setStatus(UserStatus.ONLINE);

    userRepository.save(testUser);
    userRepository.flush();

    // when
    Avatar avatar = userService.createAvatar(testUser.getUserId(), "test content");

    // then
    assertEquals(testUser.getUserId(), avatar.getCreatorId());
    assertEquals(avatar.getEncodedImage(), "test content");
    assertNotNull(avatar.getCreationDateTime());
    assertNotNull(avatar.getAvatarId());
  }

  @Test
  public void getAvatar_validInputs_success() {

    User testUser = new User();
    testUser.setNickname("testNickname");
    testUser.setCreationDate(LocalDate.now());
    testUser.setToken("test token");
    testUser.setStatus(UserStatus.ONLINE);

    userRepository.save(testUser);
    userRepository.flush();

    Avatar avatar = userService.createAvatar(testUser.getUserId(), "test content");

    // when
    Avatar foundAvatar = userService.getAvatar(avatar.getAvatarId());

    // then
    assertEquals(testUser.getUserId(), foundAvatar.getCreatorId());
    assertEquals(foundAvatar.getEncodedImage(), "test content");
    assertNotNull(foundAvatar.getCreationDateTime());
    assertNotNull(foundAvatar.getAvatarId());
  }

  // @Test
  // public void createUser_duplicateNickname_throwsException() {

  //   User testUser = new User();
  //   testUser.setNickname("testNickname");
  //   testUser.setCreationDate(LocalDate.now());
  //   testUser.setStatus(UserStatus.ONLINE);
  //   User createdUser = userService.createUser(testUser);

  //   // attempt to create second user with same name
  //   User testUser2 = new User();
  //   testUser2.setNickname("testNickname");
  //   testUser.setCreationDate(LocalDate.now());
  //   testUser.setStatus(UserStatus.ONLINE);

  //   // check that an error is thrown
  //   assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  // }
}
