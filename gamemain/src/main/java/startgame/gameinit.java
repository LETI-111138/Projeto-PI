package startgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import startgame.Objects.*;
import startgame.RNG.Distribuicoes;
import startgame.RNG.RandomConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class gameinit extends ApplicationAdapter {

    SpriteBatch batch;
    OrthographicCamera camera;

    // --- GESTORES DE IMAGENS ---
    StaticImage gestorEstatico;
    AnimatedImage gestorAnimado;

    ArrayList<Skeleton> sklts;

    // --- HUD (INTERFACE) ---
    OrthographicCamera hudCamera;
    // Estatísticas para mostrar
    int vidaJogar = 100;
    int pontuacao = 0;

    // --- SALAS ---
    private RoomManager roomManager;
    private Room currentRoom;
    private ArrayList<Door> doors;
    private boolean doorsVisible = false;

    // Variáveis para o Jogador (MC)
    HashMap<String, Animation<TextureRegion>> animAll;
    public static float stateTime = 0f;

    // Atributos em relação ao número de Itens no jogo e a sua probabilidade de aparecerem na sala
    int nItems = 0;
    public static ArrayList<Position> posItems;
    public static ArrayList<Coin> itemObjects;

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
        hudCamera = new OrthographicCamera();
        sklts = new ArrayList<>();
        itemObjects = new ArrayList<>();

        gestorAnimado.criarAnimacao("mc_pj_pi.png", "player", 6, 1, 0.1f);
        animAll.put("player", gestorAnimado.getAnimacao("player"));

        // Inimigo esqueleto (exemplo para visualização do procedimento de chamada das animações para o GUI)
        gestorAnimado.criarAnimacao("skeleton-Sheet.png", "skeleton", 8, 1, 0.1f);
        animAll.put("skeleton", gestorAnimado.getAnimacao("skeleton"));

        // --- GERIR SALAS ---
        roomManager = new RoomManager();
        currentRoom = roomManager.getCurrentRoom();
        doors = new ArrayList<>();
        doorsVisible = false;

        // gera inimigos e itens da sala inicial
        setupRoom(currentRoom);

        // 3. CONFIGURAR CÂMARA
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Ou Gdx.graphics.getWidth()
        camera.zoom = 0.5f;

        // Câmara HUD com o tamanho real da janela (ex: 1920x1080)
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Gera inimigos e itens para a sala atual, com base nas variáveis aleatórias.
     */
    private void setupRoom(Room room) {
        System.out.println("=== NOVA SALA ID " + room.getId() + " (" + room.getType() + ") ===");

        // limpar dados da sala anterior
        sklts.clear();
        itemObjects.clear();
        posItems.clear();
        doors.clear();
        doorsVisible = false;

        // --- INIMIGOS ---
        int qtdInimigos = room.getEnemyCount();
        System.out.println("Inimigos Gerados: " + qtdInimigos);

        for (int i = 0; i < qtdInimigos; i++) {
            float posX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
            float posY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);

            Skeleton esqueleto = new Skeleton(new Position(posX, posY));
            sklts.add(esqueleto);
            System.out.println("Inimigo " + i + " em: " + (int) posX + ", " + (int) posY);
        }

        // --- ITENS ---
        nItems = room.getItemCount();
        System.out.println("Itens Gerados: " + nItems);

        for (int i = 0; i < nItems; i++) {
            float posX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
            float posY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);

            itemObjects.add(new Coin(new Position(posX, posY)));
            posItems.add(new Position(posX, posY));
            System.out.println("Item " + i + " em: " + (int) posX + ", " + (int) posY);
        }

        // opcional: recentrar o player
        Mc.getInstance().getPosition().setX(1000);
        Mc.getInstance().getPosition().setY(1000);
    }

    /**
     * Cria as portas da sala atual, de acordo com o número de opções.
     */
    private void spawnDoorsForCurrentRoom() {
        doors.clear();

        if (currentRoom == null) return;

        int numDoors = currentRoom.getNumberOfDoors();

        // posições simples para teste – podes mudar depois
        float[][] doorPositions = new float[][]{
                {800, 400},
                {1000, 400},
                {1200, 400}
        };

        for (int i = 0; i < numDoors && i < doorPositions.length; i++) {
            Door d = new Door(new Position(doorPositions[i][0], doorPositions[i][1]), i);
            doors.add(d);
        }

        doorsVisible = true;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Mc.getInstance().setDelta(Gdx.graphics.getDeltaTime());
        stateTime += Mc.getInstance().getDelta();

        // --- MOVIMENTO ---
        Mc.getInstance().move();

        // DEBUG: limpar inimigos da sala ao carregar K
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            sklts.clear();
            System.out.println("[DEBUG] Todos os inimigos removidos desta sala.");
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
            System.exit(0);
        }

        // --- OBTER FRAMES ATUAIS ---
        TextureRegion frameEnemy = animAll.get("skeleton").getKeyFrame(stateTime, true);

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
        camera.position.set(Mc.getInstance().getPosition().getX() + framePlayer.getRegionWidth() / 2f, Mc.getInstance().getPosition().getY() + framePlayer.getRegionHeight() / 2f, 0);
        camera.update();

        // --- COLISÃO COM ITENS ---
        if (!itemObjects.isEmpty()) {
            ArrayList<Coin> aux = new ArrayList<>();
            for (Coin c : itemObjects) {
                if (Mc.getInstance().getPosition().isWithinRange(c.getPosition().getX(), c.getPosition().getY(), 20)) {
                    nItems--;
                    c.consume(Mc.getInstance());
                    aux.add(c);
                }
            }
            itemObjects.removeAll(aux);
        }

        // Quando não há inimigos, mostrar portas (se ainda não estão visíveis)
        if (sklts.isEmpty() && !doorsVisible) {
            spawnDoorsForCurrentRoom();
        }

        // Interação com portas – sem modificar a lista dentro do for
        Door doorToUse = null;
        for (Door d : doors) {
            if (Mc.getInstance().getPosition().isWithinRange(
                    d.getPosition().getX(),
                    d.getPosition().getY(),
                    40
            )) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                    doorToUse = d;
                    break;
                }
            }
        }

        if (doorToUse != null) {
            currentRoom = roomManager.goToNextRoom(doorToUse.getOptionIndex());
            setupRoom(currentRoom);
            doorsVisible = false;
            doors.clear();
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // 1. Desenhar Fundo (Estático)
        // Usa o nome do ficheiro sem extensão como chave
        Texture mapa = null;

        if (currentRoom != null) {
            switch (currentRoom.getType()) {
                case COMBAT:
                    // sala normal de combate → usa mapa1
                    mapa = gestorEstatico.getTexture("mapa1");
                    break;

                case TREASURE:
                    // sala de tesouro → usa o mapvoid só para testar mudança de sala
                    mapa = gestorEstatico.getTexture("mapvoid");
                    break;

                case BOSS:
                    // por agora usa o mesmo fundo que o tesouro (podes mudar depois)
                    mapa = gestorEstatico.getTexture("mapvoid");
                    break;
            }
        }

        if (mapa == null) {
            mapa = gestorEstatico.getTexture("mapa1");
        }

        if (mapa != null) {
            batch.draw(mapa, 0, 0);
        }

        // desenhar portas (se existirem)
        if (!doors.isEmpty()) {
            Texture doorTexture = gestorEstatico.getTexture("Basedoor");
            if (doorTexture != null) {
                for (Door d : doors) {
                    batch.draw(doorTexture, d.getPosition().getX(), d.getPosition().getY());
                }
            }
        }

        // 1.5. Desenhar Itens (Estáticos)
        if (gestorEstatico.getTexture("coin") != null && posItems != null) {
            for (Position pos : posItems) {
                batch.draw(gestorEstatico.getTexture("coin"), (int) pos.getX(), (int) pos.getY());
            }
        }

        // 2. Desenhar Inimigos
        if (frameEnemy != null) {
            for (Skeleton s : sklts) {
                s.giveF(frameEnemy);
                s.move();
                batch.draw(frameEnemy, (int) s.getPosition().getX(), (int) s.getPosition().getY());
            }
        }

        // 3. Desenhar Jogador
        if (framePlayer != null) {
            batch.draw(framePlayer, (int) Mc.getInstance().getPosition().getX(), (int) Mc.getInstance().getPosition().getY());
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

    public static boolean withinbounds(Position position) {
        if (position.getX() <= 2000 && position.getX() >= 0 && position.getY() <= 2000 && position.getY() >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static float getStateTime() {
        return stateTime;
    }

    public static int getLarguraMundo() {
        return LARGURA_MUNDO;
    }

    public static int getAlturaMundo() {
        return ALTURA_MUNDO;
    }

    public static ArrayList<Position> getposItems() {
        return posItems;
    }

    public static void rmItem(Position position) {
        posItems.remove(position);
    }

    @Override
    public void dispose() {
        batch.dispose();
        gestorEstatico.dispose();
        gestorAnimado.dispose();
    }
}
