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
import startgame.RNG.Distribuicoes;

import java.util.ArrayList; // Import necessário


import java.util.HashMap;

public class gameinit extends ApplicationAdapter {

    SpriteBatch batch;
    OrthographicCamera camera;

    // --- GESTORES DE IMAGENS ---
    StaticImage gestorEstatico;
    AnimatedImage gestorAnimado;

    // --- HUD (INTERFACE) ---
    OrthographicCamera hudCamera;
    // Estatísticas para mostrar
    int vidaJogar = 100;
    int pontuacao = 0;

    // Variáveis para o Jogador (MC)
    HashMap<String,Animation<TextureRegion>> animAll;
    float stateTime = 0f;
    float x = 1000, y = 1000;
    float velocidade = 80;

    // Atributos em relação ao número de Itens no jogo e a sua probabilidade de aparecerem na sala
    int nItems = 0;
    ArrayList<Position> posItems;

    // Índice para controlar a direção (0=Baixo, 1=Esq, 2=Dir, 3=Cima - Ajusta à tua imagem!)
    ArrayList<Position> posicoesInimigos;


    final int LARGURA_MUNDO = 2000;
    final int ALTURA_MUNDO = 2000;


    @Override
    public void create() {
        batch = new SpriteBatch();
        animAll = new HashMap<>();
        // 1. INICIALIZAR GESTORES
        gestorEstatico = new StaticImage(); // Carrega automaticamente as imagens estáticas para o objeto
        gestorAnimado = new AnimatedImage();
        //Logica de VAs
        posicoesInimigos = new ArrayList<>();
        posItems = new ArrayList<>();
        hudCamera = new OrthographicCamera();

        gestorAnimado.criarAnimacao("mc_pj_pi.png", "player", 6, 1, 0.1f);
        animAll.put("player", gestorAnimado.getAnimacao("player"));

        // Inimigo esqueleto (exemplo para visualização do procedimento de chamada das animações para o GUI)
        gestorAnimado.criarAnimacao("skeleton-Sheet.png", "skeleton", 8, 1, 0.1f);
        animAll.put("skeleton", gestorAnimado.getAnimacao("skeleton"));



        int qtdInimigos = Distribuicoes.gerarPoisson(5.0);

        System.out.println("=== GERAÇÃO DE NÍVEL ===");
        System.out.println("Inimigos Gerados (Poisson): " + qtdInimigos);

        // VARIÁVEL ALEATÓRIA UNIFORME
        // Define ONDE cada inimigo aparece
        for (int i = 0; i < qtdInimigos; i++) {
            // Gera X entre 0 e o limite do mundo (com margem de 100px)

            float posX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
            // Gera Y entre 0 e o limite do mundo
            float posY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);

            posicoesInimigos.add(new Position(posX, posY));
            System.out.println("Inimigo " + i + " em: " + (int) posX + ", " + (int) posY);
        }

        // Geração do numero de itens no jogo a nivel de probabilidade e nº tentativas por V.A Discreta Binomial
        nItems = Distribuicoes.gerarBinomial(10, 0.5f);
        System.out.println(nItems);

        for (int i = 0; i < nItems; i++) {
            // Gera X entre 0 e o limite do mundo (com margem de 100px)

            float posX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
            // Gera Y entre 0 e o limite do mundo
            float posY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);

            posItems.add(new Position(posX, posY));
            System.out.println("Item " + i + " em: " + (int) posX + ", " + (int) posY);
        }

        // 3. CONFIGURAR CÂMARA
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Ou Gdx.graphics.getWidth()
        camera.zoom = 0.5f;


        // Câmara HUD com o tamanho real da janela (ex: 1920x1080)
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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
        TextureRegion frameEnemy = animAll.get("skeleton").getKeyFrame(stateTime, true);

        // --- CÂMARA ---
        //x = MathUtils.clamp(x, 0, LARGURA_MUNDO - frameEnemy.getRegionWidth());
        //y = MathUtils.clamp(y, 0, ALTURA_MUNDO - frameEnemy.getRegionHeight());


        // --- OBTER FRAMES ATUAIS ---
        TextureRegion framePlayer = animAll.get("player").getKeyFrame(stateTime, true);

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

        // 1.5. Desenhar Itens (Estáticos)
        if (gestorEstatico.getTexture("coin") != null && posItems != null) {
            for (Position pos : posItems) {
                batch.draw(gestorEstatico.getTexture("coin"), (int) pos.getX(), (int) pos.getY());
            }
        }

        // 2. Desenhar Inimigos
        if (frameEnemy != null) {
            for (Position pos : posicoesInimigos) {
                batch.draw(frameEnemy, (int) pos.getX(), (int) pos.getY());
            }
        }


        // 3. Desenhar Jogador
        if (framePlayer != null) {
            batch.draw(framePlayer, (int)x, (int)y);
        }



        batch.end();

        //Render para câmera do HUD
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        // EXEMPLO 1: Desenhar uma imagem de "healthbar" (Vida)
        // Assumindo que tens um ficheiro "healthbar.png" na pasta assets/Static
        if (gestorEstatico.getTexture("healthbar") != null) {
            // Desenha a healthbar fixa no canto superior esquerdo (X=20, Y=Altura-50)
            batch.draw(gestorEstatico.getTexture("healthbar"), 20, Gdx.graphics.getHeight() - 120);
        }

        if (gestorEstatico.getTexture("mc_icon") != null) {
            // Desenha a mc_icon fixa no canto superior esquerdo (X=20, Y=Altura-50)
            batch.draw(gestorEstatico.getTexture("mc_icon"), 40, 40);
        }

        if (gestorEstatico.getTexture("StaticHeart") != null) {
            // Desenha a mc_icon fixa no canto superior esquerdo (X=20, Y=Altura-50)
            batch.draw(gestorEstatico.getTexture("StaticHeart"), 270, Gdx.graphics.getHeight() - 110);
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