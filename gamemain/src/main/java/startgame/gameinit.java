package startgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import startgame.Objects.*;

import java.util.ArrayList;
import java.util.HashMap;

public class gameinit extends ApplicationAdapter {

    SpriteBatch batch;
    OrthographicCamera camera;

    // --- GESTORES DE IMAGENS ---
    StaticImage gestorEstatico;
    AnimatedImage gestorAnimado;

    // Variáveis para o Jogador (MC)
    HashMap<String,Animation<TextureRegion>> animPlayer;
    float stateTime = 0f;
    float x = 1000, y = 1000;
    float velocidade = 80;

    //Lista com animações de assets animados
    Animation<TextureRegion> animBoss;

    final int LARGURA_MUNDO = 2000;
    final int ALTURA_MUNDO = 2000;

    @Override
    public void create() {
        batch = new SpriteBatch();
        animPlayer = new HashMap<>();
        // 1. INICIALIZAR GESTORES
        gestorEstatico = new StaticImage(); // Carrega automaticamente as imagens estáticas para o objeto
        gestorAnimado = new AnimatedImage();


        gestorAnimado.criarAnimacao("mc_pj_pi.png", "player", 6, 1, 0.1f);

        // Guardar no mapa (agora já não dá erro)
        animPlayer.put("player", gestorAnimado.getAnimacao("player"));

        // Inimigo esqueleto (exemplo para visualização do procedimento de chamada das animações para o GUI)
        gestorAnimado.criarAnimacao("skeleton-Sheet.png", "skeleton", 8, 1, 0.1f);

        // Atenção: Aqui tinha um erro de copy-paste, estava a ir buscar "player" em vez de "skeleton"
        animPlayer.put("skeleton", gestorAnimado.getAnimacao("skeleton"));

        // 3. CONFIGURAR CÂMARA
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Ou Gdx.graphics.getWidth()
        camera.zoom = 0.5f;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;

        // --- MOVIMENTO ---
        if (Gdx.input.isKeyPressed(Input.Keys.A)) x -= velocidade * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) x += velocidade * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) y += velocidade * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) y -= velocidade * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
            System.exit(0);
        }

        // --- OBTER FRAMES ATUAIS ---
        TextureRegion frameEnemy = animPlayer.get("skeleton").getKeyFrame(stateTime, true);

        // --- CÂMARA ---
        //x = MathUtils.clamp(x, 0, LARGURA_MUNDO - frameEnemy.getRegionWidth());
        //y = MathUtils.clamp(y, 0, ALTURA_MUNDO - frameEnemy.getRegionHeight());


        // --- OBTER FRAMES ATUAIS ---
        TextureRegion framePlayer = animPlayer.get("player").getKeyFrame(stateTime, true);

        // --- CÂMARA ---
        x = MathUtils.clamp(x, 0, LARGURA_MUNDO - framePlayer.getRegionWidth());
        y = MathUtils.clamp(y, 0, ALTURA_MUNDO - framePlayer.getRegionHeight());



        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            camera.zoom -= 1.0f * delta; // Aproximar camera do personagem
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X) && camera.zoom <= 0.50f) {
            camera.zoom += 1.0f * delta; // Afastar camara do personagem
        }

        camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, 5.0f);
        camera.position.set(x + framePlayer.getRegionWidth()/2f, y  + framePlayer.getRegionHeight()/2f, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // 1. Desenhar Fundo (Estático)
        // Usa o nome do ficheiro sem extensão como chave
        if (gestorEstatico.getTexture("mapvoid") != null) {
            batch.draw(gestorEstatico.getTexture("mapvoid"), -1000, -1000);
        }
        if (gestorEstatico.getTexture("mapa1") != null) {
            batch.draw(gestorEstatico.getTexture("mapa1"), 0, 0);
        }

        if (gestorEstatico.getTexture("Basedoor") != null) {
            batch.draw(gestorEstatico.getTexture("Basedoor"), 350, 350);
        }

        // 3. Desenhar Jogador
        if (framePlayer != null) {
            batch.draw(framePlayer, (int)x, (int)y);
        }

        // 2. Desenhar Boss (Exemplo de posição fixa)
        if (frameEnemy != null) {
            batch.draw(frameEnemy, 700, 700);
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        gestorEstatico.dispose();
        gestorAnimado.dispose();
    }
}