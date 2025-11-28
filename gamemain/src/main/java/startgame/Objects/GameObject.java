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

    public void setPosition(Position newPosition) {position.setPosition(newPosition);}

    public void setPosition(float x, float y) {
        position.setX(x);
        position.setY(y);
    }

    public void addPosition(Position addPos){
        position.addPosition(addPos);
    }

    public void addPosition(float x, float y) {
        position.addX(x);
        position.addY(y);
    }

    public static GameObject create(String name, Position position) {
        return null;
    }
}
