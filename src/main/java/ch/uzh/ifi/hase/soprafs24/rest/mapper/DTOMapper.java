package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TextPromptDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "admin", target = "admin")
  @Mapping(source = "gamePin", target = "gamePin")
  Game convertGamePostDTOtoEntity(GamePostDTO GamePostDTO);

  @Mapping(source = "gameId", target = "gameId")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "admin", target = "admin")
  @Mapping(source = "gamePin", target = "gamePin")
  GameGetDTO convertEntityToGameGetDTO(Game game);

  @Mapping(source = "gameSpeed", target = "gameSpeed")
  @Mapping(source = "numCycles", target = "numCycles")
  @Mapping(source = "enableTextToSpeech", target = "enableTextToSpeech")
  GameSettings convertGameSettingsPostDTOtoEntity(GameSettingsPostDTO GameSettingsPostDTO);

  @Mapping(source = "gameSpeed", target = "gameSpeed")
  @Mapping(source = "numCycles", target = "numCycles")
  @Mapping(source = "enableTextToSpeech", target = "enableTextToSpeech")
  GameSettingsGetDTO convertEntityToGameSettingsGetDTO(GameSettings gameSettings);

  @Mapping(source = "content", target = "content")
  TextPrompt convertTextPromptDTOtoEntity(TextPromptDTO textPromptDTO);

  @Mapping(source = "content", target = "content")
  TextPromptDTO convertEntityToTextPromptDTO(TextPrompt textPrompt);

}