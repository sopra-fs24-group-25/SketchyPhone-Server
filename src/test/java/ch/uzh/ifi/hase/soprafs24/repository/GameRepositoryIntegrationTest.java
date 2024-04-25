package ch.uzh.ifi.hase.soprafs24.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class GameRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private GameRepository gameRepository;

  @Test
  public void findByGamePin_success() {
    // given
    User admin = new User();
    admin.setUserId(1L);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setAdmin(admin.getUserId());
    game.setGameSettingsId(1L);
    game.setGameToken("Test Token");
    game.setStatus(GameStatus.OPEN);
    

    entityManager.persist(game);
    entityManager.flush();

    // when
    Game found = gameRepository.findByGamePin(game.getGamePin());

    // then
    assertNotNull(found.getGameId());
    assertEquals(found.getGamePin(), game.getGamePin());
    assertEquals(found.getAdmin(), admin.getUserId());
    assertEquals(found.getStatus(), game.getStatus());
  }

  @Test
  public void findByGameId_success() {
    // given
    User admin = new User();
    admin.setUserId(1L);

    Game game = new Game();
    game.setGamePin(777777L);
    game.setAdmin(admin.getUserId());
    game.setGameSettingsId(1L);
    game.setGameToken("Test Token");
    game.setStatus(GameStatus.OPEN);
    

    entityManager.persist(game);
    entityManager.flush();

    // when
    Game found = gameRepository.findByGameId(game.getGameId());

    // then
    assertNotNull(found.getGameId());
    assertEquals(found.getGamePin(), game.getGamePin());
    assertEquals(found.getAdmin(), admin.getUserId());
    assertEquals(found.getStatus(), game.getStatus());
  }
}
