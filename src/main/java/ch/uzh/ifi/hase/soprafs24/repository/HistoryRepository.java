package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.SessionHistory; // Import the History class
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // Import the List class

@Repository
public interface HistoryRepository extends JpaRepository<SessionHistory, Long> {
    // Delete all HistoryItems associated with a specific GameSession ID
    void deleteByGameSessionId (Long gameSessionId);

    // Retrieve all HistoryItems associated with a specific GameSession ID
    List<SessionHistory> findByGameSessionId(Long gameSessionId);

    // Retrieve all HistoryItems associated with a specific User ID
    List<SessionHistory> findByUserId(Long userId);
}