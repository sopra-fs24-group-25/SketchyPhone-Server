package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Avatar;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.AvatarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }
  // Get Mapping to get a list of all users  
  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<User> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return users;
  }

  // Post Mapping to create a user - when testing with Postman, the body should be a JSON object with the key "username", "nickname" and 'name' as the value
  // guest user
  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  // Put mapping to update existing user's profile
  // For guest users: the user can only update their nickname
  // For persistent users: the user can update their nickname, username, password

  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO updateUser(@PathVariable Long userId, @RequestBody UserPostDTO userPostDTO){
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    
    // update User
    User updatedUser = userService.updateUser(userId, userInput);

    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);

  }

  // Get mapping to update get existing's user's info
  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUser(@PathVariable Long userId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id){
    userService.authenticateUser(token, userService.getUserById(id));
    User user = userService.getUserById(userId);

    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

  }

  // Post mapping to create a custom avatar
  @PostMapping("/users/{userId}/avatar/create")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public Avatar createAvatar(@PathVariable Long userId, @RequestBody String avatarBase64){


    Avatar createdAvatar = userService.createAvatar(userId, avatarBase64);

    return createdAvatar;
  }

  // Get mapping to retrieve a certain avatar
  @GetMapping("/users/avatar/{avatarId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public AvatarDTO getAvatar(@PathVariable Long avatarId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long userId){
    userService.authenticateUser(token, userService.getUserById(userId));

    Avatar avatar = userService.getAvatar(avatarId);

    return DTOMapper.INSTANCE.convertEntityToAvatarDTO(avatar);
  }


  // Post mapping to signup a user
  // username is unique 
  // to test the endpoint with Postman, the body should be a JSON object with the key "username", "password" as the value
  // tested with Postman
  @PostMapping("/signUp")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO signUpUser(@RequestBody UserPostDTO userPostDTO){
    User userCredentials = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    User createdUser = userService.signUpUser(userCredentials);

    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }


  // Post mapping to login a user
  // to test the endpoint with Postman, the body should be a JSON object with the key "username", "password" as the value
  //tested with Postman
  @PostMapping("/logIn")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO){
    User userCredentials = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    User loggedInUser = userService.loginUser(userCredentials);

    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedInUser);
  }

  // Post mapping to logout a user
  @PostMapping("/logOut")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void logoutUser(@RequestHeader("Authorization")String token){
  
    userService.logoutUser(token);
  }

  // Delete mapping to delete a user
  @DeleteMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void deleteUser(@PathVariable Long userId, @RequestHeader("Authorization")String token, @RequestHeader("X-User-ID") Long id){
    userService.authenticateUser(token, userService.getUserById(id));
    User user = userService.getUserById(userId);

    userService.deleteUser(user);
  }
  

}