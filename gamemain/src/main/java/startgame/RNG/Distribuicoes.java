package startgame.RNG;


import java.util.Random;
import java.util.List;
public class Distribuicoes {

    // Classe do java random já que é mais versátil que o método math.random()
    private static Random random = new Random();

    /**
     * V.A Discreta
     * Gera um número aleatório segundo a Distribuição de Poisson.
     * @param lambda A média de ocorrências esperada, 5 no caso da utilização para n.º de inimigos da sala.
     * @return O número de inimigos gerados.
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
        float x = 0;
                float p= random.nextFloat();
                System.out.println(p);
               x =  min + (max - min) * p;
               System.out.println(x);
        return x;
    }

    // V.A. Discreta: Binomial
    // Numero de itens aleatórios numa sala de tesouro (TREASURE ROOM)
    public static int gerarBinomial(int n, float p) {
        int sucessos = 0;
        for (int i = 0; i < n; i++) {
            if (random.nextFloat() < p) {
                sucessos++;
            }
        }
        return sucessos;
    }

    /**
     * V.A. Contínua: Exponencial
     * Tempo de Movimentação Aleatoria de Inimigos
     */
    public static float gerarExponencial(double lambda) {
        // Fórmula: X = -ln(1-U) / lambda
        return (float) (-Math.log(1 - random.nextDouble()) / lambda);
    }

    /**
     * V.A. Contínua: Normal (Box-Muller Transform)
     * Valor aleatorio de dano critico
     */
    public static float gerarNormal(float media, float desvioPadrao) {
        // nextGaussian() usa a transformação de Box-Muller internamente
        return (float) (media + random.nextGaussian() * desvioPadrao);
    }


    /**
     * V.A. Discreta: Uniforme (Permutação)
     * Implementação do algoritmo Fisher-Yates Shuffle.
     * Gera uma permutação aleatória onde todas as ordens têm probabilidade igual (1/n!).
     * Reorganiza a ordem dos bosses do jogo
     */
    public static void gerarPermutacao(List<BossIndex> lista) {
        // Percorre a lista do fim para o início
        for (int i = lista.size() - 1; i > 0; i--) {
            // Escolhe um índice aleatório j entre 0 e i (inclusive)
            // Isto baseia-se numa Uniforme Discreta [0, i]
            int j = random.nextInt(i + 1);

            // Troca o elemento na posição i com o elemento na posição j
            BossIndex temp = lista.get(i);
            lista.set(i, lista.get(j));
            lista.set(j, temp);
        }
    }


}