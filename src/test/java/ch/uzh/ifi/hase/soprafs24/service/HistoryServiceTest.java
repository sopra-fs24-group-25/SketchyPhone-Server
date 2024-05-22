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

    @InjectMocks
    private HistoryService historyService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void saveHistory_validInput_success() {
        // given
        GameSession gameSession = new GameSession();
        gameSession.setGameSessionId(1L);
        gameSession.setToken("test token");

        when(gameSessionRepository.findById(any())).thenReturn(Optional.of(gameSession));

        // when
        historyService.saveHistory(1L, 1L, "test history");

        // then
        verify(historyRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void saveHistory_invalidGameSessionId_failure() {
        // given
        when(gameSessionRepository.findById(any())).thenReturn(Optional.empty());

        // when
        try {
            historyService.saveHistory(1L, 1L, "test history");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // then
            assertEquals("Game session not found", e.getMessage());
        }
    }

    @Test
    public void getUserHistory_validInput_success() {
        // given
        when(historyRepository.findByUserId(any())).thenReturn(List.of());

        // when
        List result = historyService.getUserHistory(1L);

        // then
        assertNotNull(result);
    }

    @Test
    public void getUserHistory_invalidInput_failure() {
        // given
        when(historyRepository.findByUserId(any())).thenReturn(null);

        // when
        List result = historyService.getUserHistory(1L);

        // then
        assertNull(result);
    }
}