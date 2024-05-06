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

  // to do: post put and get mappings for persistent users! 
}