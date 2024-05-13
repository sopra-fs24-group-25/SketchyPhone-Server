package ch.uzh.ifi.hase.soprafs24.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.repository.GameSessionRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TextPromptRepository;
import ch.uzh.ifi.hase.soprafs24.repository.DrawingRepository;
import java.util.List;
import ch.uzh.ifi.hase.soprafs24.service.Game.HistoryService;
import ch.uzh.ifi.hase.soprafs24.repository.HistoryRepository;

import java.util.Optional;

public class HistoryServiceTest {

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private TextPromptRepository textPromptRepository;

    @Mock
    private DrawingRepository drawingRepository;

    @InjectMocks
    private HistoryService historyService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void saveHistory_validInput_success() {
        // Arrange
        Long gameSessionId = 1L;
        GameSession gameSession = new GameSession();
        gameSession.setGameSessionId(gameSessionId);
        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.of(gameSession));

        // Act
        List<Object> result = historyService.saveHistory(gameSessionId);

        // Assert
        assertNotNull(result);
        
    }

    @Test
    public void getHistory_validInput_success() {
        // Arrange
        Long gameSessionId = 1L;
        GameSession gameSession = new GameSession();
        gameSession.setGameSessionId(gameSessionId);
        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.of(gameSession));

        // Act
        List<Object> result = historyService.getHistory(gameSessionId);

        // Assert
        assertNotNull(result);

    }
}


