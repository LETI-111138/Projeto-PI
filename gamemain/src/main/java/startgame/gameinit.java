package startgame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class gameinit extends ApplicationAdapter {
        Texture imagem;
        SpriteBatch batch;
        BitmapFont font;
        Texture mc;

        float x;
        float y;
        float velocidade = 200;

        // Onde inicializamos variáveis (V.A.s, imagens, sons)
        @Override
        public void create() {
            batch = new SpriteBatch();
            mc = new Texture(Gdx.files.internal("assets/mc_pj_pi.gif"));
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
            // 2. Desenhar elementos
            batch.begin();
            if (imagem != null) batch.draw(imagem, 100, 100);
            if (mc != null) batch.draw(mc, x, y);
            batch.end();
        }

        // Limpar memória ao fechar
        @Override
        public void dispose() {
            batch.dispose();
            if (imagem != null || mc != null) batch.dispose();
        }
    }

