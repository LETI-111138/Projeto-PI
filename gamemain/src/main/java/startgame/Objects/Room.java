package startgame.Objects;

import startgame.RNG.BossIndex;

public class Room {

    private final int id;
    private final RoomType type;
    private final int enemyCount;
    private final int itemCount;
    private final int numberOfDoors;
    private final BossIndex boss; // null se não for boss

    public Room(int id,
                RoomType type,
                int enemyCount,
                int itemCount,
                int numberOfDoors,
                BossIndex boss) {
        this.id = id;
        this.type = type;
        this.enemyCount = enemyCount;
        this.itemCount = itemCount;
        this.numberOfDoors = numberOfDoors;
        this.boss = boss;
    }

    public int getId() { return id; }
    public RoomType getType() { return type; }
    public int getEnemyCount() { return enemyCount; }
    public int getItemCount() { return itemCount; }
    public int getNumberOfDoors() { return numberOfDoors; }
    public BossIndex getBoss() { return boss; }
}