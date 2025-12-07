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
            } else {
                type = RoomType.COMBAT;
            }
        }

        switch (type) {
            case COMBAT:
                enemies = Distribuicoes.gerarPoisson(RandomConfig.ENEMIES_LAMBDA);
                break;
            case TREASURE:
                items = Distribuicoes.gerarBinomial(
                        RandomConfig.TREASURE_ITEMS_N,
                        RandomConfig.TREASURE_ITEMS_P
                );
                break;
            case STORE:
                // Na loja não há inimigos, e geramos um nº fixo de itens (ex: 3)
                enemies = 0;
                items = RandomConfig.STORE_ITEMS_N;
                break;
        }

        // Gera portas (igual ao anterior)
        for (Room room : nextOptions) {
            if (room.getType() == RoomType.BOSS) {
                return new Room(id, type, enemies, items, 1, boss);
            }
        }
        int doors = RandomConfig.MIN_DOORS +
                random.nextInt(RandomConfig.MAX_DOORS - RandomConfig.MIN_DOORS + 1);
        return new Room(id, type, enemies, items, doors, boss);
    }

    // Método que escolhe o tipo de sala aleatoriamente
    private Room generateRoom() {
        RoomType type = null;

        if (numberOfRooms < 6) {
            // Gera um número entre 0.0 e 1.0
            float chance = Distribuicoes.gerarUniforme(0, 100) / 100f;

            if (chance < RandomConfig.PROB_STORE_ROOM) {
                type = RoomType.STORE;
            } else if (chance < RandomConfig.PROB_TREASURE_ROOM + RandomConfig.PROB_STORE_ROOM) {
                type = RoomType.TREASURE; // Caiu nos 10% do tesouro
            } else {
                type = RoomType.COMBAT; // Restante é combate
            }
        } else {
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
    public void addMoreRooms(){ numberOfRooms++; }
    public int getNextBossIndex() { return nextBossIndex; }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }
}
