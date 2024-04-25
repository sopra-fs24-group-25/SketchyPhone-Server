package ch.uzh.ifi.hase.soprafs24.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

@DataJpaTest
public class TextPromptRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private TextPromptRepository textPromptRepository;

  @Test
  public void findByTextPromptId_success() {
    // given
    User admin = new User();
    admin.setNickname("Test nickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    entityManager.persist(admin);
    entityManager.flush();

    TextPrompt textPrompt = new TextPrompt();
    textPrompt.setContent("Test content");
    textPrompt.setCreator(admin);

    entityManager.persist(textPrompt);
    entityManager.flush();

    // when
    TextPrompt found = textPromptRepository.findByTextPromptId(textPrompt.getTextPromptId());

    // then
    assertNotNull(found.getTextPromptId());
    assertEquals(found.getContent(), textPrompt.getContent());
    assertEquals(found.getCreator(), admin);
  }
}
