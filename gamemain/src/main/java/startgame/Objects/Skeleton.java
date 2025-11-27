package startgame.Objects;

import startgame.Position;
import startgame.gameinit;

import java.util.HashMap;

public class Skeleton extends Enemie{



    public Skeleton(Position position) {
        super(position, 50, 5, 80);
        this.putKeys("skeleton");
    }

    public void move(){
        float delta = Mc.getInstance().getDelta();
        float stateTime = gameinit.getStateTime();

        stateTime += delta;
        this.addTempoDecorrido(delta);

        if (this.getTempoDecorrido() >= this.getTempoParaMudarDirecao()) {
            mudarDirecao();
        }

        switch (this.getDirecaoAtual()) {
            case 1: this.getPosition().rmX(this.getVelocidade() * Mc.getInstance().getDelta()); break; // Esquerda
            case 2: this.getPosition().addX(this.getVelocidade() * Mc.getInstance().getDelta()); break; // Direita
            case 3: this.getPosition().addY(this.getVelocidade() * Mc.getInstance().getDelta()); break; // Cima
            case 4: this.getPosition().rmY(this.getVelocidade() * Mc.getInstance().getDelta()); break; // Baixo
            // case 0: Fica parado
        }

    }
}
