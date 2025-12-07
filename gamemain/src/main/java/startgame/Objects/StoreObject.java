package startgame.Objects;

import startgame.Position;

public class StoreObject extends staticAssets {

    public StoreObject(Position position) {
        // Usamos "darkmage" temporariamente como o Sprite do vendedor
        super(position, "darkmage");
        this.putKeys("darkmage");
    }

    // Método auxiliar para saber se o jogador está perto para interagir
    public boolean canInteract(Position playerPos) {
        return this.getPosition().isWithinRange(playerPos.getX(), playerPos.getY(), 60f);
    }
}