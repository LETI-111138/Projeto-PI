package startgame.Objects;

import startgame.Position;

public abstract class Character {

    private int health;
    private int atkD;
    private Position position;

    public Character(Position position, int health, int atkD) {
        this.position = position;
        this.health = health;
        this.atkD = atkD;
    }

    public void setatkD(int x) {
        atkD=x;
    }

    public int getatkD() {
        return atkD;
    }

    public void setHealth(int n) {
        health = n;
    }

    public int getHealth() {
        return health;
    }

    public Position getPosition() {
        return position;
    }

    public abstract void move();
}
