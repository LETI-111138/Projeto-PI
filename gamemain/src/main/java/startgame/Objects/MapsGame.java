package startgame.Objects;

import startgame.Position;


public class MapsGame extends staticAssets{

    public MapsGame(Position position){
        super(position, "mapa1");
        this.putKeys("mapa1");
        this.putKeys("mapvoid");
        this.putKeys("mapa2");
        //Meter restantes de mapas aqui posteriormente
    }

}
