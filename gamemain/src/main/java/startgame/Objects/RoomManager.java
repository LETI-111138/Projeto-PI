package startgame.Objects;

import startgame.RNG.BossIndex;
import startgame.RNG.Distribuicoes;
import startgame.RNG.RandomConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomManager {

    private int numberOfRooms;
    private int previousNumberOfRooms;
    private final Random random = new Random();

    // Ordem aleatória dos bosses
    private final List<BossIndex> bossOrder = new ArrayList<>();
    private int nextBossIndex = 0;

    private int nextRoomId = 1;

    private Room currentRoom;
    private List<Room> nextOptions = new ArrayList<>();

    public RoomManager() {
        generateBossOrder();
        currentRoom = generateStartingRoom();
        this.numberOfRooms = 1;
        this.previousNumberOfRooms = 0;
        nextOptions = generateNextRooms();

    }

    private void generateBossOrder() {
        bossOrder.clear();
        for (int i = 0; i < RandomConfig.TOTAL_BOSSES; i++) {
            bossOrder.add(BossIndex.getByIndex(i));
        }
        Distribuicoes.gerarPermutacao(bossOrder);
    }

    private Room generateStartingRoom() {
        return generateRoom();
    }

    // Este método cria o objeto Room
    private Room generateRoom(RoomType type) {
        int id = nextRoomId++;
        System.out.println("previousNumberOfRooms: " + previousNumberOfRooms);
    // Incrementa o contador de salas

        BossIndex boss = null;
        int enemies = 0;
        int items = 0;

        // Configuração de Boss
        if (type == RoomType.BOSS) {
            if (nextBossIndex < bossOrder.size()) {
                boss = bossOrder.get(nextBossIndex++);
            } else {
                type = RoomType.COMBAT; // Se acabaram os bosses, volta a combate
            }
        }

        // Configuração de Inimigos/Itens
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
                enemies = 0;
                items = RandomConfig.STORE_ITEMS_N;
                break;
        }

        // --- LÓGICA DE PORTAS ---
        int doors;

        // Se a sala atual for BOSS, geralmente tem 1 porta para sair/acabar
        // Se estamos na sala 6 (limite definido no outro método), a PRÓXIMA será Boss.
        // Logo, esta sala deve ter obrigatoriamente 1 porta para afunilar o jogador.
        if (previousNumberOfRooms >= 5) {
            doors = 1;
        } else {
            doors = RandomConfig.MIN_DOORS +
                    random.nextInt(RandomConfig.MAX_DOORS - RandomConfig.MIN_DOORS + 1);
        }

        return new Room(id, type, enemies, items, doors, boss);
    }

    // Este método decide o TIPO da próxima sala
    private Room generateRoom() {
        RoomType type = null;
        System.out.println("numberOfRooms: " + numberOfRooms);
        // Se ainda não chegámos ao limite (6 salas), gera salas normais
        if (numberOfRooms < 6) {
            float chance = Distribuicoes.gerarUniforme(0, 100) / 100f;

            if (chance < RandomConfig.PROB_STORE_ROOM) {
                type = RoomType.STORE;
            } else if (chance < RandomConfig.PROB_TREASURE_ROOM + RandomConfig.PROB_STORE_ROOM) {
                type = RoomType.TREASURE;
            } else {
                type = RoomType.COMBAT;
            }
        } else {
            // Atingiu o limite, gera BOSS e reinicia a contagem
            type = RoomType.BOSS;
        }

        return generateRoom(type);
    }

    // Gera as opções de salas seguintes baseadas nas portas da sala atual
    private List<Room> generateNextRooms() {
        List<Room> list = new ArrayList<>();
        previousNumberOfRooms++;
        numberOfRooms++;
        // CORREÇÃO CRÍTICA:
        // O número de salas geradas TEM de ser igual ao número de portas da sala atual.
        // Isto impede o erro "Index inválido" ao clicar numa porta.
        int numDoors = currentRoom != null ? currentRoom.getNumberOfDoors() : 1;

        if(numberOfRooms>= 6 && previousNumberOfRooms >= 5) {
            int numDoorsBoss=1;
            for (int i = 0; i < numDoorsBoss; i++) {
                list.add(generateRoom());
            }
            numberOfRooms=1;
            previousNumberOfRooms=0;
        }else {
            for (int i = 0; i < numDoors; i++) {
                list.add(generateRoom());
            }
        }

        return list;
    }

    public Room goToNextRoom(int optionIndex) {
        if(nextOptions.size()<=1){
            currentRoom = nextOptions.get(0);
            nextOptions = generateNextRooms();
        }else{
            if (optionIndex < 0 || optionIndex >= nextOptions.size()) {
                throw new IllegalArgumentException("Invalid room option index: " + optionIndex);
            }
            currentRoom = nextOptions.get(optionIndex);

            // Gera as próximas opções baseadas na nova sala atual
            nextOptions = generateNextRooms();
        }
        return currentRoom;
    }

    // Getters
    public Room getCurrentRoom() { return currentRoom; }
    public List<Room> getNextOptions() { return nextOptions; }
    public List<BossIndex> getBossOrder() { return bossOrder; }
    public void addMoreRooms(){ numberOfRooms++; }
    public int getNextBossIndex() { return nextBossIndex; }
    public int getNumberOfRooms() { return numberOfRooms; }
}