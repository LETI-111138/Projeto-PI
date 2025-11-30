package startgame.Objects;

import startgame.Position;

public class Door extends staticAssets {

    // índice da opção (0, 1, 2) para o RoomManager saber qual foi a porta escolhida
    private int optionIndex;

    public Door(Position position, int optionIndex) {
        super(position, "Basedoor");
        this.optionIndex = optionIndex;
        this.putKeys("Basedoor");
    }

    public int getOptionIndex() {
        return optionIndex;
    }
}
