package startgame.Objects;

import com.badlogic.gdx.math.MathUtils;
import startgame.Position;
import startgame.RNG.Distribuicoes;
import startgame.gameinit;

public class BossClown extends Enemy {

    // --- Variáveis de Controlo do Teleporte ---
    private float tempoDesdeUltimoTeleporte = 0f;
    private final float INTERVALO_TELEPORTE = 4.0f; // Teleporta a cada 4 segundos
    private final float DISTANCIA_DO_JOGADOR = 150f; // Distância a que aparece do jogador

    public BossClown(Position position) {
        super(position, 500, 30, 70f);
        this.putKeys("clownboss");
        mudarDirecao();
    }

    public void move() {
        float delta = Mc.getInstance().getDelta();

        // 1. Atualizar Temporizador de Teleporte
        tempoDesdeUltimoTeleporte += delta;

        // 2. Verificar se está na hora de teleportar
        if (tempoDesdeUltimoTeleporte >= INTERVALO_TELEPORTE) {
            teleportarParaPertoDoMc();
            tempoDesdeUltimoTeleporte = 0f; // Resetar temporizador
        }

        // 3. Movimento Normal (Enquanto não teleporta, ele anda)
        this.addTempoDecorrido(delta);

        if (this.getTempoDecorrido() >= this.getTempoParaMudarDirecao()) {
            mudarDirecao();
        }

        float x = this.getPosition().getX();
        float y = this.getPosition().getY();

        // Movimento aleatório padrão
        switch (this.getDirecaoAtual()) {
            case 1:
                x -= this.getVelocidade() * delta;
                break; // Esquerda
            case 2:
                x += this.getVelocidade() * delta;
                break; // Direita
            case 3:
                y += this.getVelocidade() * delta;
                break; // Cima
            case 4:
                y -= this.getVelocidade() * delta;
                break; // Baixo
        }

        // Garantir que não sai do mapa durante o movimento normal
        atualizarPosicaoComLimites(x, y);
    }

    private void teleportarParaPertoDoMc() {
        // Obter posição do Jogador
        float mcX = Mc.getInstance().getPosition().getX();
        float mcY = Mc.getInstance().getPosition().getY();

        // Gerar um ângulo aleatório (0 a 360 graus)
        float angulo = Distribuicoes.gerarUniforme(0, 360);

        // Calcular deslocamento (offset) baseado no ângulo e distância fixa
        // MathUtils.cosDeg e sinDeg aceitam graus
        float offsetX = MathUtils.cosDeg(angulo) * DISTANCIA_DO_JOGADOR;
        float offsetY = MathUtils.sinDeg(angulo) * DISTANCIA_DO_JOGADOR;

        // Nova posição alvo
        float novoX = mcX + offsetX;
        float novoY = mcY + offsetY;

        System.out.println("BossClown: *POOF* Teleportado!");

        // Aplicar a posição garantindo que não cai fora do mundo
        atualizarPosicaoComLimites(novoX, novoY);

        // Opcional: Mudar logo de direção após o teleporte para surpreender
        mudarDirecao();
    }

    // Método auxiliar para setar X e Y respeitando os limites do mundo
    private void atualizarPosicaoComLimites(float x, float y) {
        float w = 0;
        float h = 0;

        if (getCurrentFrame() != null) {
            w = getCurrentFrame().getRegionWidth();
            h = getCurrentFrame().getRegionHeight();
        }

        // Clamp (prender) valores entre 0 e o tamanho do mundo
        x = MathUtils.clamp(x, 0, gameinit.getLarguraMundo() - w);
        y = MathUtils.clamp(y, 0, gameinit.getAlturaMundo() - h);

        this.getPosition().setX(x);
        this.getPosition().setY(y);
    }

}
