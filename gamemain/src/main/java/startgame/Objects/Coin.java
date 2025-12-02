package startgame.Objects;

import startgame.Position;
import startgame.gameinit;

public class Coin extends staticAssets implements Item {

    public Coin(Position position){
        super(position, "coin");
        this.putKeys("coin");

    }


    @Override
    public void consume(Mc mc){
        Mc.getInstance().addBalanceCoins(1);
        gameinit.rmItem(this.getPosition());
    }



}

