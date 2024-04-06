package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;


@Repository("gameRepository")
public interface GameRepository extends JpaRepository<Game, Long> {
  Game findByGamePin(Long gamePin);

  Game findByGameId(Long gameId);

  @Query("SELECT g FROM Game g LEFT JOIN FETCH g.gameSessions WHERE g.gameId = :gameId")
  List<Game> findGameWithGameSessions(@Param("gameId") Long gameId);

}
