package ch.uzh.ifi.hase.soprafs24.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.repository.GameSessionRepository;
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

    // @Test
    // public void deleteHistory_WrongUser() {
    //     // setup
    //     User admin = new User();
    //     admin.setNickname("testNickname");
    //     admin.setUserId(1L);

    //     Game game = new Game();
    //     game.setAdmin(admin.getUserId());

    //     User player = new User();
    //     player.setNickname("testNickname");
    //     player = userService.createUser(player);

    //     gameService.joinGame(game.getGamePin(), player.getUserId());

    //     gameService.createGameSession(game.getGameId());

    //     GameSession gameSession = game.getGameSessions().get(0);

    //     // create Session history object
    //     historyService.saveHistory(gameSession.getGameSessionId(), admin.getUserId(), "test history name");
    //     List<SessionHistory> historyList = historyRepository.findByUserId(admin.getUserId());
    //     SessionHistory history = historyList.get(0);

    //     // assert whether creation worked
    //     assertEquals(history.getUserId(), admin.getUserId());
    //     assertEquals(history.getHistoryName(), "test history name");
    //     assertEquals(history.getGameSessionId(), gameSession.getGameSessionId());

    //     // delete hisory object
    //     historyService.deleteHistory(history.getHistoryId(), admin.getUserId());
    //     historyList = historyRepository.findByUserId(admin.getUserId());

    //     // check whether deletion worked
    //     assertEquals(historyList.size(), 0);
    //     assertThrows(ResponseStatusException.class, () -> historyRepository.findByHistoryId(history.getHistoryId()));

    // }
}