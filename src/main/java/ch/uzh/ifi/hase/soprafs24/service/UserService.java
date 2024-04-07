package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Avatar;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.AvatarRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
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
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final AvatarRepository avatarRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository, AvatarRepository avatarRepository) {
    this.userRepository = userRepository;
    this.avatarRepository = avatarRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User getUser(Long id) {
    return this.userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
        "User not found."));
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.OFFLINE);
    newUser.setCreationDate(LocalDate.now());
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public User updateUser(long userId, User userInput){
    User oldUser = userRepository.findById(userId);

    if (oldUser == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
    }

    if (userInput.getName() != null){
      oldUser.setName(userInput.getName());
    }
    if (userInput.getPersistent() != null){
      oldUser.setPersistent(userInput.getPersistent());
    }
    if (userInput.getAvatarId() != null){
      oldUser.setAvatarId(userInput.getAvatarId());
    }
    if (userInput.getEmail() != null){
      oldUser.setEmail(userInput.getEmail());
    }
    if (userInput.getPassword() != null){
      oldUser.setPassword(userInput.getPassword());
    }

    return userRepository.save(oldUser);
  }

  public User getUserById(long id){
    User user = userRepository.findById(id);

    if (user == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    return user;
  }

  public boolean authenticateUser(String token, User user){
    if(user.getToken().equals(token)){
      return true;
    }
    else{
      return false;
    }
  }

  public Avatar createAvatar(Long creatorId, Avatar avatar){
    avatar.setCreationDateTime(LocalDateTime.now());
    avatar.setCreatorId(creatorId);
    avatar.setEncodedImage(Base64.getEncoder().encode(avatar.getEncodedImage()));
    Avatar updatedAvatar = avatarRepository.save(avatar);
    avatarRepository.flush();

    return updatedAvatar;
  }

  public Avatar getAvatar(long avatarId){
    Avatar avatar = avatarRepository.findById(avatarId);

    if (avatar == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Avatar not found.");
    }

    return avatar;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByName = userRepository.findByName(userToBeCreated.getName());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "name", "is"));
    }
  }

}

