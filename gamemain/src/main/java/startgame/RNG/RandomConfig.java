package startgame.RNG;

public final class RandomConfig {

    private RandomConfig() {}

    // Poisson: nº médio de inimigos por sala
    public static final double ENEMIES_LAMBDA = 5.0;

    // Prob próxima sala TESOURO vs COMBATE (Binomial n=1)
    public static final float PROB_TREASURE_ROOM = 0.30f; // 30% tesouro, 70% combate

    // Nº total de bosses (para a permutação)
    public static final int TOTAL_BOSSES = 3;

    // Nº de portas possíveis
    public static final int MIN_DOORS = 2;
    public static final int MAX_DOORS = 3;

    // Parâmetros para itens (Binomial)
    public static final int TREASURE_ITEMS_N = 15;
    public static final float TREASURE_ITEMS_P = 0.7f;

    public static final int COMBAT_ITEMS_N = 8;
    public static final float COMBAT_ITEMS_P = 0.3f;
}
