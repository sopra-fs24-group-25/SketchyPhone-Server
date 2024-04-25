package ch.uzh.ifi.hase.soprafs24.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class GameSettingsRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private GameSettingsRepository gameSettingsRepository;

  @Test
  public void findByGameSettingsId_success() {
    // given

    GameSettings gameSettings = new GameSettings();
    gameSettings.setGameSpeed(40);
    gameSettings.setNumCycles(4);
    gameSettings.setEnableTextToSpeech(true);


    entityManager.persist(gameSettings);
    entityManager.flush();

    // when
    GameSettings found = gameSettingsRepository.findByGameSettingsId(gameSettings.getGameSettingsId());

    // then
    assertNotNull(found.getGameSettingsId());
    assertEquals(found.getGameSpeed(), gameSettings.getGameSpeed());
    assertEquals(found.getNumCycles(), gameSettings.getNumCycles());
    assertEquals(found.getEnableTextToSpeech(), gameSettings.getEnableTextToSpeech());
  }
}
