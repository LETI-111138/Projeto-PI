package startgame.Objects;

import startgame.Position;

import java.util.HashMap;

public class Skeleton extends Enemie{

    //HashMap<String name, Texture> = ;

    public Skeleton(Position position, int health, int atkD) {
        super(position, health, atkD);
        this.putKeys("skeleton");
    }

    public void move(){

    }
}
