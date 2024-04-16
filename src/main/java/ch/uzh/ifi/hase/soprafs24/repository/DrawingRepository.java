package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Drawing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("drawingRepository")
public interface DrawingRepository extends JpaRepository<Drawing, Long> {
  
  Drawing findByDrawingId(long drawingId);
}

