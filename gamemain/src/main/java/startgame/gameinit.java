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
import java.util.Random;


import java.util.HashMap;

public class gameinit extends ApplicationAdapter {

    SpriteBatch batch;
    OrthographicCamera camera;

    // --- GESTORES DE IMAGENS ---
    StaticImage gestorEstatico;
    AnimatedImage gestorAnimado;

    ArrayList <Enemy> enemies;

    // --- EFEITOS DE DANO / HUD ---
    private int lastPlayerHp;
    private float hpDamageTimer = 0f;
    private static final float HP_DAMAGE_FLASH_TIME = 0.25f;


    private float cameraShakeTimer = 0f;
    private static final float CAMERA_SHAKE_TIME = 0.15f;
    private static final float CAMERA_SHAKE_STRENGTH = 10f;

    // --- FLOATING DAMAGE TEXT ---
    private static final float DAMAGE_TEXT_DURATION = 0.6f;
    private ArrayList<DamageText> damageTexts;

    // --- HUD (INTERFACE) ---
    OrthographicCamera hudCamera;
    com.badlogic.gdx.graphics.g2d.BitmapFont font;
    /** Estatísticas para mostrar
     * int vidaJogar = 100;
     * int pontuacao = 0;
     * */

    // --- SALAS / ROOMS ---
    private RoomManager roomManager;
    private Room currentRoom;
    private ArrayList<Door> doors;
    private boolean doorsVisible = false;

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
    private final Random random = new Random();

    // ---------- CONSTANTES DE COMBATE / FÍSICAS ----------
    private static final float PLAYER_ATTACK_RANGE = 60f;      // alcance do ataque corpo-a-corpo
    private static final float ENEMY_CONTACT_RANGE = 40f;      // distância para levar dano de contacto
    private static final float PLAYER_HIT_COOLDOWN = 0.6f;     // segundos de invencibilidade depois de levar hit
    private float timeSinceLastPlayerHit = 0f;

    // raio mínimo entre slime e inimigos (para não ficarem em cima uns dos outros)
    private static final float MIN_DISTANCE_BETWEEN_CHARACTERS = 30f;

    //Sprite do mapa
    String mapaKey = "mapa1";

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

        damageTexts = new ArrayList<>();

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

        roomManager = new RoomManager();
        doors = new ArrayList<>();
        currentRoom = roomManager.getCurrentRoom();
        System.out.println("=== NOVA SALA ID " + currentRoom.getId() +
                " (" + currentRoom.getType() + ") ===");

        setupRoom(currentRoom);



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

        lastPlayerHp = Mc.getInstance().getHealth();

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        Mc.getInstance().setDelta(Gdx.graphics.getDeltaTime());
        stateTime += Mc.getInstance().getDelta();
        timeSinceLastPlayerHit += Mc.getInstance().getDelta();

        // --- MOVIMENTO ---
        Mc.getInstance().move();

        // NÃO deixar o slime colar nos inimigos (cumpre o que o Guilherme pediu)
        resolvePlayerEnemyCollision();

        int currentHp = Mc.getInstance().getHealth();
        if (currentHp < lastPlayerHp) {
            // levou dano
            hpDamageTimer = HP_DAMAGE_FLASH_TIME;
            cameraShakeTimer = CAMERA_SHAKE_TIME;
        }
        lastPlayerHp = currentHp;

        // Atualizar timers de efeitos
        if (hpDamageTimer > 0f) {
            hpDamageTimer -= Mc.getInstance().getDelta();
            if (hpDamageTimer < 0f) hpDamageTimer = 0f;
        }
        if (cameraShakeTimer > 0f) {
            cameraShakeTimer -= Mc.getInstance().getDelta();
            if (cameraShakeTimer < 0f) cameraShakeTimer = 0f;
        }
        // Atualizar textos de dano flutuantes
        updateDamageTexts(Mc.getInstance().getDelta());

