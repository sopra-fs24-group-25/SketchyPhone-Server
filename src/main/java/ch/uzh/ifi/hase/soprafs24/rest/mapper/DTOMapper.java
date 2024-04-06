package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Avatar;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.AvatarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TextPromptDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionGetDTO;
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
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "status", target = "status")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "admin", target = "admin")
  @Mapping(source = "gamePin", target = "gamePin")
  Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

  @Mapping(source = "gameId", target = "gameId")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "admin", target = "admin")
  @Mapping(source = "gamePin", target = "gamePin")
  @Mapping(source = "gameSessions", target = "gameSessions")
  GameGetDTO convertEntityToGameGetDTO(Game game);

  @Mapping(source = "status", target = "status")
  @Mapping(source = "gameSessionId", target = "gameSessionId")
  GameSessionDTO gameSessionToGameSessionDTO(GameSession gameSession);

  @Mapping(source = "status", target = "status")
  @Mapping(source = "gameSessionId", target = "gameSessionId")
  GameSession gameSessionDTOToGameSession(GameSessionDTO dto);

  @Mapping(source = "status", target = "status")
  @Mapping(source = "gameSessionId", target = "gameSessionId")
  @Mapping(source = "token", target = "token")
  GameSessionGetDTO gameSessionToGameSessionGetDTO(GameSession gameSession);

  @Mapping(source = "content", target = "content")
  @Mapping(source = "gameSession", target = "gameSession")
  @Mapping(source = "creator", target = "creator")
  TextPrompt convertTextPromptDTOtoEntity(TextPromptDTO textPromptDTO);

  @Mapping(source = "content", target = "content")
  @Mapping(source = "gameSession", target = "gameSession")
  @Mapping(source = "creator", target = "creator")
  TextPromptDTO convertEntityToTextPromptDTO(TextPrompt textPrompt);

  @Mapping(source = "gameSpeed", target = "gameSpeed")
  @Mapping(source = "numCycles", target = "numCycles")
  @Mapping(source = "enableTextToSpeech", target = "enableTextToSpeech")
  GameSettings convertGameSettingsDTOtoEntity(GameSettingsDTO GameSettingsDTO);

  @Mapping(source = "gameSpeed", target = "gameSpeed")
  @Mapping(source = "numCycles", target = "numCycles")
  @Mapping(source = "enableTextToSpeech", target = "enableTextToSpeech")
  GameSettingsDTO convertEntityToGameSettingsDTO(GameSettings gameSettings);

  @Mapping(source = "encodedImage", target = "encodedImage")
  @Mapping(source = "creatorId", target = "creatorId")
  @Mapping(source = "creationDateTime", target = "creationDateTime")
  Avatar convertAvatarDTOtoEntity(AvatarDTO avatarDTO);

  @Mapping(source = "encodedImage", target = "encodedImage")
  @Mapping(source = "creatorId", target = "creatorId")
  @Mapping(source = "creationDateTime", target = "creationDateTime")
  AvatarDTO convertEntityToAvatarDTO(Avatar avatar);
}