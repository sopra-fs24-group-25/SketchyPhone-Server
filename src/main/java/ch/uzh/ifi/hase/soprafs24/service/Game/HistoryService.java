package ch.uzh.ifi.hase.soprafs24.service.Game;

import ch.uzh.ifi.hase.soprafs24.entity.Drawing;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.repository.HistoryRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameSessionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import ch.uzh.ifi.hase.soprafs24.entity.SessionHistory;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class HistoryService {

    private final Logger log = LoggerFactory.getLogger(HistoryService.class);

    private final HistoryRepository historyRepository;
    private final GameSessionRepository gameSessionRepository;

    @Autowired
    public HistoryService(HistoryRepository historyRepository, GameSessionRepository gameSessionRepository) {
        this.historyRepository = historyRepository;
        this.gameSessionRepository = gameSessionRepository;
    }

    public void saveHistory(List<Object> sequence, Long gameSessionId) {
        // Retrieve the game session by ID
        GameSession gameSession = gameSessionRepository.findById(gameSessionId).orElseThrow(() -> new IllegalArgumentException("Game session not found"));

        for (Object item : sequence) {
            if (item instanceof TextPrompt || item instanceof Drawing) {
                SessionHistory history = new SessionHistory(); // Instantiate History
                history.setGameSession(gameSession);
    
                if (item instanceof TextPrompt) {
                    history.setTextPrompt((TextPrompt) item); // Set text prompt
                } else if (item instanceof Drawing) {
                    history.setDrawing((Drawing) item); // Set drawing
                }
    
                historyRepository.save(history); // Save the history object
            } else {
                // Handle other types of items if necessary
                continue; // Skip to the next iteration
            }
        }
    }

    public List<Object> getHistory(Long gameSessionId) {
        // Retrieve the history entries for the given game session ID
        List<SessionHistory> history = historyRepository.findByGameSession_GameSessionId(gameSessionId);
        
        List<Object> historyList = new ArrayList<>();
        // Iterate over each history entry
        for (SessionHistory item : history) {
            if (item.getTextPrompt() != null) {
                // Handle text history entry
                TextPrompt textPrompt = item.getTextPrompt();
                // Add text prompt to the history list
                historyList.add(textPrompt);
            } else if (item.getDrawing() != null) {
                // Handle drawing history entry
                Drawing drawing = item.getDrawing();
                // Add drawing to the history list
                historyList.add(drawing);
            } 
        }
        return historyList;
    }
}