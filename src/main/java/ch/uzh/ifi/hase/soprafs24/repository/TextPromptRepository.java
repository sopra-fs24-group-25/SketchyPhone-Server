package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TextPromptRepository extends JpaRepository<TextPrompt, Long> {
    // Find all text prompts by a specific user
    List<TextPrompt> findByCreatorId(@Param("creatorId") Long creatorId);

    // Delete all TextPrompts associated with a specific GameSession ID
    void deleteByGameSession_GameSessionId (Long gameSessionId);
}