package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.GameLoopStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Drawing;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TextPromptDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;

import org.hibernate.internal.util.xml.DTDEntityResolver;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

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
}
