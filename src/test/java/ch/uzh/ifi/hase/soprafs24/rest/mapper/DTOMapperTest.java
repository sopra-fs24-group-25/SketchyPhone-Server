package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.GameLoopStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
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
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TextPromptDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
  @Test
  public void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setNickname("name");

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // check content
    assertEquals(userPostDTO.getNickname(), user.getNickname());
  }

  @Test
  public void testCreateUser_fromUserPostDTO_toUser_success_02() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setNickname("name");
    userPostDTO.setPassword("password");
    userPostDTO.setEmail("email@gmail.com");
    userPostDTO.setAvatarId(1L);

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // check content
    assertEquals(userPostDTO.getNickname(), user.getNickname());
    assertEquals(userPostDTO.getPassword(), user.getPassword());
    assertEquals(userPostDTO.getEmail(), user.getEmail());
    assertEquals(userPostDTO.getAvatarId(), user.getAvatarId());
  }

  @Test
  public void testGetUser_fromUser_toUserGetDTO_success() {
    // create User
    User user = new User();
    user.setNickname("Firstname Lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");

    // MAP -> Create UserGetDTO
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    // check content
    assertEquals(user.getUserId(), userGetDTO.getUserId());
    assertEquals(user.getNickname(), userGetDTO.getNickname());
    assertEquals(user.getStatus(), userGetDTO.getStatus());
  }

  @Test
  public void testCreateGame_fromGamePostDTO_toGame_success() {
    User admin = new User();
    admin.setUserId(1L);
    admin.setNickname("test name");

    // create GamePostDTO
    GamePostDTO gamePostDTO = new GamePostDTO();
    gamePostDTO.setAdmin(admin.getUserId());
    gamePostDTO.setGamePin(777777L);


    // MAP -> Create game
    Game game = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);

    // check content
    assertEquals(gamePostDTO.getAdmin(), game.getAdmin());
  }

  @Test
  public void testGetGame_fromGame_toGameGetDTO_success() {
    User admin = new User();
    admin.setUserId(1L);
    admin.setNickname("test name");

    // create Game
    Game game = new Game();
    game.setStatus(GameStatus.OPEN);
    game.setGamePin(777777L);
    game.setAdmin(admin.getUserId());

    // MAP -> Create GameGetDTO
    GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

    // check content
    assertEquals(game.getStatus().toString(), gameGetDTO.getStatus());
    assertEquals(game.getGamePin(), gameGetDTO.getGamePin());
    assertEquals(game.getAdmin(), gameGetDTO.getAdmin());
  }

  @Test
  public void testCreateGameSession_fromGameSessionDTO_toGameSessoion_success() {
    User admin = new User();
    admin.setUserId(1L);
    admin.setNickname("test name");

    List<Long> users = new ArrayList<Long>();
    users.add(admin.getUserId());

    // create GameSessionDTO
    GameSessionDTO gameSessionDTO = new GameSessionDTO();
    gameSessionDTO.setGameSessionId(1L);
    gameSessionDTO.setUsersInSession(users);
    gameSessionDTO.setStatus(GameStatus.IN_PLAY);

    // MAP -> Create gameSession
    GameSession gameSession = DTOMapper.INSTANCE.convertGameSessionDTOToEntity(gameSessionDTO);

    // check content
    assertEquals(gameSessionDTO.getGameSessionId(), gameSession.getGameSessionId());
    assertEquals(gameSessionDTO.getUsersInSession().size(), gameSession.getUsersInSession().size());
    assertEquals(gameSessionDTO.getStatus(), gameSession.getStatus());
  }

  @Test
  public void testGetGameSession_fromGameSession_toGameSessionGetDTO_success() {
    User admin = new User();
    admin.setUserId(1L);
    admin.setNickname("test name");

    // create GameSession
    GameSession gameSession = new GameSession();
    gameSession.setGameSessionId(1L);
    gameSession.setToken("Test token");
    gameSession.setRoundCounter(3);
    gameSession.setGameLoopStatus(GameLoopStatus.DRAWING);

    // MAP -> Create GameSessionGetDTO
    GameSessionGetDTO gameSessionGetDTO = DTOMapper.INSTANCE.convertEntityToGameSessionGetDTO(gameSession);

    // check content
    assertEquals(gameSession.getGameSessionId(), gameSessionGetDTO.getGameSessionId());
    assertEquals(gameSession.getToken(), gameSessionGetDTO.getToken());
    assertEquals(gameSession.getRoundCounter(), gameSessionGetDTO.getRoundCounter());
    assertEquals(gameSession.getGameLoopStatus(), gameSessionGetDTO.getGameLoopStatus());
  }

  @Test
  public void testCreateTextPrompt_fromTextPromptDTO_toTextPrompt_success() {
    User admin = new User();
    admin.setUserId(1L);
    admin.setNickname("test name");

    // create TextPromptDTO
    TextPromptDTO textPromptDTO = new TextPromptDTO();
    textPromptDTO.setContent("test content");
    textPromptDTO.setCreator(admin);
    textPromptDTO.setRound(2);

    // MAP -> Create textPrompt
    TextPrompt textPrompt = DTOMapper.INSTANCE.convertTextPromptDTOtoEntity(textPromptDTO);

    // check content
    assertEquals(textPromptDTO.getContent(), textPrompt.getContent());
    assertEquals(textPromptDTO.getcreator(), textPrompt.getCreator());
    assertEquals(textPromptDTO.getRound(), textPrompt.getRound());
  }

  @Test
  public void testGetTextPrompt_fromTextPrompt_toTextPromptDTO_success() {
    User admin = new User();
    admin.setUserId(1L);
    admin.setNickname("test name");

    GameSession gameSession = new GameSession();
    gameSession.setGameSessionId(1L);

    Drawing previous = new Drawing();
    previous.setDrawingId(2L);

    Drawing next = new Drawing();
    next.setDrawingId(3L);

    // create TextPrompt
    TextPrompt textPrompt = new TextPrompt();
    textPrompt.setAssignedTo(admin.getUserId());
    textPrompt.setGameSession(gameSession);
    textPrompt.setPreviousDrawingId(previous.getDrawingId());
    textPrompt.setNextDrawingId(next.getDrawingId());

    // MAP -> Create TextPromptDTO
    TextPromptDTO textPromptDTO = DTOMapper.INSTANCE.convertEntityToTextPromptDTO(textPrompt);

    // check content
    assertEquals(textPrompt.getAssignedTo(), textPromptDTO.getAssignedTo());
    assertEquals(textPrompt.getGameSession(), textPromptDTO.getGameSession());
    assertEquals(textPrompt.getPreviousDrawingId(), textPromptDTO.getPreviousDrawingId());
    assertEquals(textPrompt.getNextDrawingId(), textPromptDTO.getNextDrawingId());
  }

  @Test
  public void testCreateGameSettings_fromGameSettingsDTO_toGameSettings_success() {
    // create GameSettingsDTO
    GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
    gameSettingsDTO.setEnableTextToSpeech(true);
    gameSettingsDTO.setNumCycles(3);
    gameSettingsDTO.setGameSpeed(40);

    // MAP -> Create gameSettings
    GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOtoEntity(gameSettingsDTO);

    // check content
    assertEquals(gameSettingsDTO.getEnableTextToSpeech(), gameSettings.getEnableTextToSpeech());
    assertEquals(gameSettingsDTO.getNumCycles(), gameSettings.getNumCycles());
    assertEquals(gameSettingsDTO.getGameSpeed(), gameSettings.getGameSpeed());
  }

  @Test
  public void testGetGameSettings_fromGameSettings_toGameSettingstDTO_success() {
    // create TextPrompt
    GameSettings gameSettings = new GameSettings();
    gameSettings.setEnableTextToSpeech(true);
    gameSettings.setNumCycles(3);
    gameSettings.setGameSpeed(40);

    // MAP -> Create TextPromptDTO
    GameSettingsDTO gameSettingsDTO = DTOMapper.INSTANCE.convertEntityToGameSettingsDTO(gameSettings);
    // check content
    assertEquals(gameSettings.getEnableTextToSpeech(), gameSettingsDTO.getEnableTextToSpeech());
    assertEquals(gameSettings.getNumCycles(), gameSettingsDTO.getNumCycles());
    assertEquals(gameSettings.getGameSpeed(), gameSettingsDTO.getGameSpeed());
  }

  @Test
  public void testCreateAvatar_fromAvatarDTO_toAvatar_success() {
    User admin = new User();
    admin.setUserId(1L);
    admin.setNickname("test name");

    // create AvatarDTO
    AvatarDTO avatarDTO = new AvatarDTO();
    avatarDTO.setEncodedImage("test content");
    avatarDTO.setCreationDateTime(LocalDateTime.now());
    avatarDTO.setCreatorId(admin.getUserId());

    // MAP -> Create avatar
    Avatar avatar = DTOMapper.INSTANCE.convertAvatarDTOtoEntity(avatarDTO);

    // check content
    assertEquals(avatarDTO.getEncodedImage(), avatar.getEncodedImage());
    assertEquals(avatarDTO.getCreatorId(), avatar.getCreatorId());
    assertEquals(avatarDTO.getCreationDateTime(), avatar.getCreationDateTime());
  }

  @Test
  public void testGetAvatar_fromAvatar_toAvatarDTO_success() {
    User admin = new User();
    admin.setUserId(1L);
    admin.setNickname("test name");

    // create Avatar
    Avatar avatar = new Avatar();
    avatar.setEncodedImage("test content");
    avatar.setCreationDateTime(LocalDateTime.now());
    avatar.setCreatorId(admin.getUserId());

    // MAP -> Create avatarDTO
    AvatarDTO avatarDTO = DTOMapper.INSTANCE.convertEntityToAvatarDTO(avatar);

    // check content
    assertEquals(avatar.getEncodedImage(), avatarDTO.getEncodedImage());
    assertEquals(avatar.getCreatorId(), avatarDTO.getCreatorId());
    assertEquals(avatar.getEncodedImage(), avatarDTO.getEncodedImage());
  }

  @Test
  public void testCreateDrawing_fromDrawingDTO_toDrawing_success() {
    User admin = new User();
    admin.setUserId(1L);
    admin.setNickname("test name");

    GameSession gameSession = new GameSession();
    gameSession.setGameSessionId(1L);

    // create DrawingDTO
    DrawingDTO drawingDTO = new DrawingDTO();
    drawingDTO.setEncodedImage("test content");
    drawingDTO.setCreator(admin);
    drawingDTO.setCreationDateTime(LocalDateTime.now());;
    drawingDTO.setGameSessionId(gameSession.getGameSessionId());

    // MAP -> Create drawing
    Drawing drawing = DTOMapper.INSTANCE.convertDrawingDTOtoEntity(drawingDTO);

    // check content
    assertEquals(drawingDTO.getEncodedImage(), drawing.getEncodedImage());
    assertEquals(drawingDTO.getCreator(), drawing.getCreator());
    assertEquals(drawingDTO.getCreationDateTime(), drawing.getCreationDateTime());
    assertEquals(drawingDTO.getGameSessionId(), drawing.getGameSessionId());
  }

  @Test
  public void testGetDrawing_fromDrawing_toDrawingDTO_success() {
    User admin = new User();
    admin.setUserId(1L);
    admin.setNickname("test name");

    GameSession gameSession = new GameSession();
    gameSession.setGameSessionId(1L);

    TextPrompt previous = new TextPrompt();
    previous.setTextPromptId(2L);

    TextPrompt next = new TextPrompt();
    next.setTextPromptId(3L);

    // create Drawing
    Drawing drawing = new Drawing();
    drawing.setPreviousTextPrompt(previous.getTextPromptId());
    drawing.setNextTextPrompt(next.getTextPromptId());
    drawing.setAssignedTo(admin.getUserId());
    drawing.setRound(2);

    // MAP -> Create DrawingDTO
    DrawingDTO drawingDTO = DTOMapper.INSTANCE.convertEntityToDrawingDTO(drawing);

    // check content
    assertEquals(drawing.getAssignedTo(), drawingDTO.getAssignedTo());
    assertEquals(drawing.getRound(), drawingDTO.getRound());
    assertEquals(drawing.getPreviousTextPrompt(), drawingDTO.getPreviousTextPrompt());
    assertEquals(drawing.getNextTextPrompt(), drawingDTO.getNextTextPrompt());
  }
}
