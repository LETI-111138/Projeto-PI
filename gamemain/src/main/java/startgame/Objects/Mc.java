package startgame.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import startgame.AnimatedImage;
import startgame.Position;
import startgame.RNG.Distribuicoes;
import startgame.RNG.RandomConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Mc extends Character{

    private int balanceCoins;
    private static Mc INSTANCE;
    private float velocidade;
    float delta;
    private float attackTimer = 0f;
    private boolean isAttacking = false;
    Music mcwalk = null;

    public Mc(Position position) {
        super(position, 200, 100);
        this.putKeys("player");
        balanceCoins = 0;
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

    public void setVelocidade(float velocidade) {this.delta = velocidade;}

    public float getVelocidade() {return velocidade;}

    public void move(){

        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            this.getPosition().rmX(velocidade * delta);
            mcwalk.setVolume(1f);
            mcwalk.play();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            this.getPosition().addX(velocidade * delta);
            mcwalk.setVolume(1f);
            mcwalk.play();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            this.getPosition().addY (velocidade * delta);
            mcwalk.setVolume(1f);
            mcwalk.play();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            this.getPosition().rmY (velocidade * delta);
            mcwalk.setVolume(1f);
            mcwalk.play();
        }

    }

    public int getatkDMc() {
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

}
