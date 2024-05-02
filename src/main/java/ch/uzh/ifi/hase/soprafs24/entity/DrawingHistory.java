package ch.uzh.ifi.hase.soprafs24.entity;
import javax.persistence.*;


@Entity
@Table(name = "DrawingHistory")
public class DrawingHistory extends History {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drawingId")
    private Drawing drawing;

    // Getters and Setters
    public Drawing getDrawing() {
        return drawing;
    }

    public void setDrawing(Drawing drawing) {
        this.drawing = drawing;
    }
}