package startgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
public class gameinit extends ApplicationAdapter {



        SpriteBatch batch;
        BitmapFont font;

        // Onde inicializamos variáveis (V.A.s, imagens, sons)
        @Override
        public void create() {
            batch = new SpriteBatch();
            font = new BitmapFont(); // Fonte padrão do sistema
            font.getData().setScale(2);
        }

        // O Game Loop (chamado 60x por segundo)
        @Override
        public void render() {
            // 1. Limpar o ecrã (Fundo Azul Escuro)
            Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // 2. Desenhar elementos
            batch.begin();
            font.draw(batch, "Simulador PI - LibGDX", 100, 400);

            // Exemplo: Mostrar valor de uma V.A. (Lógica que vocês vão criar)
            // font.draw(batch, "V.A. Gerada: " + valorVariavel, 100, 350);

            batch.end();
        }

        // Limpar memória ao fechar
        @Override
        public void dispose() {
            batch.dispose();
            font.dispose();
        }
    }

