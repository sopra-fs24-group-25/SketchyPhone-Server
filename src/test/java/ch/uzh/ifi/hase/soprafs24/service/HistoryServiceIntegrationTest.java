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
import java.util.ArrayList; 

import ch.uzh.ifi.hase.soprafs24.service.Game.HistoryService;
import ch.uzh.ifi.hase.soprafs24.repository.HistoryRepository;
import java.util.Optional;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameSettingsRepository;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.DrawingRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TextPromptRepository;
import ch.uzh.ifi.hase.soprafs24.entity.Drawing;
import ch.uzh.ifi.hase.soprafs24.entity.SessionHistory;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameLoopStatus;

import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


@WebAppConfiguration
@SpringBootTest
public class HistoryServiceIntegrationTest {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DrawingRepository drawingRepository;

    @Autowired
    private TextPromptRepository textPromptRepository;

    @Autowired
    private GameSettingsRepository gameSettingsRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {
        gameSessionRepository.deleteAll();
        gameRepository.deleteAll();
        userRepository.deleteAll();
        gameSettingsRepository.deleteAll();
        drawingRepository.deleteAll();
        textPromptRepository.deleteAll();
    }

    @Test
    public void saveHistory_validInput_success() {
        // Create and save the admin user
        User admin = new User();
        admin.setUsername("adminUsername");
        admin.setPassword("adminPassword");
        admin.setCreationDate(LocalDate.now());
        admin.setToken("adminToken");
        userRepository.save(admin);
        userRepository.flush();

        // Create and save guest players
        User guestPlayer1 = new User();
        guestPlayer1.setNickname("guest1");
        guestPlayer1.setCreationDate(LocalDate.now());
        guestPlayer1.setToken("guestToken1");
        guestPlayer1.setStatus(UserStatus.ONLINE);
        userRepository.save(guestPlayer1);
        userRepository.flush();

        User guestPlayer2 = new User();
        guestPlayer2.setNickname("guest2");
        guestPlayer2.setCreationDate(LocalDate.now());
        guestPlayer2.setToken("guestToken2");
        guestPlayer2.setStatus(UserStatus.ONLINE);
        userRepository.save(guestPlayer2);
        userRepository.flush();

        // Create game settings
        GameSettings gameSettings = new GameSettings();
        gameSettings.setEnableTextToSpeech(true);
        gameSettings.setGameSettingsId(2L);
        gameSettingsRepository.save(gameSettings);
        gameSettingsRepository.flush();

        // Create game session
        List<Long> usersInSession = new ArrayList<>();
        usersInSession.add(admin.getUserId());
        usersInSession.add(guestPlayer1.getUserId());
        usersInSession.add(guestPlayer2.getUserId());

        GameSession gameSession = new GameSession();
        gameSession.setCreationDate(LocalDateTime.now());
        gameSession.setToken("gameSessionToken");
        gameSession.setStatus(GameStatus.IN_PLAY);
        gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
        gameSession.setUsersInSession(usersInSession);
        gameSessionRepository.save(gameSession);
        gameSessionRepository.flush();

        // Create game
        List<User> users = new ArrayList<>();
        users.add(admin);
        users.add(guestPlayer1);
        users.add(guestPlayer2);

        List<GameSession> gameSessions = new ArrayList<>();
        gameSessions.add(gameSession);

        Game game = new Game();
        game.setGamePin(777777L);
        game.setGameToken("gameToken");
        game.setStatus(GameStatus.OPEN);
        game.setAdmin(admin.getUserId());
        game.setGameSettingsId(gameSettings.getGameSettingsId());
        game.setUsers(users);
        game.setGameSessions(gameSessions);
        gameRepository.save(game);
        gameRepository.flush();

        // Create and save session history
        SessionHistory sessionHistory = new SessionHistory();
        sessionHistory.setGameSessionId(gameSession.getGameSessionId());
        sessionHistory.setUserId(admin.getUserId());
        sessionHistory.setHistoryName("History Test");
        historyRepository.save(sessionHistory);
        historyRepository.flush();

        // Given
        Optional<GameSession> foundGameSession = gameSessionRepository.findById(gameSession.getGameSessionId());

        // When
        historyService.saveHistory(gameSession.getGameSessionId(), admin.getUserId(), "History Test");

        // Then (you can add assertions here to verify the behavior if needed)
        assertEquals("History Test", sessionHistory.getHistoryName());
    }

