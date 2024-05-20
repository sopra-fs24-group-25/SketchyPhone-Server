package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Avatar;
import ch.uzh.ifi.hase.soprafs24.entity.Drawing;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.AvatarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.DrawingDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TextPromptDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.SessionHistoryDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionGetDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ch.uzh.ifi.hase.soprafs24.entity.SessionHistory;

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

  @Mapping(source = "nickname", target = "nickname")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "persistent", target = "persistent")
  @Mapping(source = "avatarId", target = "avatarId")
  @Mapping(source = "username", target = "username")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "nickname", target = "nickname")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "persistent", target = "persistent")
  @Mapping(source = "avatarId", target = "avatarId")
  @Mapping(source = "role", target = "role")
  @Mapping(source = "token", target = "token")
  @Mapping(source = "gameRoom", target = "gameRoom")
  @Mapping(source = "username", target = "username")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "admin", target = "admin")
  @Mapping(source = "gamePin", target = "gamePin")
  Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

  @Mapping(source = "gameId", target = "gameId")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "admin", target = "admin")
  @Mapping(source = "gamePin", target = "gamePin")
  @Mapping(source = "gameSessions", target = "gameSessions")
  @Mapping(source = "gameSettingsId", target = "gameSettingsId")
  GameGetDTO convertEntityToGameGetDTO(Game game);

  @Mapping(source = "status", target = "status")
  @Mapping(source = "gameSessionId", target = "gameSessionId")
  @Mapping(source = "usersInSession", target = "usersInSession")
  GameSessionDTO gameSessionToGameSessionDTO(GameSession gameSession);

  @Mapping(source = "status", target = "status")
  @Mapping(source = "gameSessionId", target = "gameSessionId")
  @Mapping(source = "usersInSession", target = "usersInSession")
  GameSession convertGameSessionDTOToEntity(GameSessionDTO dto);

  @Mapping(source = "status", target = "status")
  @Mapping(source = "gameSessionId", target = "gameSessionId")
  @Mapping(source = "token", target = "token")
  @Mapping(source = "usersInSession", target = "usersInSession")
  @Mapping(source = "roundCounter", target = "roundCounter")
  @Mapping(source = "gameLoopStatus", target = "gameLoopStatus")
  GameSessionGetDTO convertEntityToGameSessionGetDTO(GameSession gameSession);

  @Mapping(source = "content", target = "content")
  @Mapping(source = "gameSession", target = "gameSession")
  @Mapping(source = "creator", target = "creator")
  @Mapping(source = "assignedTo", target = "assignedTo")
  @Mapping(source = "previousDrawingId", target = "previousDrawingId")
  @Mapping(source = "nextDrawingId", target = "nextDrawingId")
  @Mapping(source = "textPromptId", target = "textPromptId")
  @Mapping(source = "round", target = "round")
  @Mapping(source = "numVotes", target = "numVotes")
  TextPrompt convertTextPromptDTOtoEntity(TextPromptDTO textPromptDTO);

  @Mapping(source = "content", target = "content")
  @Mapping(source = "gameSession", target = "gameSession")
  @Mapping(source = "creator", target = "creator")
  @Mapping(source = "assignedTo", target = "assignedTo")
  @Mapping(source = "previousDrawingId", target = "previousDrawingId")
  @Mapping(source = "nextDrawingId", target = "nextDrawingId")
  @Mapping(source = "textPromptId", target = "textPromptId")
  @Mapping(source = "round", target = "round")
  @Mapping(source = "numVotes", target = "numVotes")
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

  @Mapping(source = "encodedImage", target = "encodedImage")
  @Mapping(source = "creator", target = "creator")
  @Mapping(source = "creationDateTime", target = "creationDateTime")
  @Mapping(source = "gameSessionId", target = "gameSessionId")
  @Mapping(source = "previousTextPrompt", target = "previousTextPrompt")
  @Mapping(source = "nextTextPrompt", target = "nextTextPrompt")
  @Mapping(source = "assignedTo", target = "assignedTo")
  @Mapping(source = "round", target = "round")
  @Mapping(source = "numVotes", target = "numVotes")
  Drawing convertDrawingDTOtoEntity(DrawingDTO drawingDTO);

  @Mapping(source = "encodedImage", target = "encodedImage")
  @Mapping(source = "creator", target = "creator")
  @Mapping(source = "creationDateTime", target = "creationDateTime")
  @Mapping(source = "gameSessionId", target = "gameSessionId")
  @Mapping(source = "previousTextPrompt", target = "previousTextPrompt")
  @Mapping(source = "nextTextPrompt", target = "nextTextPrompt")
  @Mapping(source = "assignedTo", target = "assignedTo")
  @Mapping(source = "round", target = "round")
  @Mapping(source = "numVotes", target = "numVotes")
  DrawingDTO convertEntityToDrawingDTO(Drawing drawing);

  @Mapping(source = "gameSession.gameSessionId", target = "gameSessionId")
  @Mapping(source = "userId", target = "userId")
  SessionHistoryDTO convertEntityToHistoryDTO(SessionHistory history);

  @Mapping(source = "gameSessionId", target = "gameSession.gameSessionId")
  @Mapping(source = "userId", target = "userId")
  SessionHistory convertHistoryDTOToEntity(SessionHistoryDTO dto);

}