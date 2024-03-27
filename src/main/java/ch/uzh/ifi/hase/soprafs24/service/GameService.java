package ch.uzh.ifi.hase.soprafs24.service;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameResponse;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GameService {
    
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final GameRepository gameRepository;
    
    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
      }

    public List<Game> getGame() {
        return this.gameRepository.findAll();
    }

    public Game createGame(User admin) {
        Game newGame = new Game();
        if (admin.getName() == null) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Game Room couldn't be created.");
        }
        newGame.setAdmin(admin);
        newGame.setToken(UUID.randomUUID().toString());
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

    public Game getGameByGamePIN(Long gamePin) {
        return gameRepository.findByGamePin(gamePin);
    }

    public void deleteGame(Long gamePin) {
        Game game = getGameByGamePIN(gamePin);
        gameRepository.delete(game);

    }

}