package startgame.RNG;

public final class RandomConfig {

    private RandomConfig() {}

    // Poisson: nº médio de inimigos por sala
    public static final double ENEMIES_LAMBDA = 5.0;

    // Prob próxima sala TESOURO vs COMBATE (Binomial n=1)
    public static final float PROB_TREASURE_ROOM = 0.30f; // 30% tesouro, 70% combate

    // Nº total de bosses (para a permutação)
    public static final int TOTAL_BOSSES = 2;

    // Nº de portas possíveis
    public static final int MIN_DOORS = 2;
    public static final int MAX_DOORS = 3;

    // Parâmetros para itens (Binomial)
    public static final int TREASURE_ITEMS_N = 15;
    public static final float TREASURE_ITEMS_P = 0.7f;

    // Configurações de Dano Crítico do Jogador
    public static final float PROB_CRITICO = 0.20f;       // 20% de chance de dar crítico
    public static final float CRITICO_MEDIA_MULT = 2.0f;  // Dano crítico médio é 2x o dano base
    public static final float CRITICO_DESVIO = 5.0f;

}
