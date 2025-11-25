package startgame.RNG;


import java.util.Random;

public class Distribuicoes {

    // Classe do java random ja que é mais versátil que o método math.random()
    private static Random random = new Random();

    /**
     * Gera um número aleatório segundo a Distribuição de Poisson.
     * Algoritmo de Knuth (ideal para simulações simples).
     * @param lambda A média de ocorrências esperada (ex: 5 inimigos).
     * @return O número de eventos (inimigos) gerado.
     * V.A Discreta
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
     * Gera um número aleatório segundo a Distribuição Uniforme Contínua || V.A Contínua
     */
    public static float gerarUniforme(int min, int max) {
        // Fórmula: X = min + (max - min) * U
        float x = 0;
            do{
                float p= random.nextFloat();
                System.out.println(p);
               x =  min + (max - min) * p;
            } while (x >= 2000 || x <= 0);
               System.out.println(x);
        return x;
    }

    public static int gerarBinomial(int n, float p) {
        int sucessos = 0;
        for (int i = 0; i < n; i++) {
            if (random.nextFloat() < p) {
                sucessos++;
            }
        }
        return sucessos;
    }
}