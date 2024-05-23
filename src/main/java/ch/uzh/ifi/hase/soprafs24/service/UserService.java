package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Avatar;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.AvatarRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

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

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setCreationDate(LocalDate.now());
    if (newUser.getNickname() == null){
      throw new IllegalArgumentException("Nickname cannot be null");
    }
    // checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  // persistent user sign up
  public User signUpUser(User user){
    if (user.getUsername() == null){
      throw new IllegalArgumentException("Username cannot be null");
    }

    User existingUser = userRepository.findByUsername(user.getUsername());
    if (existingUser != null) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists.");
    }
    user.setPersistent(true);
    user.setToken(UUID.randomUUID().toString());
    user.setCreationDate(LocalDate.now());
    user.setStatus(UserStatus.ONLINE);

    user = userRepository.save(user);
    userRepository.flush();

    return user;
  }

  // persistent user sign in
  public User loginUser(User user){
    // Check if the username is provided
    if (user.getUsername() == null || user.getUsername().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty.");
    }

    // Check if the password is provided
    if (user.getPassword() == null || user.getPassword().isEmpty()) {
        throw new IllegalArgumentException("Password cannot be null or empty.");
    }

    // Find the user by username
    User userToLogin = userRepository.findByUsername(user.getUsername());

    if (userToLogin == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
    }

    if (userToLogin.getPassword() == null || !userToLogin.getPassword().equals(user.getPassword())){
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password incorrect.");
    }

    userToLogin.setToken(UUID.randomUUID().toString());
    userToLogin.setStatus(UserStatus.ONLINE);

    // Save the updated user (with token and status) to the database
    userToLogin = userRepository.save(userToLogin);
    userRepository.flush();

    // Return the authenticated user
    return userToLogin;
  }

  // check if user is persistent(exists in the database)
  public void checkIfUserExists(User user){

    User userToCheck = userRepository.findByUserId(user.getUserId());
    
    if (userToCheck == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
    }
    // Check if the user is persistent
    boolean isPersistent = userToCheck.getPersistent();
    
    if (!isPersistent){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not persistent.");
    }
  }

  // logout persistent user
  public void logoutUser(String token) {
    // Retrieve the user based on the provided token from the repository
    User currentUser = userRepository.findByToken(token);

    // Check if the user is found
    if (currentUser != null) {
        // Set the user status to offline and remove the token
        currentUser.setStatus(UserStatus.OFFLINE);

        // Save the changes to the repository
        userRepository.save(currentUser);
    } else {
        // Handle case when user is not found based on the provided token
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
    }
  }

  // delete persistent user
  public void deleteUser(User user){
    User userToDelete = userRepository.findByUserId(user.getUserId());

    if (userToDelete == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
    }

    userRepository.delete(userToDelete);
  }


  public User updateUser(long userId, User userInput){
    User oldUser = userRepository.findByUserId(userId);

    if (oldUser == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
    }

    if (userInput.getNickname() != null){
      oldUser.setNickname(userInput.getNickname());
    }
    if (userInput.getPersistent() != null){
      oldUser.setPersistent(userInput.getPersistent());
    }
    if (userInput.getAvatarId() != null){
      oldUser.setAvatarId(userInput.getAvatarId());
    }
    if (userInput.getUsername() != null){
      oldUser.setUsername(userInput.getUsername());
    }
    if (userInput.getPassword() != null){
      oldUser.setPassword(userInput.getPassword());
    }

    return userRepository.save(oldUser);
  }

  public User getUserById(long id){
    User user = userRepository.findByUserId(id);

    if (user == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    return user;
  }

  public void authenticateUser(String token, User user){
    if(!user.getToken().equals(token)){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }

  public Avatar createAvatar(Long creatorId, String avatarBase64){
    Avatar avatar = new Avatar();
    avatar.setCreationDateTime(LocalDateTime.now());
    avatar.setCreatorId(creatorId);

    String base64String = avatarBase64;

    int paddingLength = 4 - (base64String.length() % 4);
    
    // Add padding characters if necessary
    if (paddingLength != 4) {
        StringBuilder paddedString = new StringBuilder(base64String);
        for (int i = 0; i < paddingLength; i++) {
            paddedString.append('=');
        }
        base64String = paddedString.toString();
    } 

    avatar.setEncodedImage(base64String);

    Avatar updatedAvatar = avatarRepository.save(avatar);
    avatarRepository.flush();

    return updatedAvatar;
  }

  public Avatar getAvatar(long avatarId){
    Avatar avatar = avatarRepository.findByAvatarId(avatarId);

    if (avatar == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Avatar not found.");
    }

    return avatar;
  }

  public List<Avatar> getAllAvatars() {
    return avatarRepository.findAll();
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

  // no need to use this since users could have the same values for every field
  // private void checkIfUserExists(User userToBeCreated) {
  //   User userByNickname = userRepository.findByNickname(userToBeCreated.getNickname());

  //   String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
  //   if (userByNickname != null) {
  //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "name", "is"));
  //   }
  // }

}

