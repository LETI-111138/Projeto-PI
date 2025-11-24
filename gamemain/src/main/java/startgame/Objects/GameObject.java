package startgame.Objects;

import startgame.Position;
public abstract class GameObject {

    private Position position;

    public GameObject(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public static GameObject create(String name, Position position) {
        return null;
    }
}
