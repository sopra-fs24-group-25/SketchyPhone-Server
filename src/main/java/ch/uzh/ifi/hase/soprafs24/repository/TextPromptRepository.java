package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TextPromptRepository extends JpaRepository<TextPrompt, Long> {
    // Find all text prompts by a specific user
    List<TextPrompt> findByCreatorId(Long creatorId);

    void deleteByGameSessionId(Long gameSessionId);
}