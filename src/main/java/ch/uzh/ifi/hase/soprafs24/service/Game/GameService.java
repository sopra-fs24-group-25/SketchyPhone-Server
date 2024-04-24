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
import ch.uzh.ifi.hase.soprafs24.repository.GameSessionRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameSettingsRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TextPromptRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

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
    
    public GameService(GameRepository gameRepository, UserService userService, GameSettingsRepository gameSettingsRepository, DrawingRepository drawingRepository) {
        this.gameSettingsRepository = gameSettingsRepository;
        this.gameRepository = gameRepository;
        this.userService = userService;
        this.drawingRepository = drawingRepository;
      }

    public Game getGame(Long gameId) {
        Game game = gameRepository.findByGameId(gameId);

        if (game == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        return game;
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

    // TODO if permanent users are allowed -> add condition to check whether admin is already a user -> if not, create new user
    // else just save that user

    public Game createGame(User admin) {
        User savedUser = userService.createUser(admin);
        savedUser.setRole("admin");

        Game newGame = new Game();

        // redundant code since userService.createUser already checks whether the name is passed or not
        // should think about why a room creation should fail
        if (admin.getNickname() == null) {
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
        
        game.setStatus(GameStatus.IN_PLAY);
    
        // create a game session and assign the according values
        GameSession gameSession = new GameSession();
        gameSession.setCreationDate(LocalDate.now());
        gameSession.setGame(game);
        gameSession.setStatus(GameStatus.IN_PLAY);
        gameSession.setToken(UUID.randomUUID().toString());
        // add gameLoopStatus and set it to TEXTPROMPT
        gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
        gameSessionRepository.save(gameSession);

        // add users who are currently in the room
        for (int i = 0; i<game.getUsers().size(); i++){
            gameSession.getUsersInSession().add(game.getUsers().get(i).getId());
        }

        game.getGameSessions().add(gameSession);
        gameRepository.save(game);
    
        return game;
    }

    public void authenticateAdmin(String token, User user){
        if (!user.getRole().equals("admin") || !user.getToken().equals(token)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not the admin");
        }
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
        joinUser.setRole("player");
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
                .anyMatch(existingUser -> existingUser.getNickname().equals(joinUser.getNickname()));
            
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

        //
        if (user.getRole().equals("admin")){
            // if admin is the only player and leaves the room should be deleted
            if (game.getUsers().size() == 1){
                gameRepository.delete(game);
            } // else reassign admin role to random user in room
            else{
                SecureRandom random = new SecureRandom();
                int randomNumber = random.nextInt(game.getUsers().size());

                while (game.getUsers().get(randomNumber) == user){
                    randomNumber = random.nextInt(game.getUsers().size());
                }

                game.getUsers().get(randomNumber).setRole("admin");
            }
        }
        int size = game.getGameSessions().size();
        // remove user from game session
        if (size != 0){
            // last index would be the current game session
            game.getGameSessions().get(size - 1).getUsersInSession().remove(user.getId());
        }

        // delete user from repository
        userRepository.delete(user);
    }
      
        
    public Game getGameByGamePIN(Long gamePin) {
        return gameRepository.findByGamePin(gamePin);
    }

    // TODO should delete all temporary users
    public Game gameroomCleanUp (Long gameRoomId){
        Game game = gameRepository.findByGameId(gameRoomId);
        if (game == null){
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


    public TextPrompt createTextPrompt(Long gameSessionId, Long userId, long previousDrawingId, String textPromptContent) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null){
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
        if (previousDrawingId != 777L){
            Drawing previousDrawing = drawingRepository.findByDrawingId(previousDrawingId);
            previousDrawing.setNextTextPrompt(text.getTextPromptId());
        }

        // get list of all textprompts in current round
        List<TextPrompt> textPrompts = textPromptRepository.findAll().stream()
            .filter(textPrompt -> textPrompt.getAssignedTo() == null && textPrompt.getRound() == gameSession.getRoundCounter() && textPrompt.getGameSession().getGameSessionId().equals(gameSessionId))
            .collect(Collectors.toList());

        // once every user has created a text prompt switch game loop status to drawing
        if (textPrompts.size()%gameSession.getUsersInSession().size() == 0){
            gameSession.setGameLoopStatus(GameLoopStatus.DRAWING);
        }

        return text;
    }

    public TextPrompt getTextPrompt(Long gameSessionId, Long userId) {
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // get list of all available textprompts
        List<TextPrompt> availablePrompts = textPromptRepository.findAll().stream()
            .filter(textPrompt -> textPrompt.getAssignedTo() == null && !textPrompt.getCreator().getId().equals(userId) && textPrompt.getRound() == gameSession.getRoundCounter() && textPrompt.getGameSession().getGameSessionId().equals(gameSessionId))
            .collect(Collectors.toList());

        List<TextPrompt> lastPrompts = textPromptRepository.findAll().stream()
            .filter(textPrompt -> textPrompt.getAssignedTo() == null && textPrompt.getRound() == gameSession.getRoundCounter() && textPrompt.getGameSession().getGameSessionId().equals(gameSessionId))
            .collect(Collectors.toList());

        List<TextPrompt> alreadyAssignedPrompts = textPromptRepository.findAll().stream()
            .filter(textPrompt -> textPrompt.getAssignedTo() != null && textPrompt.getRound() == gameSession.getRoundCounter() && textPrompt.getGameSession().getGameSessionId().equals(gameSessionId))
            .collect(Collectors.toList());

        
        // select random prompting 
        SecureRandom random = new SecureRandom();
        int randomNumber = random.nextInt(availablePrompts.size());
        TextPrompt assignedPrompt = availablePrompts.get(randomNumber);

        // if last prompt would be the one userId drew -> choose random already assigned prompt
        // and assign that one to userId and the last prompt to whoever had that prompt
        if (availablePrompts.isEmpty()){
            randomNumber = random.nextInt(alreadyAssignedPrompts.size());
            assignedPrompt = alreadyAssignedPrompts.get(randomNumber);
            lastPrompts.get(0).setAssignedTo(assignedPrompt.getAssignedTo());
        }

        assignedPrompt.setAssignedTo(userId);

        return assignedPrompt;
    }

    public List<TextPrompt> getTextPrompts(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return textPromptRepository.findByCreatorId(user.getId());

    }
    
    public Drawing createDrawing(Long gameSessionId, long userId, long previousTextPromptId, String drawingBase64){
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found");
        }
        User user = userRepository.findById(userId);
        if (user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Drawing drawing = new Drawing();
        drawing.setCreationDateTime(LocalDateTime.now());
        drawing.setGameSessionId(gameSessionId);
        drawing.setCreator(userRepository.findById(userId));
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
            .filter(drawingCreated -> drawingCreated.getAssignedTo() == null && drawingCreated.getRound() == gameSession.getRoundCounter() && drawingCreated.getGameSessionId().equals(gameSessionId))
            .collect(Collectors.toList());
        
        int numCycles = gameSettingsRepository.findByGameSettingsId(gameSession.getGame().getGameSettingsId()).getNumCycles();

        // check whether this was the last round and if so set the game loop status to presentation
        if (gameSession.getRoundCounter() == 2* numCycles) {
            gameSession.setGameLoopStatus(GameLoopStatus.PRESENTATION);
        }
        // once every user has created a drawings switch game loop status to text prompt
        else {
            if (drawings.size()%gameSession.getUsersInSession().size() == 0){
                gameSession.setGameLoopStatus(GameLoopStatus.TEXTPROMPT);
            }
        }

        return savedDrawing;
    }

    public Drawing getDrawing(Long gameSessionId, Long userId){
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // get list of all available drawings from current round
        List<Drawing> availableDrawings = drawingRepository.findAll().stream()
        .filter(drawing -> drawing.getAssignedTo() == null && !drawing.getCreator().getId().equals(userId) && drawing.getRound() == gameSession.getRoundCounter() && drawing.getGameSessionId().equals(gameSessionId))
        .collect(Collectors.toList());

        List<Drawing> lastDrawings = drawingRepository.findAll().stream()
            .filter(drawing -> drawing.getAssignedTo() == null && drawing.getRound() == gameSession.getRoundCounter() && drawing.getGameSessionId().equals(gameSessionId))
            .collect(Collectors.toList());

        List<Drawing> alreadyAssignedDrawings = drawingRepository.findAll().stream()
            .filter(drawing -> drawing.getAssignedTo() != null && drawing.getRound() == gameSession.getRoundCounter() && drawing.getGameSessionId().equals(gameSessionId))
            .collect(Collectors.toList());

        
        // select random drawing 
        SecureRandom random = new SecureRandom();
        int randomNumber = random.nextInt(availableDrawings.size());
        Drawing assignedDrawing = availableDrawings.get(randomNumber);

        // if last drawing would be the one userId drew -> choose random alreaday assigned drawing
        // and assign that one to userId and the last drawing to whoever had that drawing

        if (availableDrawings.isEmpty()){
            random.nextInt(alreadyAssignedDrawings.size());
            assignedDrawing = alreadyAssignedDrawings.get(randomNumber);
            lastDrawings.get(0).setAssignedTo(assignedDrawing.getAssignedTo());
        }

        assignedDrawing.setAssignedTo(userId);

        return assignedDrawing;

    }

    // TODO ending a game session should create a sessionHistory entity with all drawings and textprompts
    public void endGameSessionAndDeleteTextPrompts(Long gameSessionId) {
        // Check if the game session exists and whether it can be ended
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        if (gameSession == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found");
        }

        // Delete text prompts related to the game session
        textPromptRepository.deleteByGameSession_GameSessionId(gameSessionId);
        
    }
    
    public void startNextRound(long gameSessionId){
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        int countRounds = gameSettingsRepository.findByGameSettingsId(gameSession.getGame().getGameSettingsId()).getNumCycles();
        int nextRound = (gameSession.getRoundCounter() + 1);
        gameSession.setRoundCounter(nextRound);
    }

    public List<Game> getAllGames(){
        return gameRepository.findAll();
    }

    public List<Object> getSequence(Long gameSessionId){

        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);

        // get a list of all first text prompts
        List<TextPrompt> availablePrompts = textPromptRepository.findAll().stream()
            .filter(textPrompt -> textPrompt.getPreviousDrawingId() == 777L && textPrompt.getGameSession().getGameSessionId().equals(gameSessionId))
            .collect(Collectors.toList());

        if (availablePrompts.size() == 0){
            gameSession.setCurrentIndex(0);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No more sequences left");
        }
        
        SecureRandom random = new SecureRandom();
        int randomNumber = random.nextInt(availablePrompts.size());
        TextPrompt assignedPrompt = availablePrompts.get(randomNumber);

        // set previous drawing to 776 so that when trying to present the next sequence we won't repeat the same sequence
        assignedPrompt.setPreviousDrawingId(776L);

        Long nextId = assignedPrompt.getTextPromptId();

        List<Object> sequence = new ArrayList<>();

        Drawing drawing = new Drawing();

        while(nextId != null){
            sequence.add(assignedPrompt);
            drawing = drawingRepository.findByDrawingId(assignedPrompt.getNextDrawingId());
            sequence.add(drawing);
            nextId = drawing.getNextTextPrompt();
            if (nextId != null){
                assignedPrompt = textPromptRepository.findByTextPromptId(nextId);
            }
        }

        return sequence;
    }

    public int getCurrentIndex(Long gameSessionId){
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        return gameSession.getCurrentIndex();
    }

    public int increaseCurrentIndex(Long gameSessionId){
        GameSession gameSession = gameSessionRepository.findByGameSessionId(gameSessionId);
        gameSession.setCurrentIndex(gameSession.getCurrentIndex() + 1);
        return gameSession.getCurrentIndex();
    }
}