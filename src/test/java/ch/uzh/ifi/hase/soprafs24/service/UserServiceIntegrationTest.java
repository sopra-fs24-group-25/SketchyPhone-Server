package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
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
    testUser.setName("testName");
    testUser.setCreationDate(LocalDate.now());
    testUser.setStatus(UserStatus.ONLINE);

    // when
    User createdUser = userService.createUser(testUser);

    // then
    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateName_throwsException() {

    User testUser = new User();
    testUser.setName("testName");
    testUser.setCreationDate(LocalDate.now());
    testUser.setStatus(UserStatus.ONLINE);
    User createdUser = userService.createUser(testUser);

    // attempt to create second user with same name
    User testUser2 = new User();
    testUser2.setName("testName");
    testUser.setCreationDate(LocalDate.now());
    testUser.setStatus(UserStatus.ONLINE);

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  }
}
