package startgame.Objects;

public class Room {

    private final int id;
    private final RoomType type;
    private final int enemyCount;
    private final int itemCount;
    private final int numberOfDoors;
    private final Integer bossId; // null se não for boss

    public Room(int id,
                RoomType type,
                int enemyCount,
                int itemCount,
                int numberOfDoors,
                Integer bossId) {
        this.id = id;
        this.type = type;
        this.enemyCount = enemyCount;
        this.itemCount = itemCount;
        this.numberOfDoors = numberOfDoors;
        this.bossId = bossId;
    }

    public int getId() { return id; }
    public RoomType getType() { return type; }
    public int getEnemyCount() { return enemyCount; }
    public int getItemCount() { return itemCount; }
    public int getNumberOfDoors() { return numberOfDoors; }
    public Integer getBossId() { return bossId; }
}