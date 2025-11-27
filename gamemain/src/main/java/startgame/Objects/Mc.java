package startgame.Objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import startgame.AnimatedImage;
import startgame.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Mc extends Character{


    public Mc(Position position, int health, int atkD) {
        super(position, health, atkD);
        this.putKeys("player");
    }


    public void move(){

    }

}
