package startgame.Objects;

import com.badlogic.gdx.math.MathUtils;
import startgame.Position;
import startgame.gameinit;

public class BlueDroplet extends Enemy{

    // --- Variáveis para a lógica do Salto ---
    private float tempoDesdeUltimoSalto = 0f;
    private final float INTERVALO_SALTO = 3.0f; // O boss salta a cada 3 segundos
    private boolean isJumping = false;
    private float tempoDeSaltoAtual = 0f;
    private final float DURACAO_SALTO = 0.5f;   // O salto demora 0.5 segundos
    private final float VELOCIDADE_SALTO = 350f;

    // Vetores de direção para o salto
    private float jumpDirX = 0f;
    private float jumpDirY = 0f;// Velocidade durante o salto (muito mais rápido)

    public BlueDroplet(Position position) {
        super(position, 250, 32, 90f);
        this.putKeys("bluedroplet");
        mudarDirecao();
    }

    public void move(){
        float delta = Mc.getInstance().getDelta();

        // 1. Atualizar o temporizador global do salto
        if (!isJumping) {
            tempoDesdeUltimoSalto += delta;

            // Verifica se está na hora de saltar
            if (tempoDesdeUltimoSalto >= INTERVALO_SALTO) {
                iniciarSalto();
            }
        }

        // 2. Lógica de Movimento
        if (isJumping) {
            executarSalto(delta);
        } else {
            movimentoPadrao(delta);
        }

        // 3. Manter dentro dos limites do mundo (Válido para ambos os estados)
        limitarLimitesMundo();
    }

    private void iniciarSalto() {
        isJumping = true;
        tempoDeSaltoAtual = 0f;
        tempoDesdeUltimoSalto = 0f;

        // Posição do Jogador
        float playerX = Mc.getInstance().getPosition().getX();
        float playerY = Mc.getInstance().getPosition().getY();

        // A nossa posição atual (BlueDroplet)
        float myX = this.getPosition().getX();
        // float myY = this.getPosition().getY(); // Não é estritamente necessário declarar se usarmos this.getPosition().getY()

        // Diferença (necessário para saber a direção X e Y independentemente)
        float dx = playerX - myX;
        float dy = playerY - this.getPosition().getY();

        // --- ALTERAÇÃO AQUI ---
        // Usamos o método distanceTo da classe Position em vez de Math.sqrt manual
        float distancia = this.getPosition().distanceTo(playerX, playerY);

        // Normalizar o vetor (dx/distancia, dy/distancia)
        if (distancia != 0) {
            jumpDirX = dx / distancia;
            jumpDirY = dy / distancia;
        } else {
            // Caso raro onde o boss está exatamente em cima do jogador (evita divisão por zero)
            jumpDirX = 0;
            jumpDirY = 0;
        }

        System.out.println("BlueDroplet: SALTANDO! Distância: " + distancia);
    }

    private void executarSalto(float delta) {
        tempoDeSaltoAtual += delta;

        // Move na direção calculada com velocidade de salto
        float x = this.getPosition().getX();
        float y = this.getPosition().getY();

        x += jumpDirX * VELOCIDADE_SALTO * delta;
        y += jumpDirY * VELOCIDADE_SALTO * delta;

        this.getPosition().setX(x);
        this.getPosition().setY(y);

        // Verifica se o salto acabou
        if (tempoDeSaltoAtual >= DURACAO_SALTO) {
            isJumping = false;
            mudarDirecao(); // Reseta a direção aleatória ao aterrar
        }
    }

    // Este é o teu código original de movimento, encapsulado num método auxiliar
    private void movimentoPadrao(float delta) {
        // float stateTime = gameinit.getStateTime(); // Não estava a ser usado no move original, mas podes manter se precisares
        float x = this.getPosition().getX();
        float y = this.getPosition().getY();

        this.addTempoDecorrido(delta);

        if (this.getTempoDecorrido() >= this.getTempoParaMudarDirecao()) {
            mudarDirecao();
        }

        switch (this.getDirecaoAtual()) {
            case 1: x -= this.getVelocidade() * delta; break; // Esquerda
            case 2: x += this.getVelocidade() * delta; break; // Direita
            case 3: y += this.getVelocidade() * delta; break; // Cima
            case 4: y -= this.getVelocidade() * delta; break; // Baixo
            // case 0: Fica parado
        }

        this.getPosition().setX(x);
        this.getPosition().setY(y);
    }

    private void limitarLimitesMundo() {
        float x = this.getPosition().getX();
        float y = this.getPosition().getY();

        // Verificar se temos frame atual para obter largura/altura
        float w = 0;
        float h = 0;
        if (getCurrentFrame() != null) {
            w = getCurrentFrame().getRegionWidth();
            h = getCurrentFrame().getRegionHeight();
        }

        x = MathUtils.clamp(x, 0, gameinit.getLarguraMundo() - w);
        y = MathUtils.clamp(y, 0, gameinit.getAlturaMundo() - h);

        this.getPosition().setX(x);
        this.getPosition().setY(y);
    }
}
