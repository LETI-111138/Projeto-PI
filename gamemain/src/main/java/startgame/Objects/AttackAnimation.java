package startgame.Objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import startgame.Position;
import startgame.RNG.Distribuicoes;
import startgame.gameinit;

public class AttackAnimation extends Enemy{

    public AttackAnimation(Position position) {
        super(position, 0, 0, 70f);
        this.putKeys("attackanimation");
        mudarDirecao();
    }

    public void move(){
       return;
    }
}
