package ch.uzh.ifi.hase.soprafs24.service.Game;

import ch.uzh.ifi.hase.soprafs24.constant.GameLoopStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Drawing;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSession;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.TextPrompt;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.DrawingRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.HistoryRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameSessionRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameSettingsRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TextPromptRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.hibernate.mapping.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.security.SecureRandom;
import java.util.random.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Collections;

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final GameSettingsRepository gameSettingsRepository;
    private final GameRepository gameRepository;
    private final DrawingRepository drawingRepository;
    private final UserService userService;
    private static final Set<Long> generatedPins = new HashSet<>();

    @Autowired
    private GameSessionRepository gameSessionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TextPromptRepository textPromptRepository;

    public GameService(GameRepository gameRepository, UserService userService,
            GameSettingsRepository gameSettingsRepository, DrawingRepository drawingRepository) {
        this.gameSettingsRepository = gameSettingsRepository;
        this.gameRepository = gameRepository;
        this.userService = userService;
        this.drawingRepository = drawingRepository;
    }

    public Game getGame(Long gameId) {
        Game game = gameRepository.findByGameId(gameId);

        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        return game;
    }

    public void openGame(Long gameId) {
        Game game = gameRepository.findByGameId(gameId);

        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        game.setStatus(GameStatus.OPEN);
        gameRepository.save(game);
        gameRepository.flush();
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

    public Game createGame(Long adminId) {
        User savedUser = userRepository.findByUserId(adminId);
        if (savedUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        savedUser.setRole("admin");

        Game newGame = new Game();

        // assign unique gamePin to game room
        long gamePin = generateGamePin();
        newGame.setGamePin(gamePin);

        // set the admin to the admin's ID so the info can be pulled from the
        // userRepository
        newGame.setAdmin(savedUser.getUserId());

        newGame.setGameToken(UUID.randomUUID().toString());
        // creates list and adds it to the gameroom
        List<User> users = new ArrayList<>();
        users.add(savedUser);
        newGame.setUsers(users);

        // gets the current date and sets it in the gameroom
        LocalDate today = LocalDate.now();
        newGame.setGameCreationDate(today);

        // sets the game room status
        newGame.setStatus(GameStatus.OPEN);

        // create game settings with some standard values
        GameSettings gameSettings = new GameSettings();
        gameSettings.setGameSpeed(25);
        gameSettings.setNumCycles(2);
        gameSettings.setEnableTextToSpeech(true);
        gameSettingsRepository.save(gameSettings);
        gameSettingsRepository.flush();
        newGame.setGameSettingsId(gameSettings.getGameSettingsId());

        // Set the other fields of game...

        // Create a list of GameSession instances
        List<GameSession> gameSessions = new ArrayList<>();

        newGame.setGameSessions(gameSessions);

        newGame = gameRepository.save(newGame);
        gameRepository.flush();

        log.debug("Created Information for Game Room: {}", newGame);

        return newGame;
    }

    public Game createGameSession(Long gameId) {

        // Find the game by ID
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        game.setStatus(GameStatus.IN_PLAY);

        // create a game session and assign the according values
        GameSession gameSession = new GameSession();
        gameSession.setCreationDate(LocalDateTime.now());
        gameSession.setGame(game);
        gameSession.setStatus(GameStatus.IN_PLAY);
        gameSession.setToken(UUID.randomUUID().toString());
        // add gameLoopStatus and set it to TEXTPROMPT
        gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
        gameSessionRepository.save(gameSession);
        gameSessionRepository.flush();

        // add users who are currently in the room
        for (int i = 0; i < game.getUsers().size(); i++) {
            gameSession.getUsersInSession().add(game.getUsers().get(i).getUserId());
        }

        game.getGameSessions().add(gameSession);
        gameRepository.save(game);

        return game;
    }

    public void authenticateAdmin(String token, User user) {
        if (!user.getRole().equals("admin") || !user.getToken().equals(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not the admin");
        }
    }

    public Game joinGame(Long submittedPin, Long userId) {
        User joinUser = userRepository.findByUserId(userId);
        if (joinUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        joinUser.setRole("player");
        Game game = gameRepository.findByGamePin(submittedPin);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        // If the game is CLOSED or IN_PLAY, return an informative message
        if (game.getStatus() == GameStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Game is closed.");
        } else if (game.getStatus() == GameStatus.IN_PLAY) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Game is in play.");
        }

        boolean userAlreadyInGame = game.getUsers().stream()
                .anyMatch(existingUser -> existingUser.getUserId().equals(joinUser.getUserId()));

        if (!userAlreadyInGame) {
            game.getUsers().add(joinUser);
        }

        // if 8 (max) player joined change game status to CLOSED
        if (game.getUsers().size() == 8) {
            game.setStatus(GameStatus.CLOSED);
        }

        gameRepository.save(game);

        // Return a successful join message
        return game;

    }

    public void leaveRoom(Long gameRoomId, long userId) {
        Game game = gameRepository.findByGameId(gameRoomId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // if it's the last user -> delete game, else go through normal user removal process
        if (game.getUsers().size() == 1) {
            // go through all gameSessions and remove game reference from game sessions
            List<GameSession> gameSessions = game.getGameSessions();
            
            for (int i = 0; i < gameSessions.size(); i++) {
                gameSessions.get(i).setGame(null);
            }

            gameSessionRepository.flush();

            gameRepository.delete(game);
        } else {
            if (user.getRole().equals("admin")) {
                // if admin is the only player and leaves the room should be deleted
                if (game.getUsers().size() == 1) {
                    gameRepository.delete(game);
                } // else reassign admin role to random user in room
                else {
                    SecureRandom random = new SecureRandom();
                    int randomNumber = random.nextInt(game.getUsers().size());
    
                    while (game.getUsers().get(randomNumber) == user) {
                        randomNumber = random.nextInt(game.getUsers().size());
                    }
    
                    game.getUsers().get(randomNumber).setRole("admin");
                    game.setAdmin(game.getUsers().get(randomNumber).getUserId());
                }
            }
            int size = game.getGameSessions().size();
            // remove user from game session
            if (size != 0) {
                // last index would be the current game session
                game.getGameSessions().get(size - 1).getUsersInSession().remove(user.getUserId());
            }
    
            game.getUsers().remove(user);
    
            // revert back role to default
            user.setRole(null);
            userRepository.save(user);
    
            // if game status is CLOSED and there's now less than 8 players in the game ->
            // set status back to OPEN
            if (game.getStatus() == GameStatus.CLOSED && game.getUsers().size() < 8) {
                game.setStatus(GameStatus.OPEN);
            }
        }
        
    }

    public Game getGameByGamePIN(Long gamePin) {
        return gameRepository.findByGamePin(gamePin);
    }

    public Game gameroomCleanUp(Long gameRoomId) {
        Game game = gameRepository.findByGameId(gameRoomId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game Room doesn't exist");
        }
        // Check if the game room is inactive
        if (isGameRoomInactive(game)) {
            // Close the game room
            game.setStatus(GameStatus.CLOSED);
            // Additional cleanup tasks, such as releasing resources or deleting data

            // Optionally, return a message or confirmation indicating cleanup success
        }

        return game;
    }

    private boolean isGameRoomInactive(Game game) {
        if (game.getUsers().size() < 2) {
            return true;
        }

        // Check if the game room has been inactive for more than one day
        Instant oneDayAgo = Instant.now().minus(Duration.ofDays(1));
        Instant lastActivity = game.getLastActivity(); // Assuming you have a lastActivity field in the Game entity
        if (lastActivity != null && lastActivity.isBefore(oneDayAgo)) {
            return true;
        }

        return false;
    }

    public List<User> getGameRoomUsers(Long gameId) {
        Game gameRoom = gameRepository.findByGameId(gameId);

        if (gameRoom == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game Room doesn't exist");
        }

        return gameRoom.getUsers();
    }

    public GameSettings getGameSettings(Long gameRoomId) {
        Game game = gameRepository.findByGameId(gameRoomId);
        Long gameSettingsId = game.getGameSettingsId();
        return gameSettingsRepository.findByGameSettingsId(gameSettingsId);
    }

    public Game updateGameSettings(Long gameRoomId, GameSettings gameSettings) {
        Game game = gameRepository.findByGameId(gameRoomId);
        GameSettings oldSettings = gameSettingsRepository.findByGameSettingsId(game.getGameSettingsId());
        oldSettings.setEnableTextToSpeech(gameSettings.getEnableTextToSpeech());
        oldSettings.setGameSpeed(gameSettings.getGameSpeed());
        oldSettings.setNumCycles(gameSettings.getNumCycles());

        return game;
    }

    public TextPrompt createTextPrompt(Long gameSessionId, Long userId, long previousDrawingId,
            String textPromptContent) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        TextPrompt text = new TextPrompt();
        text.setContent(textPromptContent);
        text.setGameSession(gameSession);
        text.setCreator(user);
        text.setRound(gameSession.getRoundCounter());
        // will be 777 if it's the first text prompt, can be used for presentation
        text.setPreviousDrawingId(previousDrawingId);
        textPromptRepository.save(text);
        // the very first text prompts should have 777 in the path
        if (previousDrawingId != 777L) {
            Drawing previousDrawing = drawingRepository.findByDrawingId(previousDrawingId);
            previousDrawing.setNextTextPrompt(text.getTextPromptId());
        }

        // get list of all textprompts in current round
        List<TextPrompt> textPrompts = textPromptRepository.findAll().stream()
                .filter(textPrompt -> textPrompt.getAssignedTo() == null
                        && textPrompt.getRound() == gameSession.getRoundCounter()
                        && textPrompt.getGameSession().getGameSessionId().equals(gameSessionId))
                .collect(Collectors.toList());

        // once every user has created a text prompt switch game loop status to drawing
        if (textPrompts.size() % gameSession.getUsersInSession().size() == 0) {
            startNextRound(gameSessionId);

            final List<Long> usersInSession = gameSession.getUsersInSession();

            List<TextPrompt> availablePrompts = new ArrayList<TextPrompt>();

            // Seed to have the same shuffling everytime for the current session
            long shuffleSeed = gameSessionId;

            // This should ensure the shuffling is consistent during a gamesession
            Random randomSeed = new Random(shuffleSeed);

            // Shuffle the users to be in random order for this gamesession
            Collections.shuffle(usersInSession, randomSeed);

            availablePrompts = textPromptRepository.findAll().stream()
                    .filter(prompt -> prompt.getAssignedTo() == null
                            && prompt.getRound() == gameSession.getRoundCounter() - 1
                            && prompt.getGameSession().getGameSessionId().equals(gameSessionId))
                    .collect(Collectors.toList());

            // randomSeed.nextInt(1, usersInSession.size());
            Integer shift = (gameSession.getRoundCounter() - 1) % (usersInSession.size() - 1) + 1;

            Collections.sort(availablePrompts, Comparator.comparingLong(prompt -> {
                long id = prompt.getCreator().getUserId();
                return usersInSession.indexOf(id);
            }));

            for (int i = 0; i < availablePrompts.size(); i++) {
                availablePrompts.get(i).setAssignedTo(usersInSession.get((i + shift) % usersInSession.size()));
            }

            textPromptRepository.flush();

            List<TextPrompt> assignedTexPrompts = textPromptRepository.findAll().stream()
                .filter(prompt -> prompt.getAssignedTo() != null
                        && prompt.getGameSession().getGameSessionId().equals(gameSessionId)
                        && prompt.getRound() == gameSession.getRoundCounter() - 1)
                .collect(Collectors.toList());   

            // wait for all drawings to be assigned
            while (assignedTexPrompts.size() != gameSession.getUsersInSession().size()){
                assignedTexPrompts = textPromptRepository.findAll().stream()
                .filter(prompt -> prompt.getAssignedTo() != null
                        && prompt.getGameSession().getGameSessionId().equals(gameSessionId)
                        && prompt.getRound() == gameSession.getRoundCounter() - 1)
                .collect(Collectors.toList()); 
            }

            if (gameSession.getGameLoopStatus() != GameLoopStatus.PRESENTATION) {
                gameSession.setGameLoopStatus(GameLoopStatus.DRAWING);
            }
        }

        return text;
    }

    public TextPrompt getTextPrompt(Long gameSessionId, Long userId) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // get list of text prompt assigned to user (should be just one)
        List<TextPrompt> assigned = textPromptRepository.findAll().stream()
                .filter(textprompt -> textprompt.getAssignedTo().equals(userId)
                        && textprompt.getRound() == gameSession.getRoundCounter() - 1
                        && textprompt.getGameSession() == gameSession)
                .collect(Collectors.toList());

        TextPrompt assignedPrompt = assigned.get(0);

        return assignedPrompt;
    }

    public Drawing createDrawing(Long gameSessionId, long userId, long previousTextPromptId, String drawingBase64) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found");
        }
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Drawing drawing = new Drawing();
        drawing.setCreationDateTime(LocalDateTime.now());
        drawing.setGameSessionId(gameSessionId);
        drawing.setCreator(userRepository.findByUserId(userId));
        drawing.setPreviousTextPrompt(previousTextPromptId);
        drawing.setRound(gameSession.getRoundCounter());

        String base64String = drawingBase64;

        int paddingLength = 4 - (base64String.length() % 4);

        // Add padding characters if necessary
        if (paddingLength != 4) {
            StringBuilder paddedString = new StringBuilder(base64String);
            for (int i = 0; i < paddingLength; i++) {
                paddedString.append('=');
            }
            base64String = paddedString.toString();
        }

        drawing.setEncodedImage(drawingBase64);
        Drawing savedDrawing = drawingRepository.save(drawing);
        drawingRepository.flush();

        // set previous' text prompt nextDrawingId
        TextPrompt previousTextPrompt = textPromptRepository.findByTextPromptId(previousTextPromptId);
        previousTextPrompt.setNextDrawingId(savedDrawing.getDrawingId());

        // get list of all drawings in current round
        List<Drawing> drawings = drawingRepository.findAll().stream()
                .filter(drawingCreated -> drawingCreated.getAssignedTo() == null
                        && drawingCreated.getRound() == gameSession.getRoundCounter()
                        && drawingCreated.getGameSessionId().equals(gameSessionId))
                .collect(Collectors.toList());

        int numCycles = gameSettingsRepository.findByGameSettingsId(gameSession.getGame().getGameSettingsId())
                .getNumCycles();

        // check whether this was the last round and if so set the game loop status to
        // PRESENTATION
        if (drawings.size() % gameSession.getUsersInSession().size() == 0) {
            startNextRound(gameSessionId);

            final List<Long> usersInSession = gameSession.getUsersInSession();

            // assign every text prompt to a user
            List<Drawing> availableDrawings = new ArrayList<Drawing>();

            // Seed to have the same shuffling everytime for the current session
            long shuffleSeed = gameSessionId;

            // This should ensure the shuffling is consistent during a gamesession
            Random randomSeed = new Random(shuffleSeed);

            // Shuffle the users to be in random order for this gamesession
            Collections.shuffle(usersInSession, randomSeed);

            // Sort user IDs
            Collections.sort(usersInSession, Comparator.naturalOrder());

            availableDrawings = drawingRepository.findAll().stream()
                    .filter(draw -> draw.getAssignedTo() == null
                            && draw.getRound() == gameSession.getRoundCounter() - 1
                            && draw.getGameSessionId().equals(gameSessionId))
                    .collect(Collectors.toList());

            // What we want, is a pseudorandom shift, that doesn't change for the current
            // session but increases for every round by one and a different base shift for
            // every session
            // randomSeed.nextInt(1, usersInSession.size());
            Integer shift = (gameSession.getRoundCounter() - 1) % (usersInSession.size() - 1) + 1;

            // sort available drawings based on sorting of usersInSession
            Collections.sort(availableDrawings, Comparator.comparingLong(draw -> {
                long id = draw.getCreator().getUserId();
                return usersInSession.indexOf(id);
            }));

            for (int i = 0; i < availableDrawings.size(); i++) {
                availableDrawings.get(i).setAssignedTo(usersInSession.get((i + shift) % usersInSession.size()));
            }

            drawingRepository.flush();

            List<Drawing> assignedDrawings = drawingRepository.findAll().stream()
                .filter(draw -> draw.getAssignedTo() != null
                        && draw.getGameSessionId().equals(gameSessionId)
                        && draw.getRound() == gameSession.getRoundCounter() - 1)
                .collect(Collectors.toList());   

            // wait for all drawings to be assigned
            while (assignedDrawings.size() != gameSession.getUsersInSession().size()){
                assignedDrawings = drawingRepository.findAll().stream()
                .filter(draw -> draw.getAssignedTo() != null
                        && draw.getGameSessionId().equals(gameSessionId)
                        && draw.getRound() == gameSession.getRoundCounter() - 1)
                .collect(Collectors.toList()); 
            }

            if (gameSession.getGameLoopStatus() != GameLoopStatus.PRESENTATION) {
                gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
            }

        }

        return savedDrawing;
    }

    public Drawing getDrawing(Long gameSessionId, Long userId) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // get list of text prompt assigned to user (should be just one)
        List<Drawing> assigned = drawingRepository.findAll().stream()
                .filter(draw -> draw.getAssignedTo().equals(userId)
                        && draw.getRound() == gameSession.getRoundCounter() - 1
                        && draw.getGameSessionId().equals(gameSession.getGameSessionId()))
                .collect(Collectors.toList());

        Drawing assignedDrawing = assigned.get(0);

        return assignedDrawing;

    }

    public void endGameSessionAndDeleteTextPrompts(Long gameSessionId) {
        // Check if the game session exists and whether it can be ended
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found");
        }

        // Delete text prompts related to the game session
        textPromptRepository.deleteByGameSession_GameSessionId(gameSessionId);

    }

    public void startNextRound(long gameSessionId) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);

        if (gameSession.getRoundCounter() == 2 * gameSettingsRepository
                .findByGameSettingsId(gameSession.getGame().getGameSettingsId()).getNumCycles()) {
            gameSession.setGameLoopStatus(GameLoopStatus.PRESENTATION);
        } else {
            int countRounds = gameSettingsRepository.findByGameSettingsId(gameSession.getGame().getGameSettingsId())
                    .getNumCycles();
        }

        int nextRound = (gameSession.getRoundCounter() + 1);
        gameSession.setRoundCounter(nextRound);

        gameSessionRepository.flush();

    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public List<Object> getSequence(Long gameSessionId) {

        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);

        // get a list of all first text prompts
        List<TextPrompt> availablePrompts = textPromptRepository.findAll().stream()
                .filter(textPrompt -> textPrompt.getPreviousDrawingId() == 777L
                        && textPrompt.getGameSession().getGameSessionId().equals(gameSessionId))
                .collect(Collectors.toList());

        TextPrompt assignedPrompt = new TextPrompt();

        Long nextId = assignedPrompt.getTextPromptId();

        List<Object> sequence = new ArrayList<>();

        Drawing drawing = new Drawing();

        for (int i = 0; i < availablePrompts.size(); i++) {
            assignedPrompt = availablePrompts.get(i);
            nextId = assignedPrompt.getTextPromptId();
            while (nextId != null) {
                sequence.add(assignedPrompt);
                if (assignedPrompt.getNextDrawingId() != null) {
                    drawing = drawingRepository.findByDrawingId(assignedPrompt.getNextDrawingId());
                    sequence.add(drawing);
                    nextId = drawing.getNextTextPrompt();
                    if (nextId != null) {
                        assignedPrompt = textPromptRepository.findByTextPromptId(nextId);
                    }
                } else {
                    break;
                }

            }
        }

        return sequence;
    }

    public int getCurrentIndex(Long gameSessionId) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        return gameSession.getCurrentIndex();
    }

    public int increaseCurrentIndex(Long gameSessionId) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        gameSession.setCurrentIndex(gameSession.getCurrentIndex() + 1);
        return gameSession.getCurrentIndex();
    }

    public void increasePromptVotes(Long gameSessionId, Long textPromptId, Long userId) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game Session not found");
        }
        TextPrompt text = textPromptRepository.findByTextPromptId(textPromptId);
        if (text == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Text prompt not found");
        }
        User user = userRepository.findByUserId(userId);
        if (user != text.getCreator()) {
            text.setNumVotes(text.getNumVotes() + 1);
        }

    }

    public void increaseDrawingVotes(Long gameSessionId, Long drawingId, Long userId) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game Session not found");
        }
        Drawing drawing = drawingRepository.findByDrawingId(drawingId);
        if (drawing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Drawing not found");
        }
        User user = userRepository.findByUserId(userId);
        if (user != drawing.getCreator()) {
            drawing.setNumVotes(drawing.getNumVotes() + 1);
        }

    }

    public void decreasePromptVotes(Long gameSessionId, Long textPromptId, Long userId) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game Session not found");
        }
        TextPrompt text = textPromptRepository.findByTextPromptId(textPromptId);
        if (text == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Text prompt not found");
        }
        User user = userRepository.findByUserId(userId);
        if (text.getNumVotes() != 0 && user != text.getCreator()) {
            text.setNumVotes(text.getNumVotes() - 1);
        }
    }

    public void decreaseDrawingVotes(Long gameSessionId, Long drawingId, Long userId) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game Session not found");
        }
        Drawing drawing = drawingRepository.findByDrawingId(drawingId);
        if (drawing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Drawing not found");
        }
        User user = userRepository.findByUserId(userId);
        if (drawing.getNumVotes() != 0 && user != drawing.getCreator()) {
            drawing.setNumVotes(drawing.getNumVotes() - 1);
        }
    }

    public List<TextPrompt> getTopThreeTextPrompts(Long gameSessionId) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game Session Not Found");
        }

        if (gameSession.getGameLoopStatus() != GameLoopStatus.LEADERBOARD) {
            gameSession.setGameLoopStatus(GameLoopStatus.LEADERBOARD);
        }
        // get a list of all text prompts
        List<TextPrompt> availablePrompts = textPromptRepository.findAll().stream()
                .filter(textPrompt -> textPrompt.getGameSession().getGameSessionId().equals(gameSessionId))
                .collect(Collectors.toList());

        Collections.sort(availablePrompts, new Comparator<TextPrompt>() {
            public int compare(TextPrompt t1, TextPrompt t2) {
                return t1.getNumVotes() - t2.getNumVotes();
            }
        });

        Collections.reverse(availablePrompts);

        // removes all prompts with 0 votes
        availablePrompts.removeIf(textprompt -> textprompt.getNumVotes() == 0);

        // only return the top three if there are more drawings
        if (availablePrompts.size() <= 3) {

            return availablePrompts;

        } else {
            return availablePrompts.subList(0, 3);
        }
    }

    public List<Drawing> getTopThreeDrawings(Long gameSessionId) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game Session Not Found");
        }

        if (gameSession.getGameLoopStatus() != GameLoopStatus.LEADERBOARD) {
            gameSession.setGameLoopStatus(GameLoopStatus.LEADERBOARD);
        }

        // get a list of all drawings
        List<Drawing> availableDrawings = drawingRepository.findAll().stream()
                .filter(drawing -> drawing.getGameSessionId().equals(gameSessionId))
                .collect(Collectors.toList());

        Collections.sort(availableDrawings, new Comparator<Drawing>() {
            public int compare(Drawing t1, Drawing t2) {
                return t1.getNumVotes() - t2.getNumVotes();
            }
        });

        Collections.reverse(availableDrawings);

        // remove all drawings with 0 votes
        availableDrawings.removeIf(drawing -> drawing.getNumVotes() == 0);

        // only return the top three if there are more drawings
        if (availableDrawings.size() <= 3) {

            return availableDrawings;

        } else {
            return availableDrawings.subList(0, 3);
        }

    }

}
