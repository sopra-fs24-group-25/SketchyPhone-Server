package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.History; // Import the History class
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // Import the List class

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    // Delete all HistoryItems associated with a specific GameSession ID
    void deleteByGameSession_GameSessionId (Long gameSessionId);

    History findByHistoryId(long historyId);

    // Retrieve all HistoryItems associated with a specific GameSession ID
    List<History> findByGameSessionId(Long gameSessionId);
}