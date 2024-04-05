package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameSettingsRepository")
public interface GameSettingsRepository extends JpaRepository<GameSettings, Long> {
    GameSettings findByGameSettingsId(Long gameSettingsId);
}