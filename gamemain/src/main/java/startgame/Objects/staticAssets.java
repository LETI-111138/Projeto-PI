package startgame.Objects;

import com.badlogic.gdx.graphics.Texture;
import startgame.Position;
import startgame.StaticImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class staticAssets extends GameObject{

    HashMap<String, Texture> txts;
    ArrayList<String> name_txts;


    public staticAssets(Position position){
        super(position);
        txts = new HashMap<>();
        name_txts = new ArrayList<>();
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

    public void putKeys(String key){
        name_txts.add(key);
    }

    public ArrayList<String> getAnimNames() { return name_txts; }

    public HashMap<String, Texture> getAnimSprites() { return txts; }

}
