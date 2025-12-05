package startgame.Objects;

import startgame.RNG.BossIndex;
import startgame.RNG.Distribuicoes;
import startgame.RNG.RandomConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RoomManager {

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
    }

    private void generateBossOrder() {
        bossOrder.clear();
        for (int i = 0; i < RandomConfig.TOTAL_BOSSES; i++) {
            bossOrder.add(BossIndex.getByIndex(i));
            System.out.println(i);
        }

        // SUBSTITUI O Collections.shuffle PELA TUA IMPLEMENTAÇÃO:
        Distribuicoes.gerarPermutacao(bossOrder);

        // Agora tens uma permutação uniforme validada pelo teu próprio código RNG
    }

    private Room generateStartingRoom() {
        // primeira sala: combate normal
        return generateRoom(RoomType.COMBAT);
    }

    private Room generateRoom(RoomType type) {
        int id = nextRoomId++;

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

        if (type == RoomType.COMBAT || type == RoomType.BOSS) {
            // Poisson: nº inimigos
            enemies = Math.max(1, Distribuicoes.gerarPoisson(RandomConfig.ENEMIES_LAMBDA));
            // Binomial: nº itens em sala de combate
            items = Distribuicoes.gerarBinomial(
                    RandomConfig.COMBAT_ITEMS_N,
                    RandomConfig.COMBAT_ITEMS_P
            );
        } else if (type == RoomType.TREASURE) {
            enemies = 0;
            // Binomial: nº itens em sala tesouro
            items = Distribuicoes.gerarBinomial(
                    RandomConfig.TREASURE_ITEMS_N,
                    RandomConfig.TREASURE_ITEMS_P
            );
        }

        // 2 ou 3 portas
        int doors = RandomConfig.MIN_DOORS +
                random.nextInt(RandomConfig.MAX_DOORS - RandomConfig.MIN_DOORS + 1);

        return new Room(id, type, enemies, items, doors, boss);
    }

    private Room generateRoom() {
        // Binomial com n=1 (Bernoulli) para tipo de sala
        int bernoulli = Distribuicoes.gerarBinomial(1, RandomConfig.PROB_TREASURE_ROOM);
        RoomType type = (bernoulli == 1) ? RoomType.TREASURE : RoomType.COMBAT;

        // MAIS TARDE: podes meter regra para às X salas ser BOSS
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
}
