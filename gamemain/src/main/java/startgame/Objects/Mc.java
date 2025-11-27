package startgame.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import startgame.AnimatedImage;
import startgame.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Mc extends Character{

    private int balanceCoins;
    private static Mc INSTANCE;
    private float velocidade;
    float delta;

    public Mc(Position position) {
        super(position, 200, 10);
        this.putKeys("player");
        balanceCoins = 0;
        velocidade = 80f;
        delta = Gdx.graphics.getDeltaTime();
    }

    public static Mc getInstance(){
        if(INSTANCE==null)INSTANCE = new Mc(new Position(1000,1000));
        return INSTANCE;
    }

    public int getBalanceCoins() {return balanceCoins;}

    public void setBalanceCoins(int balanceCoins) {this.balanceCoins = balanceCoins;}

    public void addBalanceCoins(int balanceCoins) {this.balanceCoins += balanceCoins;}

    public void removeBalanceCoins(int balanceCoins) {this.balanceCoins -= balanceCoins;}

    public void setDelta(float newdelta){ delta = newdelta;}

    public float getDelta(){return delta;}

    public void setVelocidade(float velocidade) {this.delta = velocidade;}

    public float getVelocidade() {return velocidade;}

    public void move(){

        if (Gdx.input.isKeyPressed(Input.Keys.A)) this.getPosition().rmX(velocidade * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) this.getPosition().addX(velocidade * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) this.getPosition().addY (velocidade * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) this.getPosition().rmY (velocidade * delta);

    }

}
