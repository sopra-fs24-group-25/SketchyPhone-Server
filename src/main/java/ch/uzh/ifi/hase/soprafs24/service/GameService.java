package ch.uzh.ifi.hase.soprafs24.service;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameSessionRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TextPromptRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;


@Service
@Transactional
public class GameService {
    
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final GameRepository gameRepository;
    private final UserService userService;
    private static final Set<Long> generatedPins = new HashSet<>();
    
    @Autowired
    public GameService(GameRepository gameRepository, UserService userService) {
        this.gameRepository = gameRepository;
        this.userService = userService;
      }

    public List<Game> getGame() {
        return this.gameRepository.findAll();
    }

    // function to generate unique game pin
    public long generateGamePin() {
        final long MIN_PIN = 100000L;
        final long MAX_PIN = 999999L;
        SecureRandom secureRandom = new SecureRandom();
        long pin;
        do {
            pin = MIN_PIN + secureRandom.nextLong() % (MAX_PIN - MIN_PIN + 1);
        } while (!generatedPins.add(pin));
        return pin;
    }

    public Game createGame(User admin) {
        User savedUser = userService.createUser(admin);

        Game newGame = new Game();
        // redundant code since userService.createUser already checks whether the name is passed or not
        // should think about why a room creation should fail
        if (admin.getName() == null) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Game Room couldn't be created.");
        }
        // assign unique gamePin to game room        
        long gamePin = generateGamePin();
        newGame.setGamePin(gamePin); 

        // set the admin to the admin's ID so the info can be pulled from the userRepository
        
        newGame.setAdmin(savedUser.getId());
        
        newGame.setToken(UUID.randomUUID().toString());
        //creates list and adds it to the gameroom
        List<User> users = new ArrayList<>();
        users.add(savedUser);
        newGame.setUsers(users);

        // gets the current date and sets it in the gameroom
        LocalDate today = LocalDate.now();
        newGame.setCreationDate(today);

        //sets the game room status
        newGame.setStatus(GameStatus.OPEN);
        
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newGame = gameRepository.save(newGame);
        gameRepository.flush();
    
        log.debug("Created Information for Game Room: {}", newGame);
    
        return newGame;
      }


    public GameResponse joinGame(Long submittePin) {
        Game game = gameRepository.findByGamePin(submittePin);
        if (game == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        
        switch (game.getStatus()) {
        case IN_PLAY:
            return new GameResponse(GameStatus.IN_PLAY, "The game is currently in play");
        case OPEN:
            return new GameResponse(GameStatus.OPEN, "The game is currently open");
        case CLOSED:
            return new GameResponse(GameStatus.CLOSED, "The game is currently closed");
        default:
            throw new IllegalStateException("Unhandled game status: " + game.getStatus());
        }

    }
    
    @Autowired
    private GameSessionRepository gameSessionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TextPromptRepository textPromptRepository;


    public void createTextPrompt(Long gameSessionId, Long userId, String textPromptContent) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    
        TextPrompt text = new TextPrompt();
        text.setContent(textPromptContent);
        text.setGameSession(gameSession);
        text.setCreator(user);
        textPromptRepository.save(text);
    }

    public void endGameSessionAndDeleteTextPrompts(Long gameSessionId) {
        // Check if the game session exists and whether it can be ended
        gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game session not found"));

        // Delete text prompts related to the game session
        textPromptRepository.deleteByGameSessionId(gameSessionId);
    }
    
}
