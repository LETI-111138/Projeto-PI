package startgame.Objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import startgame.AnimatedImage;
import startgame.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Character {

    private int health;
    private int atkD;
    private Position position;
    private HashMap<String, Animation<TextureRegion>> animSprites;
    private ArrayList<String> animNames;

    public Character(Position position, int health, int atkD) {
        this.position = position;
        this.health = health;
        this.atkD = atkD;
        animSprites = new HashMap<>();
        animNames = new ArrayList<>();
    }



    public void setatkD(int x) {
        atkD=x;
    }

    public int getatkD() {
        return atkD;
    }

    public void addAtkD(int x) {atkD+=x;}

    public void setHealth(int n) {
        health = n;
    }

    public int getHealth() {
        return health;
    }

    public Position getPosition() {
        return position;
    }

    public void names_of_sheets_anim(){
        HashMap<String, Animation<TextureRegion>> allAnim= AnimatedImage.getAnimAll();
        for(Map.Entry<String, Animation<TextureRegion>> entry : allAnim.entrySet()){
            for(String s : animNames){
                if(entry.getKey().equals(s)){
                    animSprites.put(s, entry.getValue());
                }
            }
        }

    }
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void putKeys(String key){
        animNames.add(key);
    }

    public ArrayList<String> getAnimNames() { return animNames; }

    public HashMap<String, Animation<TextureRegion>> getAnimSprites() { return animSprites; }

    public abstract void move();


}
