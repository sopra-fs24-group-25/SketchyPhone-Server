package ch.uzh.ifi.hase.soprafs24.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

@DataJpaTest
public class GameSessionRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private GameSessionRepository gameSessionRepository;

  @Test
  public void findByGameSessionId_success() {
    // given

    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDateTime.now());
    gameSession.setToken("test token");
    gameSession.setStatus(GameStatus.IN_PLAY);    

    entityManager.persist(gameSession);
    entityManager.flush();

    // when
    GameSession found = gameSessionRepository.findByGameSessionId(gameSession.getGameSessionId());

    // then
    assertNotNull(found.getGameSessionId());
    assertEquals(found.getCreationDate(), gameSession.getCreationDate());
    assertEquals(found.getToken(), gameSession.getToken());
    assertEquals(found.getStatus(), gameSession.getStatus());
  }
}
