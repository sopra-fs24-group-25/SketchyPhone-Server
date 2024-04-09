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

import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
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
    
    @Autowired
    private GameSessionRepository gameSessionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TextPromptRepository textPromptRepository;
    
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
            pin = secureRandom.nextLong(MAX_PIN) + MIN_PIN;
        } while (!generatedPins.add(pin));
        return pin;
    }

    public Game createGame(User admin) {
        User savedUser = userService.createUser(admin);
        savedUser.setRole("admin");

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

        //Gamesession 
        // Create a new GameGetDTO instance
        GameGetDTO game = new GameGetDTO();

        // Set the other fields of game...

        // Create a list of GameSession instances
        List<GameSession> gameSessions = new ArrayList<>();

        // // Populate the list with GameSessionGetDTO instances
        // // For example:
        // GameSession session = new GameSession();
        // session.setStatus(GameStatus.OPEN);
        // session.setGameSessionId(1L);
        // session.setToken("session-pin-here");
        // gameSessions.add(session);

        // Set the gameSessions field of game
        game.setGameSessions(gameSessions);
        // // Convert game to JSON
        // ObjectMapper objectMapper = new ObjectMapper();
        // String json;
        // try {
        //     json = objectMapper.writeValueAsString(game);
        // } catch (JsonProcessingException e) {
        //     // Handle JSON processing exception
        //     e.printStackTrace();
        //     json = ""; // or some default value
        // }

        // // Now json contains the JSON representation of the game object
        // System.out.println(json); 

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newGame = gameRepository.save(newGame);
        gameRepository.flush();
    
        log.debug("Created Information for Game Room: {}", newGame);
    
        return newGame;
      }
    
    
    public Game createGameSession(Long gameId) {
        
        // Find the game by ID
        Game game = gameRepository.findById(gameId).orElseThrow(() -> 
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
    
        // create a game session and assign the according values
        GameSession gameSession = new GameSession();
        gameSession.setCreationDate(LocalDate.now());
        gameSession.setGame(game);
        gameSession.setStatus(GameStatus.IN_PLAY);
        gameSession.setToken(UUID.randomUUID().toString());
        gameSessionRepository.save(gameSession);

        
        game.getGameSessions().add(gameSession);
        gameRepository.save(game);
    
        return game;
    }

    @Transactional(readOnly = true)
    public List<GameSession> getGameSessionsByGameId(Long gameId) {
        // Find the game by its ID to ensure it exists
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        // Return the game sessions associated with the game
        return gameSessionRepository.findByGame_GameId(gameId);
    }
      

    public Game joinGame(Long submittedPin, User user) {
        User joinUser = userService.createUser(user);
        Game game = gameRepository.findByGamePin(submittedPin);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
    
        // If the game is CLOSED or IN_PLAY, return an informative message
        if (game.getStatus() == GameStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Game is closed.");
        } else if (game.getStatus() == GameStatus.IN_PLAY) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Game is in play.");
        }
    
        // If the game is OPEN, continue with the logic to add a user to the game
        if (game.getStatus() == GameStatus.OPEN) {

            boolean userAlreadyInGame = game.getUsers().stream()
                .anyMatch(existingUser -> existingUser.getName().equals(joinUser.getName()));
            
                if (!userAlreadyInGame){
                    game.getUsers().add(joinUser);
                }
            gameRepository.save(game);
            
            // Return a successful join message
            return game;
        } else {
            // Handle any other unexpected statuses
            throw new IllegalStateException("Unhandled game status: " + game.getStatus());
        }
    }

    public void leaveRoom(Long gameRoomId, long userId) {
        Game game = gameRepository.findByGameId(gameRoomId);
        if (game == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        
        if (user.getRole().equals("admin")){
            // if admin is the only player and leaves the room should be deleted
            if (game.getUsers().size() == 1){
                gameRepository.delete(game);
            } // else reassign admin role to random user in room
            else{
                SecureRandom random = new SecureRandom();
                int randomNumber = random.nextInt(game.getUsers().size());
                game.getUsers().get(randomNumber).setRole("admin");
            }
            
        }else{
            game.getUsers().remove(user);
        }

        userRepository.delete(user);

        

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


    public TextPrompt createTextPrompt(Long gameSessionId, Long userId, String textPromptContent) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        TextPrompt text = new TextPrompt();
        text.setContent(textPromptContent);
        text.setGameSession(gameSession);
        text.setCreator(user);
        textPromptRepository.save(text);
        return text;
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