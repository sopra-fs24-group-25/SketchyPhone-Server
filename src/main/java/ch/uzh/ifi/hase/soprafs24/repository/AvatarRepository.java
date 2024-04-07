package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("avatarRepository")
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
  
  Avatar findById(long avatarId);
}

