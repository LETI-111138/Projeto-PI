package startgame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class gameinit extends ApplicationAdapter {
        Texture imagem;
        SpriteBatch batch;
        BitmapFont font;
        OrthographicCamera camera;
        Animation<TextureRegion> animation;
        Texture mc;
        float stateTime;
        private static final int FRAME_COLS = 6;
        private static final int FRAME_ROWS = 1;

        float x;
        float y;
        float velocidade = 80;

        // Onde inicializamos variáveis (V.A.s, imagens, sons)
        @Override
        public void create() {
            batch = new SpriteBatch();
            camera = new OrthographicCamera();
            camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.zoom = 0.37f;
            mc = new Texture(Gdx.files.internal("assets/mc_pj_pi.png"));
            mc.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            TextureRegion[][] tmp = TextureRegion.split(mc, mc.getWidth() / FRAME_COLS, mc.getHeight() / FRAME_ROWS);
            TextureRegion[] animationFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
            int index = 0;
            for (int i = 0; i < FRAME_ROWS; i++) {
                for (int j = 0; j < FRAME_COLS; j++) {
                    animationFrames[index++] = tmp[i][j];
                }
            }
            animation = new Animation<TextureRegion>(0.1f, animationFrames);
            stateTime = 0f;
            x = 100;
            y = 100;
            try {
                imagem = new Texture("assets/wdsxp.jpg");
            } catch (Exception e) {
                System.err.println("Erro: Não foi possível encontrar a imagem. Verifica a pasta assets!");
            }
            font = new BitmapFont(); // Fonte padrão do sistema
            font.getData().setScale(2);
        }

        // O Game Loop (chamado 60x por segundo)
        @Override
        public void render() {
            // 1. Limpar o ecrã (Fundo Azul Escuro)
            Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            float delta = Gdx.graphics.getDeltaTime();
            stateTime += delta;


            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                x -= velocidade * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                x += velocidade * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                y += velocidade * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                y -= velocidade * delta;
            }

            TextureRegion cFrame = animation.getKeyFrame(stateTime, true);

            if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
                camera.zoom -= 1.0f * delta; // Aproximar
            }
            if (Gdx.input.isKeyPressed(Input.Keys.X)) {
                camera.zoom += 1.0f * delta; // Afastar
            }
            camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, 5.0f);
            camera.position.set(x + cFrame.getRegionWidth()/2f, y  + cFrame.getRegionHeight()/2f, 0);
            camera.update();
            batch.setProjectionMatrix(camera.combined);
            // 2. Desenhar elementos
            batch.begin();
            if (imagem != null) batch.draw(imagem, 100, 100);
            if (imagem != null) batch.draw(imagem, 2000, 100);
            if (cFrame != null) batch.draw(cFrame, (int)x, (int)y);
            batch.end();
        }

        // Limpar memória ao fechar
        @Override
        public void dispose() {
            batch.dispose();
            if (imagem != null || mc != null) batch.dispose();
        }
    }

