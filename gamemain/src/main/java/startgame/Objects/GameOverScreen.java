package startgame.Objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import startgame.Position;

public class GameOverScreen extends GameObject{

    private TextureRegion currentFrame;

    public GameOverScreen(Position position) {
        super(position);
        currentFrame = new TextureRegion();
    }

    public void giveF(TextureRegion currentFrame) {
        this.currentFrame = currentFrame;
    }

    public TextureRegion getCurrentFrame() {return currentFrame;}
}
