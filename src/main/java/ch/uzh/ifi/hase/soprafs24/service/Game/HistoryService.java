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
    private final GameService gameService;

    @Autowired
    public HistoryService(HistoryRepository historyRepository, GameSessionRepository gameSessionRepository, TextPromptRepository textPromptRepository, DrawingRepository drawingRepository, GameService gameService) {
        this.historyRepository = historyRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.textPromptRepository = textPromptRepository;
        this.drawingRepository = drawingRepository;
        this.gameService = gameService;
    }


    public void saveHistory(Long gameSessionId, Long userId) {
        SessionHistory history = new SessionHistory();
        history.setGameSessionId(gameSessionId);
        history.setUserId(userId);
        historyRepository.save(history);
    }

    public List<List<Object>> getUserHistory(Long userId) {
        List<SessionHistory> userHistory = historyRepository.findByUserId(userId);
        List<List<Object>> allSequences = new ArrayList<>();
    
        for (SessionHistory history : userHistory) {
            Long gameSessionId = history.getGameSessionId();
            List<Object> sequence = gameService.getSequence(gameSessionId);
            allSequences.add(sequence);
    }

    return allSequences;
    }


}