package startgame.Objects;

import com.badlogic.gdx.math.MathUtils;
import startgame.Position;
import startgame.gameinit;

public class BossClown extends Enemy {

    public BossClown(Position position) {
        super(position, 500, 30, 70f);
        this.putKeys("clownboss");
        mudarDirecao();
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

        float w = getCurrentFrame().getRegionWidth();
        float h = getCurrentFrame().getRegionHeight();


        x = MathUtils.clamp(x, 0, gameinit.getLarguraMundo() - w);
        y = MathUtils.clamp(y, 0, gameinit.getAlturaMundo() - h);

        this.getPosition().setX(x);
        this.getPosition().setY(y);
    }

}
