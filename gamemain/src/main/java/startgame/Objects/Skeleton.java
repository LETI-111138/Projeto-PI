package startgame.Objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import startgame.Position;
import startgame.gameinit;

import java.util.HashMap;

public class Skeleton extends Enemie{


    private TextureRegion currentFrame;


    public Skeleton(Position position) {
        super(position, 50, 5, 80);
        this.putKeys("skeleton");
        mudarDirecao();
    }

    public void giveF(TextureRegion currentFrame) {
        this.currentFrame = currentFrame;
    }

    public void move(){
        float delta = Mc.getInstance().getDelta();
        float stateTime = gameinit.getStateTime();
        float x = this.getPosition().getX();
        float y = this.getPosition().getY();


        stateTime += delta;
        this.addTempoDecorrido(delta);

        if (this.getTempoDecorrido() >= this.getTempoParaMudarDirecao()) {
            mudarDirecao();
        }

        switch (this.getDirecaoAtual()) {
            case 1: x -= this.getVelocidade() * Mc.getInstance().getDelta(); break; // Esquerda
            case 2: x += this.getVelocidade() * Mc.getInstance().getDelta(); break; // Direita
            case 3: y += this.getVelocidade() * Mc.getInstance().getDelta(); break; // Cima
            case 4: y -= this.getVelocidade() * Mc.getInstance().getDelta(); break; // Baixo
            // case 0: Fica parado
        }

        float w = currentFrame.getRegionWidth();
        float h = currentFrame.getRegionHeight();


        x = MathUtils.clamp(x, 0, gameinit.getLarguraMundo() - w);
        y = MathUtils.clamp(y, 0, gameinit.getAlturaMundo() - h);

        this.getPosition().setX(x);
        this.getPosition().setY(y);

    }


}
//this.getPosition().rmX(this.getVelocidade() * Mc.getInstance().getDelta()); break; // Esquerda
//this.getPosition().addX(this.getVelocidade() * Mc.getInstance().getDelta()); break;
//this.getPosition().addY(this.getVelocidade() * Mc.getInstance().getDelta()); break;
//this.getPosition().rmY(this.getVelocidade() * Mc.getInstance().getDelta()); break;