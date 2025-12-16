package startgame.Objects;

import startgame.Position;
import startgame.gameinit;

public class StaticSword extends staticAssets{

    private int x = 5;
    public StaticSword(Position position){
        super(position, "staticsword");
        this.putKeys("staticsword");

    }

    public void claimSword(int x){
        this.consume(Mc.getInstance());
    }

    @Override
    public void consume(Mc mc){
        Mc.getInstance().addAtkD(x);
        this.consumeSound.setVolume(0.5f);
        this.consumeSound.play();
        System.out.println("[ITEM] Apanhaste uma espada! Novo dano do slime: " + Mc.getInstance().getatkD());
        gameinit.rmItem(this.getPosition());
    }


}
