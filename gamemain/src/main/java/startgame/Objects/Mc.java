package startgame.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import startgame.Position;
import startgame.RNG.Distribuicoes;
import startgame.RNG.RandomConfig;

public class Mc extends Character{

    private int balanceCoins;
    private static Mc INSTANCE;
    private static float velocidade;
    float delta;
    private float attackTimer = 0f;
    private boolean isAttacking = false;
    Music mcwalk = null;

    // Variáveis do DASH
    private boolean isDashing = false;
    private float dashTimer = 0f;
    private float dashCooldown = 0f;

    // Configuração
    private final float DASH_DURATION = 0.2f;
    private final float DASH_SPEED = 600f;
    private final float DASH_COOLDOWN_TIME = 1.0f;

    // Direção para onde o dash vai (X e Y)
    private float dashDirX = 0f;
    private float dashDirY = 0f;


    public Mc(Position position) {
        super(position, 200, 10);
        this.putKeys("player");
        balanceCoins = 5;
        velocidade = 80f;
        delta = Gdx.graphics.getDeltaTime();
        mcwalk = Gdx.audio.newMusic(Gdx.files.internal("assets/Sound/mcwalk.mp3"));
    }

    public static Mc getInstance(){
        if(INSTANCE==null)INSTANCE = new Mc(new Position(1000,1000));
        return INSTANCE;
    }

    public void startGameAttackTimer(){
        attackTimer = 0f;
        isAttacking = true;
    }
    public void updateAttackTimer(float delta) {
        if (isAttacking==true) {
            attackTimer += delta;
        }
    }
    public float getAttackTimer() { return attackTimer; }
    public void startAttack() { isAttacking = true; }

    public boolean isAttacking() { return isAttacking; }

    public void stopAttack() {
        isAttacking = false;
        attackTimer = 0f;
    }

    public int getBalanceCoins() {return balanceCoins;}

    public void setBalanceCoins(int balanceCoins) {this.balanceCoins = balanceCoins;}

    public void addBalanceCoins(int balanceCoins) {this.balanceCoins += balanceCoins;}

    public void removeBalanceCoins(int balanceCoins) {this.balanceCoins -= balanceCoins;}

    public void setDelta(float newdelta){ delta = newdelta;}

    public float getDelta(){return delta;}

    public void setVelocidade(float velocidade) {this.velocidade = velocidade;}

    public void addVelocidade(float add) {velocidade += add;}

    public float getVelocidade() {return velocidade;}

    public void move() {
        // 1. Atualizar Cooldown do Dash
        if (dashCooldown > 0) {
            dashCooldown -= delta; // Certifica-te que tens acesso ao delta aqui
        }

        // 2. Verificar se carregou no Espaço para iniciar Dash
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && dashCooldown <= 0 && !isDashing) {
            // Só faz dash se estivermos a andar nalguma direção
            if (dashDirX != 0 || dashDirY != 0) {
                startDash();
            }
        }

        // 3. LÓGICA DE MOVIMENTO
        if (isDashing) {
            // --- MODO DASH (Ignora o teu som e teclas, move sozinho) ---
            dashTimer -= delta;

            // Move muito rápido na direção que estava guardada
            this.getPosition().addX(dashDirX * DASH_SPEED * delta);
            this.getPosition().addY(dashDirY * DASH_SPEED * delta);

            // Se o tempo acabou, para o dash
            if (dashTimer <= 0) {
                isDashing = false;
            }

        } else {
            // --- MODO NORMAL (O TEU CÓDIGO AQUI) ---

            // Resetamos a direção do dash a 0. Assim, se parares de carregar nas teclas,
            // o boneco sabe que está parado e não dá dash.
            dashDirX = 0;
            dashDirY = 0;

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                this.getPosition().rmX(velocidade * delta);
                mcwalk.setVolume(1f);
                mcwalk.play();

                dashDirX = -1; // <--- ADICIONADO: Guarda que estamos a ir para a Esquerda
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                this.getPosition().addX(velocidade * delta);
                mcwalk.setVolume(1f);
                mcwalk.play();

                dashDirX = 1;  // <--- ADICIONADO: Guarda que estamos a ir para a Direita
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                this.getPosition().addY(velocidade * delta);
                mcwalk.setVolume(1f);
                mcwalk.play();

                dashDirY = 1;  // <--- ADICIONADO: Guarda que estamos a ir para Cima
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                this.getPosition().rmY(velocidade * delta);
                mcwalk.setVolume(2f);
                mcwalk.play();

                dashDirY = -1; // <--- ADICIONADO: Guarda que estamos a ir para Baixo
            }

            // Pequena correção matemática para o dash não ser super rápido na diagonal:
            if (dashDirX != 0 && dashDirY != 0) {
                // Normaliza apenas a direção do dash (não mexemos na tua velocidade de andar normal)
                float length = (float) Math.sqrt(dashDirX * dashDirX + dashDirY * dashDirY);
                dashDirX /= length;
                dashDirY /= length;
            }
        }
    }

    public float getatkDMc() {
        if (Distribuicoes.gerarUniforme(0, 1) < RandomConfig.PROB_CRITICO) {

            // --- CÁLCULO DO DANO CRÍTICO (V.A. NORMAL) ---

            // Média: O dobro do ataque base (ex: se atk=10, média=20)
            float media = this.getatkD() * RandomConfig.CRITICO_MEDIA_MULT;

            // Desvio Padrão: Variação do dano (ex: 5.0)
            float desvio = RandomConfig.CRITICO_DESVIO;

            // Gerar o dano crítico usando a Distribuição Normal
            float danoCritico = Distribuicoes.gerarNormal(media, desvio);

            System.out.println("CRITICAL HIT! Dano Gerado (Normal): " + danoCritico);

            // Retorna o valor arredondado (garantindo que é pelo menos o ataque base)
            return Math.max(this.getatkD(), Math.round(danoCritico));
        }

        // Ataque normal (sem crítico)
        return this.getatkD();
    }

    private void startDash() {
        isDashing = true;
        dashTimer = DASH_DURATION;
        dashCooldown = DASH_COOLDOWN_TIME;
        System.out.println("DASH!");
    }

    // Getter necessário para o gameinit saber se és invulnerável
    public boolean isDashing() {
        return isDashing;
    }



}
