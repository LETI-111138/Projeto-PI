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
        Mc.getInstance().addBalanceCoins(gameinit.getValueOfCoins());
        this.consumeSound.setVolume(0.5f);
        this.consumeSound.play();
        gameinit.rmItem(this.getPosition());
    }



}

