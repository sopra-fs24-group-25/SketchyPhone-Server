package ch.uzh.ifi.hase.soprafs24.entity;
import javax.persistence.*;

@Entity
@Table(name = "TextHistory")
public class TextHistory extends History {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "textPromptId")
    private TextPrompt textPrompt;

    // Getters and Setters
    public TextPrompt getTextPrompt() {
        return textPrompt;
    }

    public void setTextPrompt(TextPrompt textPrompt) {
        this.textPrompt = textPrompt;
    }
}