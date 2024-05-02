package ch.uzh.ifi.hase.soprafs24.service.Game;

import ch.uzh.ifi.hase.soprafs24.entity.Drawing;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.repository.HistoryRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import ch.uzh.ifi.hase.soprafs24.entity.History;
import ch.uzh.ifi.hase.soprafs24.entity.TextHistory;
import ch.uzh.ifi.hase.soprafs24.entity.DrawingHistory;
import java.util.ArrayList;

@Service
@Transactional
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final GameSessionRepository gameSessionRepository;

    public HistoryService(HistoryRepository historyRepository, GameSessionRepository gameSessionRepository) {
        this.historyRepository = historyRepository;
        this.gameSessionRepository = gameSessionRepository;
    }

    public void saveHistory(List<Object> sequence, Long gameSessionId) {
        // Retrieve the game session by ID
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);

        for (Object item : sequence) {
            History history;
            if (item instanceof TextPrompt) {
                TextHistory textHistory = new TextHistory(); // Instantiate TextHistory
                textHistory.setGameSession(gameSession);
                textHistory.setTextPrompt((TextPrompt) item); // Set text prompt
                history = textHistory;
            } else if (item instanceof Drawing) {
                DrawingHistory drawingHistory = new DrawingHistory(); // Instantiate DrawingHistory
                drawingHistory.setGameSession(gameSession);
                drawingHistory.setDrawing((Drawing) item); // Set drawing
                history = drawingHistory;
            } else {
                // Handle other types of items if necessary
                continue; // Skip to the next iteration
            }
            historyRepository.save(history); // Save the history object
        }
    }

    public List<Object> getHistory(Long gameSessionId) {
        // Retrieve the history entries for the given game session ID
        List<History> history = historyRepository.findByGameSessionId(gameSessionId);
        
        List<Object> historyList = new ArrayList<>();
        // Iterate over each history entry
        for (History item : history) {
            if (item instanceof TextHistory) {
                // Handle text history entry
                TextPrompt textPrompt = ((TextHistory) item).getTextPrompt();
                // Add text prompt to the history list
                historyList.add(textPrompt);
            } else if (item instanceof DrawingHistory) {
                // Handle drawing history entry
                Drawing drawing = ((DrawingHistory) item).getDrawing();
                // Add drawing to the history list
                historyList.add(drawing);
            } 
        }
        return historyList;
    }
}