        // DEBUG: limpar inimigos da sala ao carregar K
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            enemies.clear();
            System.out.println("[DEBUG] Todos os inimigos removidos desta sala.");
        }


        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
            System.exit(0);
        }

        // --- ATAQUE DO JOGADOR (SPACE) ---
        handlePlayerAttack();

        // --- DANO POR CONTACTO COM INIMIGOS ---
        handleEnemyContactDamage(Mc.getInstance().getDelta());


        // Se o slime morrer, termina a run
        if (Mc.getInstance().getHealth() <= 0) {
            System.out.println("O slime morreu! Fim da run.");
            Gdx.app.exit();
        }


        // --- OBTER FRAMES ATUAIS ---
        TextureRegion framePlayer = animAll.get("player").getKeyFrame(stateTime, true);

        // --- CÂMARA ---
        Mc.getInstance().getPosition().setX(MathUtils.clamp(Mc.getInstance().getPosition().getX(), 0, LARGURA_MUNDO - framePlayer.getRegionWidth()));
        Mc.getInstance().getPosition().setY(MathUtils.clamp(Mc.getInstance().getPosition().getY(), 0, ALTURA_MUNDO - framePlayer.getRegionHeight()));



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
        // Pequeno camera shake quando leva dano
        if (cameraShakeTimer > 0f) {
            float t = cameraShakeTimer / CAMERA_SHAKE_TIME;  // 1 → 0
            float shakeAmount = CAMERA_SHAKE_STRENGTH * t;
            float offsetX = (random.nextFloat() - 0.5f) * 2f * shakeAmount;
            float offsetY = (random.nextFloat() - 0.5f) * 2f * shakeAmount;
            camera.position.x += offsetX;
            camera.position.y += offsetY;
        }

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
        // Lógica de spawn de portas depende do tipo de sala
        if (!doorsVisible && currentRoom != null) {
            switch (currentRoom.getType()) {
                case COMBAT:
                case BOSS:
                    // Nas salas de combate/boss, portas só aparecem quando não há inimigos
                    if (enemies.isEmpty()) {
                        spawnDoorsForCurrentRoom();
                    }
                    break;

                case TREASURE:
                    // Na sala de tesouro, portas só aparecem quando já não há itens
                    if (itemObjects.isEmpty()) {
                        spawnDoorsForCurrentRoom();
                    }
                    break;
            }
        }
        // Verificar interação com portas (E)
        handleDoorInteraction();


        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        // 1. Desenhar Fundo (Estático)
        // Usa o nome do ficheiro sem extensão como chave
        if (gestorEstatico.getTexture("mapvoid") != null) {
            batch.draw(gestorEstatico.getTexture("mapvoid"), -1000, -1000);
        }

        // Escolher mapa consoante tipo de sala

