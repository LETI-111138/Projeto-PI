package startgame.Objects;

import startgame.Position;
public class Coin extends staticAssets implements Item {

    public Coin(Position position){
        super(position);
        this.putKeys("coin");

    }

    public void collectCoin(int x){
        this.consume(Mc.getInstance());
    }

    public void consume(Mc mc){
        Mc.getInstance().addBalanceCoins(1);
    }

}

