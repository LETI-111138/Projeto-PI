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

    ArrayList <Enemy> enemies;

    // --- HUD (INTERFACE) ---
    OrthographicCamera hudCamera;
    com.badlogic.gdx.graphics.g2d.BitmapFont font;
    /** Estatísticas para mostrar
     * int vidaJogar = 100;
     * int pontuacao = 0;
     * */


    // Variáveis para o Jogador (MC)
    HashMap<String,Animation<TextureRegion>> animAll;
    public static float stateTime = 0f;

    // Atributos em relação ao número de Itens no jogo e a sua probabilidade de aparecerem na sala
    int nCoins = 0;
    int nSwords = 0;
    public static ArrayList<Position> posItems;
    public static ArrayList<staticAssets> itemObjects;

    // Índice para controlar a direção (0=Baixo, 1=Esq, 2=Dir, 3=Cima - Ajusta à tua imagem!)
    ArrayList<Position> posicoesInimigos;


    final static int LARGURA_MUNDO = 2000;
    final static int ALTURA_MUNDO = 2000;


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

        // Inicialização de variáveis relativas ao HUD
        hudCamera = new OrthographicCamera();
        font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        font.getData().setScale(2); // Aumentar o tamanho do texto (opcional)
        font.setColor(1, 1, 1, 1);  // Cor Branca

        // Inimigos esqueletos guardados aqui
        enemies = new ArrayList<>();

        itemObjects = new ArrayList<>();

        gestorAnimado.criarAnimacao("mc_pj_pi.png", "player", 6, 1, 0.1f);
        animAll.put("player", gestorAnimado.getAnimacao("player"));

        // Inimigo esqueleto
        gestorAnimado.criarAnimacao("skeleton-Sheet.png", "skeleton", 8, 1, 0.1f);
        animAll.put("skeleton", gestorAnimado.getAnimacao("skeleton"));

        // Inimigo zombie
        gestorAnimado.criarAnimacao("zombie.png", "zombie", 12, 1, 0.1f);
        animAll.put("zombie", gestorAnimado.getAnimacao("zombie"));

        // Inimigo dark mage
        gestorAnimado.criarAnimacao("darkmage.png", "darkmage", 12, 1, 0.1f);
        animAll.put("darkmage", gestorAnimado.getAnimacao("darkmage"));



        int qtdInimigos = Distribuicoes.gerarPoisson(5.0);

        System.out.println("=== GERAÇÃO DE NÍVEL ===");
        System.out.println("Inimigos Gerados (Poisson): " + qtdInimigos);

        // VARIÁVEL ALEATÓRIA UNIFORME
        // Define ONDE cada inimigo aparece
        for (int i = 0; i < qtdInimigos; i++) {
            // Gera X entre 0 e o limite do mundo (com margem de 100px)
            int n =(int) Distribuicoes.gerarUniforme(0,3);


            switch(n) {
                case 0:
                float posX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
                // Gera Y entre 0 e o limite do mundo
                float posY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);

                Zombie zombie = new Zombie(new Position(posX, posY));

                enemies.add(zombie);
                System.out.println("Zombie " + i + " em: " + (int) posX + ", " + (int) posY); break;
                case 1:
                    float posX2 = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
                    // Gera Y entre 0 e o limite do mundo
                    float posY2 = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);

                    Skeleton skeleton = new Skeleton(new Position(posX2, posY2));

                    enemies.add(skeleton);
                    System.out.println("Skeleton " + i + " em: " + (int) posX2 + ", " + (int) posY2); break;

                case 2:
                    float posX3 = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
                    // Gera Y entre 0 e o limite do mundo
                    float posY3 = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);

                    DarkMage darkmage = new DarkMage(new Position(posX3, posY3));

                    enemies.add(darkmage);
                    System.out.println("Dark Mage " + i + " em: " + (int) posX3 + ", " + (int) posY3); break;

                default:
                    float posXdefault = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
                    // Gera Y entre 0 e o limite do mundo
                    float posYdefault = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);

                    DarkMage defaulte = new DarkMage(new Position(posXdefault, posYdefault));

                    enemies.add(defaulte);
                    System.out.println("Dark Mage " + i + " em: " + (int) posXdefault + ", " + (int) posYdefault); break;

            }
        }

        // Geração do numero de itens no jogo a nivel de probabilidade e nº tentativas por V.A Discreta Binomial
        nCoins = Distribuicoes.gerarBinomial(10, 0.5f);
        System.out.println(nCoins + ": Moedas");

        for (int i = 0; i < nCoins; i++) {
            // Gera X entre 0 e o limite do mundo (com margem de 100px)

            float posX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
            // Gera Y entre 0 e o limite do mundo
            float posY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);

            itemObjects.add(new Coin(new Position(posX, posY)));
            posItems.add(new Position(posX, posY));
            System.out.println("Moeda " + i + " em: " + (int) posX + ", " + (int) posY);
        }

        // Geração do numero de itens no jogo a nivel de probabilidade e nº tentativas por V.A Discreta Binomial
        nSwords = Distribuicoes.gerarBinomial(5, 0.5f);
        System.out.println(nSwords + ": Espadas");

        for (int i = 0; i < nSwords; i++) {
            // Gera X entre 0 e o limite do mundo (com margem de 100px)

            float posX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
            // Gera Y entre 0 e o limite do mundo
            float posY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);

            itemObjects.add(new StaticSword(new Position(posX, posY)));
            posItems.add(new Position(posX, posY));
            System.out.println("Espada" + i + " em: " + (int) posX + ", " + (int) posY);
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


        Mc.getInstance().setDelta(Gdx.graphics.getDeltaTime());
        stateTime += Mc.getInstance().getDelta();

        // --- MOVIMENTO ---
        Mc.getInstance().move();


        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
            System.exit(0);
        }

        // --- OBTER FRAMES ATUAIS ---


        // --- CÂMARA ---
        //x = MathUtils.clamp(x, 0, LARGURA_MUNDO - frameEnemy.getRegionWidth());
        //y = MathUtils.clamp(y, 0, ALTURA_MUNDO - frameEnemy.getRegionHeight());


        // --- OBTER FRAMES ATUAIS ---
        TextureRegion framePlayer = animAll.get("player").getKeyFrame(stateTime, true);

        // --- CÂMARA ---
        Mc.getInstance().getPosition().setX(MathUtils.clamp(Mc.getInstance().getPosition().getX(), 0, LARGURA_MUNDO - framePlayer.getRegionWidth()));
        Mc.getInstance().getPosition().setY(MathUtils.clamp(Mc.getInstance().getPosition().getY(), 0, ALTURA_MUNDO - framePlayer.getRegionHeight()));



        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            camera.zoom -= 1.0f * Mc.getInstance().getDelta(); // Aproximar camera do personagem
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X) && camera.zoom <= 0.50f) {
            camera.zoom += 1.0f * Mc.getInstance().getDelta(); // Afastar camara do personagem
        }

        camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, 5.0f);
        camera.position.set(Mc.getInstance().getPosition().getX() + framePlayer.getRegionWidth()/2f, Mc.getInstance().getPosition().getY()  + framePlayer.getRegionHeight()/2f, 0);
        camera.update();


        if(!itemObjects.isEmpty()) {
            ArrayList<staticAssets> aux = new ArrayList<>();
            for (staticAssets c : itemObjects) {
              if(Mc.getInstance().getPosition().isWithinRange(c.getPosition().getX(), c.getPosition().getY(), 20)){
                  switch(c.getKey()){
                      case "coin":
                        nCoins--;
                        c.consume(Mc.getInstance());
                        aux.add(c);
                      break;
                      case "staticsword":
                          nSwords--;
                          c.consume(Mc.getInstance());
                          aux.add(c);
                      break;
                      default:
                          c.consume(Mc.getInstance());
                          aux.add(c);
                  }
              }
            }
            itemObjects.removeAll(aux);
        }
        System.out.println("Dano é : " + Mc.getInstance().getatkD());

        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        // 1. Desenhar Fundo (Estático)
        // Usa o nome do ficheiro sem extensão como chave
        if (gestorEstatico.getTexture("mapvoid") != null) {
            batch.draw(gestorEstatico.getTexture("mapvoid"), -1000, -1000);
        }
        if (gestorEstatico.getTexture("mapa2") != null) {
            batch.draw(gestorEstatico.getTexture("mapa2"), 0, 0);
        }

        if (gestorEstatico.getTexture("Basedoor") != null) {
            batch.draw(gestorEstatico.getTexture("Basedoor"), 350, 350);
        }

        // 1.5. Desenhar Itens (Estáticos)
        drawItems();

        // 2. Desenhar Inimigos
        drawEnemies();



        // 3. Desenhar Jogador
        if (framePlayer != null) {
            batch.draw(framePlayer, (int)Mc.getInstance().getPosition().getX(), (int)Mc.getInstance().getPosition().getY());
        }



        batch.end();

        //Render para câmera do HUD
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        //Desenha HUD (Heads-Up Display)
        if (gestorEstatico.getTexture("healthbar") != null) {
            // Desenha a healthbar fixa no canto superior esquerdo (X=0, Y=Altura-100)
            batch.draw(gestorEstatico.getTexture("healthbar"), 0, Gdx.graphics.getHeight() - 100);
        }

        if (gestorEstatico.getTexture("manabar") != null) {
            // Desenha a healthbar fixa no canto superior esquerdo (X=0, Y=Altura-100)
            batch.draw(gestorEstatico.getTexture("manabar"), 0, Gdx.graphics.getHeight() - 200);
        }

        if (gestorEstatico.getTexture("mc_icon") != null) {
            // Desenha a mc_icon fixa no canto superior esquerdo (X=20, Y=Altura-50)
            batch.draw(gestorEstatico.getTexture("mc_icon"), 20, 20);
        }

        if (gestorEstatico.getTexture("StaticHeart") != null) {
            // Desenha a mc_icon fixa no canto superior esquerdo (X=20, Y=Altura-50)
            batch.draw(gestorEstatico.getTexture("StaticHeart"), 250, Gdx.graphics.getHeight() - 90);
        }

        if (gestorEstatico.getTexture("coin_HUD") != null) {
            font.draw(batch, "COINS: " + Mc.getInstance().getBalanceCoins(), Gdx.graphics.getWidth()- 200, Gdx.graphics.getHeight() - 25);
            batch.draw(gestorEstatico.getTexture("coin_HUD"), Gdx.graphics.getWidth()- 80, Gdx.graphics.getHeight() - 80);
        }

        batch.end();
    }

    public static boolean withinbounds(Position position){
    if(position.getX()<= 2000 && position.getX()>= 0 && position.getY()<= 2000 && position.getY()>= 0){
        return true;
    }else{
        return false;
    }
    }

    public static float getStateTime(){
        return stateTime;
    }

    public static int getLarguraMundo(){
        return LARGURA_MUNDO;
    }

    public static int getAlturaMundo(){
        return ALTURA_MUNDO;
    }

    public static ArrayList<Position> getposItems() {
        return posItems;
    }

    public static void rmItem(Position position){
        posItems.remove(position);
    }

    //Desenha Inimigos
    public void drawEnemies(){
        for (Enemy e : enemies) {
            if(e instanceof Zombie) {
                TextureRegion framez = animAll.get("zombie").getKeyFrame(stateTime, true);
                e.giveF(framez);
                e.move();
                batch.draw(framez, (int) e.getPosition().getX(), (int) e.getPosition().getY());
            }else if (e instanceof Skeleton) {
                TextureRegion frames = animAll.get("skeleton").getKeyFrame(stateTime, true);
                e.giveF(frames);
                e.move();
                batch.draw(frames, (int) e.getPosition().getX(), (int) e.getPosition().getY());
            }else if (e instanceof DarkMage) {
                TextureRegion framedm = animAll.get("darkmage").getKeyFrame(stateTime, true);
                e.giveF(framedm);
                e.move();
                batch.draw(framedm, (int) e.getPosition().getX(), (int) e.getPosition().getY());
            }


        }
    }

    public void drawItems(){
        if (gestorEstatico.getTexture("coin") != null && posItems != null) {
            for (staticAssets c : itemObjects) {
                switch (c.getKey()) {
                    case "coin":
                        batch.draw(gestorEstatico.getTexture("coin"), (int) c.getPosition().getX(), (int) c.getPosition().getY());
                        break;
                    case "staticsword":
                        batch.draw(gestorEstatico.getTexture("staticsword"), (int) c.getPosition().getX(), (int) c.getPosition().getY());
                        break;
                }
            }

        }
    }


    @Override
    public void dispose() {
        batch.dispose();
        gestorEstatico.dispose();
        gestorAnimado.dispose();
    }
}