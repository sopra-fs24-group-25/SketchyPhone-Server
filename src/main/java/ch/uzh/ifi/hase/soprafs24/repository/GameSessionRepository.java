package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("gameSessionRepository")
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByGame_GameId(Long gameId);
    
    GameSession findByGameSessionId(long gameSessionId);

    List<GameSession> findByStatus(GameStatus status);

}