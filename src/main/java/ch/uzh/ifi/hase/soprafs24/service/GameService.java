package ch.uzh.ifi.hase.soprafs24.service;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameSessionRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameSettingsRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TextPromptRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSessionDTO;

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
    private final GameSettingsRepository gameSettingsRepository;
    private final GameRepository gameRepository;
    private final UserService userService;
    private static final Set<Long> generatedPins = new HashSet<>();
    
    public GameService(GameRepository gameRepository, UserService userService, GameSettingsRepository gameSettingsRepository) {
        this.gameSettingsRepository = gameSettingsRepository;
        this.gameRepository = gameRepository;
        this.userService = userService;
      }

    public List<Game> getGame() {
        return this.gameRepository.findAll();
    }

    // function to generate unique game pin
    public long generateGamePin() {
        final long MIN_PIN = 100000L;
        final long MAX_PIN = 900000L;
        SecureRandom secureRandom = new SecureRandom();
        long pin;
        do {
            pin = MIN_PIN + secureRandom.nextLong(MAX_PIN) + 100000L;
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

        newGame.setGameToken(UUID.randomUUID().toString());
        //creates list and adds it to the gameroom
        List<User> users = new ArrayList<>();
        users.add(savedUser);
        newGame.setUsers(users);

        // gets the current date and sets it in the gameroom
        LocalDate today = LocalDate.now();
        newGame.setGameCreationDate(today);

        //sets the game room status
        newGame.setStatus(GameStatus.OPEN);
        
        // create game settings with some standard values
        GameSettings gameSettings = new GameSettings();
        gameSettings.setGameSpeed(40);
        gameSettings.setNumCycles(4);
        gameSettings.setEnableTextToSpeech(false);
        gameSettingsRepository.save(gameSettings);
        gameSettingsRepository.flush();
        newGame.setGameSettingsId(gameSettings.getGameSettingsId());

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newGame = gameRepository.save(newGame);
        gameRepository.flush();
    
        log.debug("Created Information for Game Room: {}", newGame);
    
        return newGame;
      }


    public GameSessionDTO joinGame(Long submittePin) {
        Game game = gameRepository.findByGamePin(submittePin);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        switch (game.getStatus()) {
            case IN_PLAY:
                return new GameSessionDTO(GameStatus.IN_PLAY, "The game is currently in play");
            case OPEN:
                return new GameSessionDTO(GameStatus.OPEN, "The game is currently open");
            case CLOSED:
                return new GameSessionDTO(GameStatus.CLOSED, "The game is currently closed");
            default:
                throw new IllegalStateException("Unhandled game status: " + game.getStatus());
        }
    }

    public Game getGameByGamePIN(Long gamePin) {
        return gameRepository.findByGamePin(gamePin);
    }

    public void deleteGame(Long gamePin) {
        Game game = getGameByGamePIN(gamePin);
        gameRepository.delete(game);

    }

    public List<User> getGameRoomUsers(Long gameId){
        Game gameRoom = gameRepository.findByGameId(gameId);

        if (gameRoom == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game Room doesn't exist");
        }

        return gameRoom.getUsers();
    }

    public GameSettings getGameSettings(Long gameRoomId){
        Game game = gameRepository.findByGameId(gameRoomId);
        Long gameSettingsId = game.getGameSettingsId();
        return gameSettingsRepository.findByGameSettingsId(gameSettingsId);
    }

    public Game updateGameSettings(Long gameRoomId, GameSettings gameSettings){
        Game game = gameRepository.findByGameId(gameRoomId);
        GameSettings oldSettings = gameSettingsRepository.findByGameSettingsId(game.getGameSettingsId());
        oldSettings.setEnableTextToSpeech(gameSettings.getEnableTextToSpeech());
        oldSettings.setGameSpeed(gameSettings.getGameSpeed());
        oldSettings.setNumCycles(gameSettings.getNumCycles());

        return game;
    }
        

    @Autowired
    private GameSessionRepository gameSessionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TextPromptRepository textPromptRepository;


    public TextPrompt createTextPrompt(Long gameSessionId, Long userId, String textPromptContent) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        TextPrompt text = new TextPrompt();
        text.setContent(textPromptContent);
        text.setGameSession(gameSession);
        text.setCreator(user);
        return textPromptRepository.save(text);
    }

    public List<TextPrompt> getTextPrompts(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return textPromptRepository.findByCreatorId(user.getId());

    }

    public void endGameSessionAndDeleteTextPrompts(Long gameSessionId) {
        // Check if the game session exists and whether it can be ended
        gameSessionRepository.findByGameSessionId(gameSessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game session not found"));

        // Delete text prompts related to the game session
        textPromptRepository.deleteByGameSession_GameSessionId(gameSessionId);
        
    }

}