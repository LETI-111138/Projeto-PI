package startgame.Objects;

import com.badlogic.gdx.graphics.Texture;
import startgame.Position;
import startgame.StaticImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class staticAssets extends GameObject implements Item{

    HashMap<String, Texture> txts;
    ArrayList<String> name_txts;
    String key;


    public staticAssets(Position position, String key){
        super(position);
        txts = new HashMap<>();
        name_txts = new ArrayList<>();
        this.key = key;
    }

    public void names_of_sheets_anim(){
        HashMap<String, Texture> allStatic= StaticImage.getStaticAll();
        for(Map.Entry<String, Texture> entry : allStatic.entrySet()){
            for(String s : name_txts){
                if(entry.getKey().equals(s)){
                    txts.put(s, entry.getValue());
                }
            }
        }

    }

    public String getKey(){
        return key;
    }

    public void putKeys(String key){
        name_txts.add(key);
    }

    public ArrayList<String> getAnimNames() { return name_txts; }

    public HashMap<String, Texture> getAnimSprites() { return txts; }

    @Override
    public void consume(Mc mc) {
        //por default nao faz nada;
    }
}
