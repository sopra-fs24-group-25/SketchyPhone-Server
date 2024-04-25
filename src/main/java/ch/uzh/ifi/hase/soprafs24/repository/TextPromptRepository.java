package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextPromptRepository extends JpaRepository<TextPrompt, Long> {

    // Delete all TextPrompts associated with a specific GameSession ID
    void deleteByGameSession_GameSessionId (Long gameSessionId);

    TextPrompt findByTextPromptId(long textPromptId);
}