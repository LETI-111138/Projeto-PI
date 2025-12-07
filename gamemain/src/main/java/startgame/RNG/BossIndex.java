package startgame.RNG;

public enum BossIndex {
BOSSCLOWN(0),BLUEDROPLET(1),FORESTOTEM(2);

    public int index;
    BossIndex(int index) {
        this.index = index;
    }
    public static BossIndex getByIndex(int index) {
        // Percorre todos os valores do Enum
        for (BossIndex boss : BossIndex.values()) {
            if (boss.index == index) {
                return boss;
            }
        }

        // Retorna null ou lança exceção se não encontrar
        throw new IllegalArgumentException("Index inválido: " + index);
    }

    public static int getBossIndex(BossIndex bossIndex) {
        return bossIndex.index;
    }

}
