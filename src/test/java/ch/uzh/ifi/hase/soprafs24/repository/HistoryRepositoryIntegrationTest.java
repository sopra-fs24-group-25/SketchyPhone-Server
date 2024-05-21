package ch.uzh.ifi.hase.soprafs24.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ch.uzh.ifi.hase.soprafs24.entity.SessionHistory;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class HistoryRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private HistoryRepository historyRepository;

  @Test
  public void findByGameSession_GameSessionId_success() {
    // given
    GameSession gameSession = new GameSession();
    gameSession.setCreationDate(LocalDateTime.now());
    gameSession.setToken("test token");
    gameSession.setStatus(GameStatus.IN_PLAY);    

    entityManager.persist(gameSession); // Persist the GameSession entity
    entityManager.flush();

    // when
    List<SessionHistory> found = historyRepository.findByGameSession_GameSessionId(gameSession.getGameSessionId());

    // then
    assertNotNull(found);
    assertEquals(0, found.size());
  }

}