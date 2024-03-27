package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameRoomRepository")
public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
  GameRoom findByName(String name);

  GameRoom findByLink(String link);
}
