package startgame.Objects;

import startgame.Position;

public abstract class Enemie extends Character {

    public Enemie(Position position, int health, int atkD) {
     super(position, health, atkD);
    }

    public abstract void move();
}
