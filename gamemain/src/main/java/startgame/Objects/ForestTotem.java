package startgame.Objects;

import com.badlogic.gdx.math.MathUtils;
import startgame.Position;
import startgame.gameinit;

public class ForestTotem extends Enemy{

    // --- Variáveis para o Ataque Diagonal ---
    private float tempoDesdeUltimoDash = 0f;
    private final float INTERVALO_DASH = 2.5f; // Tempo entre ataques
    private boolean isDashing = false;
    private float tempoDashAtual = 0f;
    private final float DURACAO_DASH = 0.8f;   // Duração do movimento diagonal
    private final float VELOCIDADE_DASH = 300f; // Velocidade rápida durante o ataque

    // Direções fixas para o dash (1 ou -1)
    private float dashDirX = 0f;
    private float dashDirY = 0f;

    public ForestTotem(Position position) {
        super(position, 200, 45, 78f);
        this.putKeys("ForestTotem");
        mudarDirecao();
    }

    public void move(){
        float delta = Mc.getInstance().getDelta();

        // 1. Gerir Estados
        if (isDashing) {
            executarDashDiagonal(delta);
        } else {
            // Conta o tempo enquanto vagueia
            tempoDesdeUltimoDash += delta;

            // Se chegou a hora, inicia o ataque
            if (tempoDesdeUltimoDash >= INTERVALO_DASH) {
                prepararDash();
            } else {
                movimentoVaguear(delta);
            }
        }

        // 2. Manter dentro do mapa (aplica-se a qualquer movimento)
        limitarLimitesMundo();
    }

    private void prepararDash() {
        isDashing = true;
        tempoDashAtual = 0f;
        tempoDesdeUltimoDash = 0f;

        // --- CALCULAR DIAGONAL ---
        // Para ser uma diagonal perfeita, X e Y têm de ter magnitude igual (ex: 1 e 1).
        // Apenas decidimos o sinal (+ ou -) com base na posição do jogador.

        float playerX = Mc.getInstance().getPosition().getX();
        float playerY = Mc.getInstance().getPosition().getY();
        float myX = this.getPosition().getX();
        float myY = this.getPosition().getY();

        // Se o jogador estiver à direita, dirX = 1, senão -1
        dashDirX = (playerX > myX) ? 1 : -1;

        // Se o jogador estiver em cima, dirY = 1, senão -1
        dashDirY = (playerY > myY) ? 1 : -1;

        System.out.println("ForestTotem: Dash Diagonal (" + dashDirX + ", " + dashDirY + ")!");
    }

    private void executarDashDiagonal(float delta) {
        tempoDashAtual += delta;

        float x = this.getPosition().getX();
        float y = this.getPosition().getY();

        // Move nas duas direções simultaneamente com a velocidade de ataque
        x += dashDirX * VELOCIDADE_DASH * delta;
        y += dashDirY * VELOCIDADE_DASH * delta;

        this.getPosition().setX(x);
        this.getPosition().setY(y);

        // Verificar fim do ataque
        if (tempoDashAtual >= DURACAO_DASH) {
            isDashing = false;
            mudarDirecao(); // Escolhe uma nova direção aleatória para vaguear
        }
    }

    private void movimentoVaguear(float delta) {
        // Lógica original de vaguear
        this.addTempoDecorrido(delta);

        if (this.getTempoDecorrido() >= this.getTempoParaMudarDirecao()) {
            mudarDirecao();
        }

        float x = this.getPosition().getX();
        float y = this.getPosition().getY();

        switch (this.getDirecaoAtual()) {
            case 1: x -= this.getVelocidade() * delta; break; // Esquerda
            case 2: x += this.getVelocidade() * delta; break; // Direita
            case 3: y += this.getVelocidade() * delta; break; // Cima
            case 4: y -= this.getVelocidade() * delta; break; // Baixo
            // case 0: Parado
        }

        this.getPosition().setX(x);
        this.getPosition().setY(y);
    }

    private void limitarLimitesMundo() {
        float w = 0;
        float h = 0;
        // Tenta obter tamanho real, senão usa padrão
        if (getCurrentFrame() != null) {
            w = getCurrentFrame().getRegionWidth();
            h = getCurrentFrame().getRegionHeight();
        } else {
            w = 50; h = 50;
        }

        float x = MathUtils.clamp(this.getPosition().getX(), 0, gameinit.getLarguraMundo() - w);
        float y = MathUtils.clamp(this.getPosition().getY(), 0, gameinit.getAlturaMundo() - h);

        this.getPosition().setX(x);
        this.getPosition().setY(y);
    }

}
