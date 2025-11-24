package startgame.RNG;


import java.util.Random;

public class Distribuicoes {

    private static Random random = new Random();

    /**
     * Gera um número aleatório segundo a Distribuição de Poisson.
     * Algoritmo de Knuth (ideal para simulações simples).
     * @param lambda A média de ocorrências esperada (ex: 5 inimigos).
     * @return O número de eventos (inimigos) gerado.
     */
    public static int gerarPoisson(double lambda) {
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= random.nextDouble(); // Gera Uniforme [0, 1]
        } while (p > L);

        return k - 1;
    }

    /**
     * Gera um número aleatório segundo a Distribuição Uniforme Contínua.
     * @param min Valor mínimo.
     * @param max Valor máximo.
     * @return Um valor float entre min e max.
     */
    public static float gerarUniforme(float min, float max) {
        // Fórmula: X = min + (max - min) * U
        return min + (max - min) * random.nextFloat();
    }
}