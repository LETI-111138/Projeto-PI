package startgame.Objects;
import startgame.Position;
import startgame.gameinit;
public class Chicken extends staticAssets implements Item{

    public Chicken(Position position){
        super(position, "chicken");
        this.putKeys("chicken");

    }


    @Override
    public void consume(Mc mc){

        if(mc.getHealth()<200){
            Mc.getInstance().setHealth(mc.getHealth()+5);

            gameinit.rmItem(this.getPosition());
        }
        this.consumeSound.setVolume(0.5f);
        this.consumeSound.play();
    }
}
