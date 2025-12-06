package startgame.Objects;

import startgame.RNG.BossIndex;
import startgame.RNG.Distribuicoes;
import startgame.RNG.RandomConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RoomManager {

    private int numberOfRooms;
    private final Random random = new Random();

    // Ordem aleatória dos bosses (V.A. Discreta Uniforme – permutação)
    private final List<BossIndex> bossOrder = new ArrayList<>();
    private int nextBossIndex = 0;

    private int nextRoomId = 1;

    private Room currentRoom;
    private List<Room> nextOptions = new ArrayList<>();

    public RoomManager() {
        generateBossOrder();          // permutação dos bosses
        currentRoom = generateStartingRoom();
        nextOptions = generateNextRooms();
        numberOfRooms = 0;
    }

    private void generateBossOrder() {
        bossOrder.clear();
        for (int i = 0; i < RandomConfig.TOTAL_BOSSES; i++) {
            bossOrder.add(BossIndex.getByIndex(i));
            System.out.println(i);
        }
        Distribuicoes.gerarPermutacao(bossOrder);

        // Agora tens uma permutação uniforme validada pelo teu próprio código RNG
    }

    private Room generateStartingRoom() {
        // primeira sala: combate normal
        return generateRoom(RoomType.COMBAT);
    }

    private Room generateRoom(RoomType type) {
        int id = nextRoomId++;
        numberOfRooms++;

        BossIndex boss = null;
        int enemies = 0;
        int items = 0;

        if (type == RoomType.BOSS) {
            if (nextBossIndex < bossOrder.size()) {
                boss = bossOrder.get(nextBossIndex++);
            }
        }

        switch (type) {
            case COMBAT:
               enemies = Distribuicoes.gerarPoisson(5.0f);
                break;
            case TREASURE:
                items = Distribuicoes.gerarBinomial(
                        RandomConfig.TREASURE_ITEMS_N,
                        RandomConfig.TREASURE_ITEMS_P
                );
                break;

        }

        // 2 ou 3 portas
        int doors = RandomConfig.MIN_DOORS +
                random.nextInt(RandomConfig.MAX_DOORS - RandomConfig.MIN_DOORS + 1);

        return new Room(id, type, enemies, items, doors, boss);
    }

    private Room generateRoom() {
        // Binomial com n=1 (Bernoulli) para tipo de sala
        RoomType type = null;
        if(numberOfRooms<6){
            int bernoulli = Distribuicoes.gerarBinomial(1, RandomConfig.PROB_TREASURE_ROOM);
             type = (bernoulli == 1) ? RoomType.TREASURE : RoomType.COMBAT;
        }else{
             type = RoomType.BOSS;
             numberOfRooms = 0;
        }


        return generateRoom(type);
    }

    private List<Room> generateNextRooms() {
        List<Room> list = new ArrayList<>();
        int numDoors = currentRoom != null ? currentRoom.getNumberOfDoors() : 2;

        for (int i = 0; i < numDoors; i++) {
            list.add(generateRoom());
        }
        return list;
    }

    public Room goToNextRoom(int optionIndex) {
        if (optionIndex < 0 || optionIndex >= nextOptions.size()) {
            throw new IllegalArgumentException("Invalid room option index: " + optionIndex);
        }
        currentRoom = nextOptions.get(optionIndex);
        nextOptions = generateNextRooms();
        return currentRoom;
    }

    public Room getCurrentRoom() { return currentRoom; }
    public List<Room> getNextOptions() { return nextOptions; }
    public List<BossIndex> getBossOrder() { return bossOrder; }

    public int getNextBossIndex() { return nextBossIndex; }
}
