package ch.uzh.ifi.hase.soprafs24.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ch.uzh.ifi.hase.soprafs24.entity.Drawing;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@DataJpaTest
public class DrawingRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private DrawingRepository drawingRepository;

  @Test
  public void findByAvatarId_success() {
    // given
    User admin = new User();
    admin.setNickname("Test nickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    entityManager.persist(admin);
    entityManager.flush();

    Drawing drawing = new Drawing();
    drawing.setEncodedImage("test input");
    drawing.setCreator(admin);
    drawing.setCreationDateTime(LocalDateTime.now());    

    entityManager.persist(drawing);
    entityManager.flush();

    // when
    Drawing found = drawingRepository.findByDrawingId(drawing.getDrawingId());

    // then
    assertNotNull(found.getDrawingId());
    assertEquals(found.getEncodedImage(), drawing.getEncodedImage());
    assertEquals(found.getCreator(), admin);
  }
}
