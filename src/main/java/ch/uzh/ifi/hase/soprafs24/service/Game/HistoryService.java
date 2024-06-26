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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import ch.uzh.ifi.hase.soprafs24.entity.SessionHistory;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final GameSessionRepository gameSessionRepository;
    private final TextPromptRepository textPromptRepository;
    private final DrawingRepository drawingRepository;
    private final GameService gameService;

    @Autowired
    public HistoryService(HistoryRepository historyRepository, GameSessionRepository gameSessionRepository, TextPromptRepository textPromptRepository, DrawingRepository drawingRepository, GameService gameService) {
        this.historyRepository = historyRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.textPromptRepository = textPromptRepository;
        this.drawingRepository = drawingRepository;
        this.gameService = gameService;
    }


    public void saveHistory(Long gameSessionId, Long userId, String historyName) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Game session not found"));
    
        SessionHistory history = new SessionHistory();
        history.setGameSessionId(gameSessionId);
        history.setUserId(userId);

        // in case history name has not been assigned any name
        if (historyName == null || historyName.trim().isEmpty()) {
            // Assuming GameSession has a getCreationDate() method that returns a LocalDateTime or similar
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String creationDate = gameSession.getCreationDate().format(formatter);
            historyName = "History " + creationDate;
        }
        // else
        history.setHistoryName(historyName);
        historyRepository.saveAndFlush(history);
    }

    public List<SessionHistory> getUserHistory(Long userId) {
        return historyRepository.findByUserId(userId);
    }

    public void deleteHistory(Long historyId, Long userId){
        SessionHistory history = historyRepository.findByHistoryId(historyId);
        // if history object wasn't found throw 404
        if (history == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "History object not found.");
        }
        // if we're trying to delete a history object from another user throw 401
        if (!history.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed to delete this history object.");
        }
        // delete history object from repository
        historyRepository.delete(history);
    }

}