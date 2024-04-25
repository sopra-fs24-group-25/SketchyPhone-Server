package ch.uzh.ifi.hase.soprafs24.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ch.uzh.ifi.hase.soprafs24.entity.Avatar;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@DataJpaTest
public class AvatarRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private AvatarRepository avatarRepository;

  @Test
  public void findByAvatarId_success() {
    // given
    User admin = new User();
    admin.setNickname("Test nickname");
    admin.setCreationDate(LocalDate.now());
    admin.setToken("test token");
    entityManager.persist(admin);
    entityManager.flush();

    Avatar avatar = new Avatar();
    avatar.setEncodedImage("test input");
    avatar.setCreatorId(1L);
    avatar.setCreationDateTime(LocalDateTime.now());
    

    entityManager.persist(avatar);
    entityManager.flush();

    // when
    Avatar found = avatarRepository.findByAvatarId(avatar.getAvatarId());

    // then
    assertNotNull(found.getAvatarId());
    assertEquals(found.getEncodedImage(), avatar.getEncodedImage());
    assertEquals(found.getCreatorId(), admin.getUserId());
  }
}