//        if (currentRoom != null) {
//            switch (currentRoom.getType()) {
//                case TREASURE:
//                    mapaKey = "mapa2";
//                    break;
//                case BOSS:
//                    mapaKey = "mapa2";
//                    break;
//                case COMBAT:
//                default:
//                    mapaKey = "mapa1";
//                    break;
//            }
//        }
        if (gestorEstatico.getTexture(mapaKey) != null) {
            batch.draw(gestorEstatico.getTexture(mapaKey), 0, 0);
        }
        if (doorsVisible && gestorEstatico.getTexture("Basedoor") != null) {
            for (Door d : doors) {
                batch.draw(gestorEstatico.getTexture("Basedoor"),
                        (int) d.getPosition().getX(),
                        (int) d.getPosition().getY());
            }
        }


        // 1.5. Desenhar Itens (Estáticos)
        drawItems();

        // 2. Desenhar Inimigos
        drawEnemies();
        drawDamageTexts();



        // 3. Desenhar Jogador
        if (framePlayer != null) {
            batch.draw(framePlayer, (int)Mc.getInstance().getPosition().getX(), (int)Mc.getInstance().getPosition().getY());
        }



        batch.end();

        //Render para câmera do HUD
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        batch.end();
        drawHUD();
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

    public void drawHUD(){
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        if(doorsVisible==true)font.draw(batch, "To enter Door press E", ((int)(Gdx.graphics.getWidth()) / 2)-150, (int)(Gdx.graphics.getHeight()) / 2);
        if(currentRoom.getType()== RoomType.TREASURE && doorsVisible == false)font.draw(batch, "Collect all items to go to next room", ((int)(Gdx.graphics.getWidth()) / 2)-150, (int)(Gdx.graphics.getHeight()) / 2);
        //Desenha HUD (Heads-Up Display)
        if (gestorEstatico.getTexture("healthbar") != null) {
            // Desenha a healthbar fixa no canto superior esquerdo (X=0, Y=Altura-100)
            batch.draw(gestorEstatico.getTexture("healthbar"), 0, Gdx.graphics.getHeight() - 100);
            // --- Texto do HP com animação quando leva dano ---
            // baseScale mais pequeno para ficar parecido com o "Health" do sprite
            float baseScale = 1.2f;
            float extraScale = 0f;

            if (hpDamageTimer > 0f) {
                float t = hpDamageTimer / HP_DAMAGE_FLASH_TIME; // 1 → 0
                extraScale = 0.4f * t;                         // popzinho no texto mas não exagerado
                font.setColor(1f, 0.3f, 0.3f, 1f);             // vermelho quando leva dano
            } else {
                font.setColor(1f, 1f, 1f, 1f);                 // branco normal
            }

            // guardar escala antiga
            float oldScaleX = font.getData().scaleX;
            float oldScaleY = font.getData().scaleY;

            font.getData().setScale(baseScale + extraScale);

            // posição afinada para ficar alinhado com o texto "Health" da barra
            font.draw(batch,
                    "HP: " + Mc.getInstance().getHealth(),
                    140,                                   // um bocado à direita de "Health"
                    Gdx.graphics.getHeight() - 60);        // mais centrado verticalmente na barra

            // reset da escala e cor para o resto do HUD
            font.getData().setScale(oldScaleX, oldScaleY);
            font.setColor(1f, 1f, 1f, 1f);

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
        if (gestorEstatico.getTexture("manastatic") != null) {
            // Desenha a mc_icon fixa no canto superior esquerdo (X=20, Y=Altura-50)
            batch.draw(gestorEstatico.getTexture("manastatic"), 250, Gdx.graphics.getHeight() - 190);
        }

        if (gestorEstatico.getTexture("coin_HUD") != null) {
            font.draw(batch, "COINS: " + Mc.getInstance().getBalanceCoins(), Gdx.graphics.getWidth()- 200, Gdx.graphics.getHeight() - 25);
            batch.draw(gestorEstatico.getTexture("coin_HUD"), Gdx.graphics.getWidth()- 80, Gdx.graphics.getHeight() - 80);
        }

        font.draw(batch, "DAMAGE: " + Mc.getInstance().getatkD(), Gdx.graphics.getWidth()- 198, Gdx.graphics.getHeight() - 85);

        batch.end();
    }

    // --- Geração de inimigos e itens para uma sala ---
    private void setupRoom(Room room) {
        enemies.clear();
        itemObjects.clear();
        posItems.clear();
        doors.clear();
        doorsVisible = false;

        int qtdInimigos = room.getEnemyCount();
        int qtdItems = room.getItemCount();

        System.out.println("=== GERAÇÃO DE NÍVEL ===");
        System.out.println("Sala ID " + room.getId() + " (" + room.getType() + ")");
        System.out.println("Inimigos Gerados: " + qtdInimigos);
        System.out.println("Itens Gerados: " + qtdItems);

        // Inimigos
        for (int i = 0; i < qtdInimigos; i++) {
            float posX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
            float posY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);

            int tipo = random.nextInt(3); // 0 zombie, 1 skeleton, 2 darkmage

            Enemy enemy;
            switch (tipo) {
                case 0:
                    enemy = new Zombie(new Position(posX, posY));
                    System.out.println("Zombie " + i + " em: " + (int) posX + ", " + (int) posY);
                    break;
                case 1:
                    enemy = new Skeleton(new Position(posX, posY));
                    System.out.println("Skeleton " + i + " em: " + (int) posX + ", " + (int) posY);
                    break;
                case 2:
                default:
                    enemy = new DarkMage(new Position(posX, posY));
                    System.out.println("Dark Mage " + i + " em: " + (int) posX + ", " + (int) posY);
                    break;
            }

            enemies.add(enemy);
        }

        // Itens: mistura de moedas e espadas (isto respeita a V.A. Binomial do nº de itens)
        nCoins = 0;
        nSwords = 0;
        for (int i = 0; i < qtdItems; i++) {
            float posX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
            float posY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);

            boolean isSword = random.nextFloat() < 0.3f; // ~30% espadas, resto moedas
            staticAssets item;
            if (isSword) {
                item = new StaticSword(new Position(posX, posY));
                nSwords++;
                System.out.println("Espada " + i + " em: " + (int) posX + ", " + (int) posY);
            } else {
                item = new Coin(new Position(posX, posY));
                nCoins++;
                System.out.println("Moeda " + i + " em: " + (int) posX + ", " + (int) posY);
            }

            itemObjects.add(item);
            posItems.add(item.getPosition());
        }
    }

    private void spawnDoorsForCurrentRoom() {
        doors.clear();
        doorsVisible = true;

        int numDoors = currentRoom != null ? currentRoom.getNumberOfDoors() : 2;

        float baseX = 700f;
        float spacing = 250f;
        float y = 350f;

        for (int i = 0; i < numDoors; i++) {
            Position doorPos = new Position(baseX + i * spacing, y);
            doors.add(new Door(doorPos, i));
        }
    }

    // Verifica se o jogador está perto de alguma porta e carrega E
    private void handleDoorInteraction() {
        if (!doorsVisible) return;
        if (!Gdx.input.isKeyJustPressed(Input.Keys.E)) return;

        Position mcPos = Mc.getInstance().getPosition();

        for (Door d : doors) {
            if (mcPos.isWithinRange(d.getPosition().getX(), d.getPosition().getY(), 40f)) {
                Room nextRoom = roomManager.goToNextRoom(d.getOptionIndex());
                System.out.println("=== TRANSIÇÃO PARA SALA ID " + nextRoom.getId() +
                        " (" + nextRoom.getType() + ") ===");
                currentRoom = nextRoom;
                int skinroom = (int) Distribuicoes.gerarUniforme(0,2);
                switch(skinroom){
                    case 0:
                        mapaKey = "mapa1";
                        break;
                    case 1:
                        mapaKey = "mapa2";
                        break;
                    default:
                        mapaKey = "mapa1";
                        break;
                }
                setupRoom(currentRoom);
                break;
            }
        }
    }

    // Ataque do jogador (melee) com SPACE
    private void handlePlayerAttack() {
        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) return;

        Position mcPos = Mc.getInstance().getPosition();
        ArrayList<Enemy> mortos = new ArrayList<>();

        for (Enemy e : enemies) {
            if (mcPos.isWithinRange(e.getPosition().getX(), e.getPosition().getY(), PLAYER_ATTACK_RANGE)) {
                int dano = Mc.getInstance().getatkD();
                e.takeDamage(dano);
                System.out.println("Acertaste num inimigo! Vida inimigo: " + e.getHealth());

                // Criar texto de dano flutuante
                DamageText dt = new DamageText(
                        e.getPosition().getX(),
                        e.getPosition().getY() + 40,
                        String.valueOf(dano),
                        DAMAGE_TEXT_DURATION
                );
                damageTexts.add(dt);

                if (e.isDead()) {
                    mortos.add(e);
                }
            }
        }

        if (!mortos.isEmpty()) {
            enemies.removeAll(mortos);
            System.out.println(mortos.size() + " inimigo(s) morto(s).");
        }
    }

    // Dano por contacto com inimigos
    private void handleEnemyContactDamage(float delta) {
        Position mcPos = Mc.getInstance().getPosition();

        for (Enemy e : enemies) {
            if (mcPos.isWithinRange(e.getPosition().getX(), e.getPosition().getY(), ENEMY_CONTACT_RANGE)) {
                if (timeSinceLastPlayerHit >= PLAYER_HIT_COOLDOWN) {
                    Mc.getInstance().takeDamage(e.getatkD());
                    timeSinceLastPlayerHit = 0f;
                    System.out.println("O slime levou dano! Vida atual: " + Mc.getInstance().getHealth());
                }
                // Só levas 1 hit por cooldown, mesmo que estejas em cima de vários
                break;
            }
        }
    }

    // NÃO deixar o slime ficar em cima dos inimigos
    private void resolvePlayerEnemyCollision() {
        Position mcPos = Mc.getInstance().getPosition();

        for (Enemy e : enemies) {
            float dx = mcPos.getX() - e.getPosition().getX();
            float dy = mcPos.getY() - e.getPosition().getY();
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            if (dist > 0f && dist < MIN_DISTANCE_BETWEEN_CHARACTERS) {
                float overlap = MIN_DISTANCE_BETWEEN_CHARACTERS - dist;

                // Direção normalizada
                float nx = dx / dist;
                float ny = dy / dist;

                // Empurrar o slime para fora do inimigo
                mcPos.setX(mcPos.getX() + nx * overlap);
                mcPos.setY(mcPos.getY() + ny * overlap);
            }
        }
    }

    // Atualizar textos de dano (subir e desaparecer)
    private void updateDamageTexts(float delta) {
        if (damageTexts.isEmpty()) return;

        ArrayList<DamageText> toRemove = new ArrayList<>();
        for (DamageText dt : damageTexts){
            dt.timeLeft -= delta;
            dt.y += 40f * delta; // sobe um bocadinho

            if (dt.timeLeft <= 0f) {
                toRemove.add(dt);
            }
        }
        damageTexts.removeAll(toRemove);
    }

    private void drawDamageTexts() {
        if (damageTexts.isEmpty()) return;

        float oldScale = font.getData().scaleX;
        font.getData().setScale(1.5f);

        for (DamageText dt : damageTexts) {
            float alpha = MathUtils.clamp(dt.timeLeft / DAMAGE_TEXT_DURATION, 0f, 1f);
            font.setColor(1f, 0.8f, 0.2f, alpha);
            font.draw(batch, dt.text, dt.x, dt.y);
        }

        font.getData().setScale(oldScale);
        font.setColor(1f, 1f, 1f, 1f);
    }



    @Override
    public void dispose() {
        batch.dispose();
        gestorEstatico.dispose();
        gestorAnimado.dispose();
    }

    private static class DamageText {
        float x, y;
        String text;
        float timeLeft;

        DamageText(float x, float y, String text, float timeLeft) {
            this.x = x;
            this.y = y;
            this.text = text;
            this.timeLeft = timeLeft;
        }
    }
}