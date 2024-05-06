package ch.uzh.ifi.hase.soprafs24.service.Game;

import ch.uzh.ifi.hase.soprafs24.entity.Drawing;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.repository.HistoryRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TextPromptRepository;
import ch.uzh.ifi.hase.soprafs24.repository.DrawingRepository;
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

    private final HistoryRepository historyRepository;
    private final GameSessionRepository gameSessionRepository;
    private final TextPromptRepository textPromptRepository;
    private final DrawingRepository drawingRepository;

    @Autowired
    public HistoryService(HistoryRepository historyRepository, GameSessionRepository gameSessionRepository, TextPromptRepository textPromptRepository, DrawingRepository drawingRepository) {
        this.historyRepository = historyRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.textPromptRepository = textPromptRepository;
        this.drawingRepository = drawingRepository;
    }

    public List<Object> saveHistory(Long gameSessionId) {
        // Retrieve the game session by ID
        GameSession gameSession = gameSessionRepository.findById(gameSessionId).orElseThrow(() -> new IllegalArgumentException("Game session not found"));

        List<Object> allPromptsAndDrawings = new ArrayList<>();
    
        // Retrieve all text prompts associated with the game session ID
        List<TextPrompt> textPrompts = textPromptRepository.findByGameSessionGameSessionId(gameSessionId);
        allPromptsAndDrawings.addAll(textPrompts);
        
        // Retrieve all drawings associated with the game session ID
        List<Drawing> drawings = drawingRepository.findByGameSessionGameSessionId(gameSessionId);
        allPromptsAndDrawings.addAll(drawings);
        
        return allPromptsAndDrawings;
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