    @Test
    public void saveHistory_invalidGameSessionId_failure() {
        // Create and save the admin user
        User admin = new User();
        admin.setUsername("adminUsername");
        admin.setPassword("adminPassword");
        admin.setCreationDate(LocalDate.now());
        admin.setToken("adminToken");
        userRepository.save(admin);
        userRepository.flush();

        // Create game settings
        GameSettings gameSettings = new GameSettings();
        gameSettings.setEnableTextToSpeech(true);
        gameSettingsRepository.save(gameSettings);
        gameSettingsRepository.flush();

        // Assume we have an invalid game session ID
        Long invalidGameSessionId = -1L;

        // When and Then (expect an exception)
        Exception exception = assertThrows(RuntimeException.class, () -> {
            historyService.saveHistory(invalidGameSessionId, admin.getUserId(), "History Test");
        });

        // Verify the exception message or type if needed
        String expectedMessage = "Game session not found"; // adjust this based on your service's actual exception message
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getAllHistory_success(){
        // Create and save the users
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("password1");
        user1.setCreationDate(LocalDate.now());
        user1.setToken("token1");
        user1.setStatus(UserStatus.ONLINE);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password2");
        user2.setCreationDate(LocalDate.now());
        user2.setToken("token2");
        user2.setStatus(UserStatus.ONLINE);
        userRepository.save(user2);

        User user3 = new User();
        user3.setUsername("user3");
        user3.setPassword("password3");
        user3.setCreationDate(LocalDate.now());
        user3.setToken("token3");
        user3.setStatus(UserStatus.ONLINE);
        userRepository.save(user3);

        userRepository.flush();

        // Create game session with three users
        GameSession gameSession = new GameSession();
        gameSession.setCreationDate(LocalDateTime.now());
        gameSession.setToken("gameSessionToken");
        gameSession.setStatus(GameStatus.IN_PLAY);
        gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);

        List<Long> usersInSession = List.of(user1.getUserId(), user2.getUserId(), user3.getUserId());
        gameSession.setUsersInSession(usersInSession);

        gameSessionRepository.save(gameSession);
        gameSessionRepository.flush();

        // Create and save session history for user1
        SessionHistory sessionHistory1 = new SessionHistory();
        sessionHistory1.setGameSessionId(gameSession.getGameSessionId());
        sessionHistory1.setUserId(user1.getUserId());
        sessionHistory1.setHistoryName("History Test 1");
        historyRepository.save(sessionHistory1);
        
        // Create and save session history for user2
        SessionHistory sessionHistory2 = new SessionHistory();
        sessionHistory2.setGameSessionId(gameSession.getGameSessionId());
        sessionHistory2.setUserId(user2.getUserId());
        sessionHistory2.setHistoryName("History Test 2");
        historyRepository.save(sessionHistory2);

        // Create and save session history for user3
        SessionHistory sessionHistory3 = new SessionHistory();
        sessionHistory3.setGameSessionId(gameSession.getGameSessionId());
        sessionHistory3.setUserId(user3.getUserId());
        sessionHistory3.setHistoryName("History Test 3");
        historyRepository.save(sessionHistory3);

        historyRepository.flush();

        // When
        List<SessionHistory> user1History = historyService.getUserHistory(user1.getUserId());
        List<SessionHistory> user2History = historyService.getUserHistory(user2.getUserId());
        List<SessionHistory> user3History = historyService.getUserHistory(user3.getUserId());

        // Then
        assertEquals(1, user1History.size());
        assertEquals("History Test 1", user1History.get(0).getHistoryName());

        assertEquals(1, user2History.size());
        assertEquals("History Test 2", user2History.get(0).getHistoryName());

        assertEquals(1, user3History.size());
        assertEquals("History Test 3", user3History.get(0).getHistoryName());
    }

    @Test
    public void deleteHistory_success(){
        // Create and save the user
        User user = new User();
        user.setUsername("user1");
        user.setPassword("password1");
        user.setCreationDate(LocalDate.now());
        user.setToken("token1");
        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
        userRepository.flush();

        // Create and save the game session
        GameSession gameSession = new GameSession();
        gameSession.setCreationDate(LocalDateTime.now());
        gameSession.setToken("gameSessionToken");
        gameSession.setStatus(GameStatus.IN_PLAY);
        gameSessionRepository.save(gameSession);
        gameSessionRepository.flush();

        // Create and save session history for the user
        SessionHistory sessionHistory = new SessionHistory();
        sessionHistory.setUserId(user.getUserId());
        sessionHistory.setGameSessionId(gameSession.getGameSessionId());
        sessionHistory.setHistoryName("History Test");
        historyRepository.save(sessionHistory);
        historyRepository.flush();

        // Given
        Long historyId = sessionHistory.getHistoryId();
        Long userId = user.getUserId();

        // When
        historyService.deleteHistory(historyId, userId);

        // Then
        Optional<SessionHistory> deletedHistory = historyRepository.findById(historyId);
        assertFalse(deletedHistory.isPresent(), "The session history should be deleted.");
    }

    @Test
    public void deleteHistory_nonExistingHistoryId_notFound() {
        // Create and save the user
        User user = new User();
        user.setUsername("user1");
        user.setPassword("password1");
        user.setCreationDate(LocalDate.now());
        user.setToken("token1");
        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
        userRepository.flush();

        // Given
        Long nonExistingHistoryId = 999L; // Assuming this ID does not exist
        Long userId = user.getUserId();

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            historyService.deleteHistory(nonExistingHistoryId, userId);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus(), "Should return 404 Not Found.");
    }
    

    
}
