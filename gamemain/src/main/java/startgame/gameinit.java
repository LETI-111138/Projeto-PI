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
import startgame.RNG.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList; // Import necessário
import java.util.Random;
import com.badlogic.gdx.math.Rectangle; // Importante para detectar clicks
import com.badlogic.gdx.math.Vector3;


import java.util.HashMap;

public class gameinit extends ApplicationAdapter {

    SpriteBatch batch;
    OrthographicCamera camera;

    // --- GESTORES DE IMAGENS ---
    private StaticImage gestorEstatico;
    private AnimatedImage gestorAnimado;

    private ArrayList <Enemy> enemies;
    private BossIndex boss = null;
    private Enemy currentBoss = null;
    // --- EFEITOS DE DANO / HUD ---
    private int lastPlayerHp;
    private float hpDamageTimer = 0f;
    private static final float HP_DAMAGE_FLASH_TIME = 0.25f;

    // --- LOJA ---
    private boolean isShopOpen = false;
    private StoreObject merchant;
    private ArrayList<ShopItem> itemsOnSale;

    // Retângulos para detetar cliques (Botão fechar e Itens)
    private Rectangle closeButtonBounds;
    private ArrayList<Rectangle> itemBounds;

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
    private int defeatedBosses = 0;

    // Variáveis para o Jogador (MC)
    HashMap<String,Animation<TextureRegion>> animAll;
    public static float stateTime = 0f;

    // Atributos em relação ao número de Itens no jogo e a sua probabilidade de aparecerem na sala
    int nCoins = 0;
    int nSwords = 0;
    int nChicken = 0;
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

        gestorAnimado.criarAnimacao("clownboss-Sheet.png", "bossclown", 8, 1, 0.1f);
        animAll.put("bossclown", gestorAnimado.getAnimacao("bossclown"));

        gestorAnimado.criarAnimacao("bluedroplet.png", "bluedroplet", 8, 1, 0.1f);
        animAll.put("bluedroplet", gestorAnimado.getAnimacao("bluedroplet"));

        gestorAnimado.criarAnimacao("attackanimationt.png", "attackanimationt", 12, 1, 0.035f);
        animAll.put("attackanimationt", gestorAnimado.getAnimacao("attackanimationt"));

        gestorAnimado.criarAnimacao("ForestTotem.png", "ForestTotem", 8, 1, 0.1f);
        animAll.put("ForestTotem", gestorAnimado.getAnimacao("ForestTotem"));

        roomManager = new RoomManager();
        doors = new ArrayList<>();
        currentRoom = roomManager.getCurrentRoom();
        System.out.println("=== NOVA SALA ID " + currentRoom.getId() +
                " (" + currentRoom.getType() + ") ===");

        setupRoom(currentRoom);

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

        // NÃO deixar o slime atravessar o merchant
        resolvePlayerMerchantCollision();

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

        // --- ATAQUE DO JOGADOR (Botão esquerdo do mouse) ---
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


        if (!itemObjects.isEmpty()) {
            ArrayList<staticAssets> aux = new ArrayList<>();
            int price = 0; // Preço padrão

            // Se for LOJA, define um preço (ex: 5 moedas)
            if (currentRoom.getType() == RoomType.STORE) {
                price = 5;
            }

            for (staticAssets c : itemObjects) {
                if (Mc.getInstance().getPosition().isWithinRange(c.getPosition().getX(), c.getPosition().getY(), 40)) {

                    // Lógica de Compra / Pick up
                    boolean canPickUp = true;

                    if (currentRoom.getType() == RoomType.STORE) {
                        if (Mc.getInstance().getBalanceCoins() >= price) {
                            Mc.getInstance().removeBalanceCoins(price); // Paga o item
                            System.out.println("Item comprado por " + price + " moedas!");
                        } else {
                            canPickUp = false; // Não tem dinheiro
                            // Opcional: Mostrar texto "Sem dinheiro"
                        }
                    }

                    if (canPickUp) {
                        switch (c.getKey()) {
                            case "coin":
                                // Moedas no chão não se pagam para apanhar, né?
                                // Se venderes moedas na loja, cuidado com o loop infinito de dinheiro!
                                nCoins--;
                                c.consume(Mc.getInstance());
                                aux.add(c);
                                break;
                            case "staticsword":
                                nSwords--;
                                c.consume(Mc.getInstance());
                                aux.add(c);
                                break;
                            case "chicken":
                                nChicken--;
                                c.consume(Mc.getInstance());
                                aux.add(c);
                                break;
                            default:
                                c.consume(Mc.getInstance());
                                aux.add(c);
                        }
                    }
                }
            }
            itemObjects.removeAll(aux);
        }
        // Lógica de spawn de portas depende do tipo de sala
        if (!doorsVisible && currentRoom != null) {
            switch (currentRoom.getType()) {
                case COMBAT:
                    // Nas salas de combate/boss, portas só aparecem quando não há inimigos
                    if (enemies.isEmpty()) {
                        spawnDoorsForCurrentRoom();
                    }
                        break;
                case BOSS:
                    // Nas salas de combate/boss, portas só aparecem quando não há inimigos
                    if (currentBoss==null){
                        spawnDoorsForCurrentRoom();
                    }
                    break;

                case TREASURE:
                    // Na sala de tesouro, portas só aparecem quando já não há itens
                    if (itemObjects.isEmpty()) {
                        spawnDoorsForCurrentRoom();
                    }
                    break;
                case STORE:
                    spawnDoorsForCurrentRoom();
                    break;
            }
        }
        // Interação com portas (Pressionar tecla E ativa a iteração do jogador com portas)
        handleDoorInteraction();

        updateShopLogic();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        // 1. Desenhar Fundo (Estático)
        // Usa o nome do ficheiro sem extensão como chave
        if (gestorEstatico.getTexture("mapvoid") != null) {
            batch.draw(gestorEstatico.getTexture("mapvoid"), -1000, -1000);
        }

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
        if(doorsVisible && gestorEstatico.getTexture("door") != null && currentRoom.getType() == RoomType.STORE) {
            for (Door d : doors) {
                batch.draw(gestorEstatico.getTexture("Basedoor"),
                        (int) d.getPosition().getX(),
                        (int) d.getPosition().getY());
            }
        }


        // 1.5. Desenhar Itens (Estáticos)


        // 2. Desenhar elementos das salas conforme o seu tipo
        switch(currentRoom.getType()){
            case COMBAT:
                drawEnemies();
                break;
            case TREASURE, STORE:
                drawItems();
                break;
            case BOSS:
                drawBoss();
                break;

        }

        drawDamageTexts();

        // DESENHAR O MERCADOR (Se a sala for STORE)
        if (currentRoom.getType() == RoomType.STORE && merchant != null) {
            // Usamos a textura do darkmage que está no gestorAnimado, mas como StoreObject herda de StaticAssets

            batch.draw(gestorEstatico.getTexture("StoreObject"), merchant.getPosition().getX(), merchant.getPosition().getY());
            // Texto "Press E" sobre o mercador se estiver perto
            if (merchant.canInteract(Mc.getInstance().getPosition()) && !isShopOpen) {
                font.getData().setScale(1.5f);
                font.draw(batch, "Press E", merchant.getPosition().getX()+10, merchant.getPosition().getY() + 120);
            }
        }

        Mc.getInstance().updateAttackTimer(Mc.getInstance().getDelta());
        TextureRegion framea = animAll.get("attackanimationt").getKeyFrame(Mc.getInstance().getAttackTimer(), false);
        Animation <TextureRegion> attackAnim = animAll.get("attackanimationt");
        // 3. Desenhar Jogador
        if (framePlayer != null) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !Mc.getInstance().isAttacking()){
                    Mc.getInstance().startAttack();
            }
            if(attackAnim.isAnimationFinished(Mc.getInstance().getAttackTimer()) && Mc.getInstance().isAttacking()){ Mc.getInstance().stopAttack();
            }else if(Mc.getInstance().isAttacking() && !attackAnim.isAnimationFinished(Mc.getInstance().getAttackTimer())){
                batch.draw(framea, (int) (Mc.getInstance().getPosition().getX() - 25), (int) (Mc.getInstance().getPosition().getY()+5));
            }
            batch.draw(framePlayer, (int)Mc.getInstance().getPosition().getX(), (int)Mc.getInstance().getPosition().getY());
        }



        batch.end();

        //Render para câmera do HUD
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        batch.end();

        drawHUD();
        drawShopUI();
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
                    case "chicken":
                        batch.draw(gestorEstatico.getTexture("chicken"), (int) c.getPosition().getX(), (int) c.getPosition().getY());
                        break;
                }
            }

        }
    }

    public void drawBoss(){
        if(currentBoss==null)return;
        switch(boss) {
            case BOSSCLOWN:
            TextureRegion framecb = animAll.get("bossclown").getKeyFrame(stateTime, true);
            currentBoss.giveF(framecb);
            currentBoss.move();
            batch.draw(framecb, (int) currentBoss.getPosition().getX(), (int) currentBoss.getPosition().getY());
            break;
            case BLUEDROPLET:
                TextureRegion framebd = animAll.get("bluedroplet").getKeyFrame(stateTime, true);
                currentBoss.giveF(framebd);
                currentBoss.move();
                batch.draw(framebd, (int) currentBoss.getPosition().getX(), (int) currentBoss.getPosition().getY());
                break;
            case FORESTOTEM:
                TextureRegion frameft = animAll.get("ForestTotem").getKeyFrame(stateTime, true);
                currentBoss.giveF(frameft);
                currentBoss.move();
                batch.draw(frameft, (int) currentBoss.getPosition().getX(), (int) currentBoss.getPosition().getY());
                break;
            default:
                break;
        }
    }

    public void drawHUD(){
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        drawIndicators();
        font.getData().setScale(2.0f);
        if(doorsVisible==true)font.draw(batch, "To enter Door press E", ((int)(Gdx.graphics.getWidth()) / 2)-150, (int)(Gdx.graphics.getHeight()) / 2);
        if(currentRoom.getType()== RoomType.TREASURE && doorsVisible == false)font.draw(batch, "Collect all items to go to next room", ((int)(Gdx.graphics.getWidth()) / 2)-130, (int)(Gdx.graphics.getHeight()) / 2);
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
            float oldScaleX = 2.0f;
            float oldScaleY = 2.0f;

            font.getData().setScale(baseScale + extraScale);

            // posição afinada para ficar alinhado com o texto "Health" da barra
            font.draw(batch,
                    ": " + Mc.getInstance().getHealth(),
                    100,                                   // um bocado à direita de "Health"
                    Gdx.graphics.getHeight() - 42);        // mais centrado verticalmente na barra

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
            font.draw(batch, "KOINS: " + Mc.getInstance().getBalanceCoins(), Gdx.graphics.getWidth()- 200, Gdx.graphics.getHeight() - 25);
            batch.draw(gestorEstatico.getTexture("coin_HUD"), Gdx.graphics.getWidth()- 80, Gdx.graphics.getHeight() - 80);
        }

        font.draw(batch, "DAMAGE: " + Mc.getInstance().getatkD(), Gdx.graphics.getWidth()- 198, Gdx.graphics.getHeight() - 85);

        batch.end();
    }


    private void drawIndicators() {
        // 1. Verificar se a textura da seta existe
        Texture arrowTexture = gestorEstatico.getTexture("arrow");
        if (arrowTexture == null) return;

        // Posição central do HUD (onde o jogador "estaria" no centro da câmara)
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        // Raio do círculo onde as setas vão ficar (distância do centro)
        float radius = 250f;

        // Posição real do Jogador no mundo
        float playerX = Mc.getInstance().getPosition().getX();
        float playerY = Mc.getInstance().getPosition().getY();

        // --- INDICADORES PARA INIMIGOS (Vermelho) ---
        batch.setColor(1f, 0f, 0f, 0.7f); // Tint Vermelho com transparência (0,70f valor maximo é 1), divide valores rgb por 255f ja que os  valores rgb do metodo setColor vão de 0 a 1
        for (Enemy e : enemies) {
            // Ignora se o inimigo já estiver morto (embora a lista enemies deva ter apenas vivos)
            if (e.isDead()) continue;

            drawSingleIndicator(arrowTexture, playerX, playerY, e.getPosition().getX(), e.getPosition().getY(), centerX, centerY, radius);
        }

        // --- INDICADORES PARA ITENS (Amarelo) ---
        batch.setColor(1f, 1f, 0f, 0.7f); // Tint Amarelo com transparência (0,70f valor maximo é 1), divide valores rgb por 255f ja que os  valores rgb do metodo setColor vão de 0 a 1
        for (staticAssets item : itemObjects) {
            // Podes filtrar aqui para não mostrar setas para moedas se forem muitas
            // if (item instanceof Coin) continue;

            drawSingleIndicator(arrowTexture, playerX, playerY, item.getPosition().getX(), item.getPosition().getY(), centerX, centerY, radius);
        }

        // --- INDICADORES PARA PORTAS (Ciano) ---
        // Só desenha se as portas estiverem visíveis
        if (doorsVisible) {
            batch.setColor(0f, 1f, 1f, 0.7f); // Cor Ciano com transparência (0,70f valor maximo é 1), divide valores rgb por 255f ja que os  valores rgb do metodo setColor vão de 0 a 1
            for (Door d : doors) {
                drawSingleIndicator(arrowTexture, playerX, playerY, d.getPosition().getX(), d.getPosition().getY(), centerX, centerY, radius);
            }
        }

        // --- INDICADOR PARA BOSS ---
        // Só desenha se as portas estiverem visíveis
        if (currentBoss!=null) {
            batch.setColor(128f / 255f, 39f / 255f, 179f / 255f, 0.7f);// Cor Roxa com transparência (0,70f valor maximo é 1), divide valores rgb por 255f ja que os  valores rgb do metodo setColor vão de 0 a 1
            drawSingleIndicator(arrowTexture, playerX, playerY, currentBoss.getPosition().getX(), currentBoss.getPosition().getY(), centerX, centerY, radius);

        }

        if (merchant != null) {
            batch.setColor(62f / 255f, 179f / 255f, 40f / 255f, 0.7f); // Cor Verde com transparência (0,70f valor maximo é 1), divide valores rgb por 255f ja que os  valores rgb do metodo setColor vão de 0 a 1
            drawSingleIndicator(arrowTexture, playerX, playerY, merchant.getPosition().getX(), merchant.getPosition().getY(), centerX, centerY, radius);
        }

        // Reset da cor do batch para branco (importante para não afetar o resto do HUD)
        batch.setColor(Color.WHITE); // Cor branca definida por nome e nao valores rgb de setColor
    }

    private void drawSingleIndicator(Texture textureIndicator, float pX, float pY, float tX, float tY, float cX, float cY, float radius) {
        // Calcular a diferença de posição
        float diffX = tX - pX;
        float diffY = tY - pY;

        // Calcular o ângulo em graus
        float angle = MathUtils.atan2(diffY, diffX) * MathUtils.radiansToDegrees;

        // Calcular a posição da seta no HUD (orbitando o centro)
        // Usamos MathUtils.cosDeg e sinDeg porque o angle está em graus
        float arrowX = cX + MathUtils.cosDeg(angle) * radius;
        float arrowY = cY + MathUtils.cosDeg(angle - 90) * radius; // Pequeno ajuste dependendo da orientação da tua matemática, geralmente cos/sin funciona bem.

        // Correção: Para coordenadas de ecrã padrão:
        arrowX = cX + MathUtils.cosDeg(angle) * radius;
        arrowY = cY + MathUtils.sinDeg(angle) * radius;

        float rotation = angle - 45f;

        // Centrar a textura na posição calculada
        float w = textureIndicator.getWidth();
        float h = textureIndicator.getHeight();
        TextureRegion regionTexture = new TextureRegion(textureIndicator);
        // Desenhar com rotação
        // batch.draw(textureIndicator, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        batch.draw(regionTexture, arrowX - w/2, arrowY - h/2, w/2, h/2, w, h, 1f, 1f, rotation);
    }

    // --- Geração de inimigos e itens para uma sala ---
    private void setupRoom(Room room) {
        enemies.clear();
        itemObjects.clear();
        posItems.clear();
        doors.clear();
        doorsVisible = false;
        merchant = null; // Resetar mercador
        isShopOpen = false; // Garantir que a loja fecha ao mudar de sala

        int qtdInimigos = room.getEnemyCount();
        int qtdItems = room.getItemCount();

        System.out.println("=== GERAÇÃO DE NÍVEL ===");
        System.out.println("Sala ID " + room.getId() + " (" + room.getType() + ")");
        System.out.println("Inimigos Gerados: " + qtdInimigos);
        System.out.println("Itens Gerados: " + qtdItems);

        // Inimigos
        switch (currentRoom.getType()) {
            case BOSS:
                boss = currentRoom.getBoss();
                switch(boss){
                    case BOSSCLOWN:
                        float bossclownX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
                        float bossclownY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);
                        currentBoss = new BossClown(new Position (bossclownX, bossclownY));
                        System.out.println("Boss Clown criado.");
                        break;
                    case BLUEDROPLET:
                        float bluedropletX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
                        float bluedropletY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);
                        currentBoss = new BlueDroplet(new Position (bluedropletX, bluedropletY));
                        System.out.println("Blue Droplet Boss criado.");
                        break;

                    case FORESTOTEM:
                        float foresttotemX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
                        float foresttotemY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);
                        currentBoss = new ForestTotem(new Position (foresttotemX, foresttotemY));
                        System.out.println("Forest Totem Boss criado.");
                        break;
                    default:
                        break;
                }
                break;

            case COMBAT:
                System.out.println("=== GERAÇÃO DE NÍVEL ===");
                System.out.println("Inimigos Gerados: " + qtdInimigos);

                for (int i = 0; i < qtdInimigos; i++) {
                    // 1. Gera as coordenadas UMA VEZ para qualquer inimigo
                    float posX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
                    float posY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);
                    Position spawnPos = new Position(posX, posY);

                    // 2. Decide qual inimigo que deverá ser criado
                    int n = (int) Distribuicoes.gerarUniforme(0, 3);

                    Enemy enemy = null;

                    switch (n) {
                        case 0:
                            enemy = new Zombie(spawnPos);
                            System.out.println("Zombie " + i + " criado.");
                            break;
                        case 1:
                            enemy = new Skeleton(spawnPos);
                            System.out.println("Skeleton " + i + " criado.");
                            break;
                        case 2:
                        default:
                            enemy = new DarkMage(spawnPos);
                            System.out.println("Dark Mage " + i + " criado.");
                            break;
                    }

                    if (enemy != null) {
                        enemies.add(enemy);
                        System.out.println("Posição: " + (int) posX + ", " + (int) posY);
                    }
                }
                break;
            case TREASURE:
                // Geração do numero de itens no jogo a nivel de probabilidade e nº tentativas por V.A Discreta Binomial

                nCoins = Distribuicoes.gerarBinomial(10, 0.5f);
                System.out.println(nCoins + ": Moedas");


                for (int i = 0; i < qtdItems; i++) {
                    int randomItems = (int) Distribuicoes.gerarUniforme(0,3);
                    // Gera X entre 0 e o limite do mundo (com margem de 100px)
                    switch(randomItems){
                        case 0:
                            nCoins++;
                            float coinX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
                            float coinY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);
                            itemObjects.add(new Coin(new Position(coinX, coinY)));
                            posItems.add(new Position(coinX, coinY));
                            System.out.println("Moeda " + i + " em: " + (int) coinX + ", " + (int) coinY);
                            break;
                        case 1:
                            nSwords++;
                            float swordX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
                            float swordY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);
                            itemObjects.add(new StaticSword(new Position(swordX, swordY)));
                            posItems.add(new Position(swordX, swordY));
                            System.out.println("Espada " + i + " em: " + (int) swordX + ", " + (int) swordY);
                            break;
                        case 2:
                            nChicken++;
                            float chickenX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
                            float chickenY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);
                            itemObjects.add(new Chicken(new Position(chickenX, chickenY)));
                            posItems.add(new Position(chickenX, chickenY));
                            System.out.println("Frango " + i + " em: " + (int) chickenX + ", " + (int) chickenY);
                            break;
                        default:
                            nCoins++;
                            float defaultX = Distribuicoes.gerarUniforme(0, LARGURA_MUNDO - 100);
                            float defaultY = Distribuicoes.gerarUniforme(0, ALTURA_MUNDO - 100);
                            itemObjects.add(new Coin(new Position(defaultX, defaultY)));
                            posItems.add(new Position(defaultX, defaultY));
                            System.out.println("Moeda " + i + " em: " + (int) defaultX + ", " + (int) defaultY);
                            break;
                    }



                }
                break;
            case STORE:
                System.out.println("=== SALA DE LOJA ===");
                // Coloca o mercador no meio da sala
                merchant = new StoreObject(new Position(LARGURA_MUNDO / 2f, ALTURA_MUNDO / 2f));

                // Configurar itens da loja
                itemsOnSale = new ArrayList<>();
                itemBounds = new ArrayList<>();

                // Exemplo: Vender 3 Espadas
                itemsOnSale.add(new ShopItem("Sword of Power", 10, "staticsword"));
                itemsOnSale.add(new ShopItem("Super Sword", 20, "staticsword"));
                itemsOnSale.add(new ShopItem("Hyper Sword", 50, "staticsword"));

                // Spawn das portas (para sair da sala)
                spawnDoorsForCurrentRoom();
                break;

        }
    }

    private void updateShopLogic() {
        // 1. Abrir/Fechar com a tecla E
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (isShopOpen) {
                isShopOpen = false; // Fecha se já estiver aberta
            } else if (merchant != null && merchant.canInteract(Mc.getInstance().getPosition())) {
                isShopOpen = true;  // Abre se estiver perto do mercador
            }
        }

        // Se a loja não estiver aberta, não processa cliques
        if (!isShopOpen) return;

        // 2. Processar Cliques do Rato
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // Converter coordenadas do rato para coordenadas do HUD (Inverter Y)
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Verificar clique no botão fechar (X)
            if (closeButtonBounds != null && closeButtonBounds.contains(mouseX, mouseY)) {
                isShopOpen = false;
                return;
            }

            // Verificar clique nos itens (Comprar)
            for (int i = 0; i < itemBounds.size(); i++) {
                if (itemBounds.get(i).contains(mouseX, mouseY)) {
                    ShopItem item = itemsOnSale.get(i);
                    buyItem(item);
                }
            }
        }
    }

    private void buyItem(ShopItem item) {
        if (Mc.getInstance().getBalanceCoins() >= item.price) {
            Mc.getInstance().removeBalanceCoins(item.price);

            // Lógica do efeito do item
            if (item.textureKey.equals("staticsword")) {
                Mc.getInstance().addAtkD(10); // Exemplo: Aumenta dano
            }

            System.out.println("Comprou: " + item.name);
        } else {
            System.out.println("Dinheiro insuficiente!");
        }
    }

    private void drawShopUI() {
        if (!isShopOpen) return;

        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        // --- 1. Fundo da Loja ---
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float windowW = 600;
        float windowH = 400;
        float windowX = (screenW - windowW) / 2;
        float windowY = (screenH - windowH) / 2;

        // Usamos uma textura existente esticada para fazer o fundo (ex: mapvoid ou criar um pixel preto)
        // Se 'mapvoid' for azul, a janela será azul.
        if (gestorEstatico.getTexture("mapvoid") != null) {
            batch.setColor(0.2f, 0.2f, 0.2f, 0.9f); // Escurecer para parecer fundo de UI
            batch.draw(gestorEstatico.getTexture("mapvoid"), windowX, windowY, windowW, windowH);
            batch.setColor(Color.WHITE); // Reset cor
        }

        // --- 2. Título ---
        font.draw(batch, "MERCHANT SHOP", windowX + 20, windowY + windowH - 20);
        font.draw(batch, "YOUR COINS: " + Mc.getInstance().getBalanceCoins(), windowX + 20, windowY + windowH - 50);

        // --- 3. Botão Fechar (X) no Canto Superior Direito ---
        float closeSize = 40;
        float closeX = windowX + windowW - closeSize - 10;
        float closeY = windowY + windowH - closeSize - 10;

        // Define a área do botão para o clique (só precisa ser feito uma vez ou quando redimensiona, mas aqui funciona)
        closeButtonBounds = new Rectangle(closeX, closeY, closeSize, closeSize);

        font.getData().setScale(2);
        font.setColor(1, 0, 0, 1); // Vermelho
        font.draw(batch, "X", closeX + 10, closeY + 35);
        font.setColor(1, 1, 1, 1); // Reset Branco
        font.getData().setScale(2); // Reset Escala (se alteraste antes)

        // --- 4. Desenhar Itens ---
        itemBounds.clear(); // Limpar áreas de clique antigas
        float startItemX = windowX + 50;
        float startItemY = windowY + windowH - 150;

        for (int i = 0; i < itemsOnSale.size(); i++) {
            ShopItem item = itemsOnSale.get(i);
            float itemX = startItemX + (i * 170);
            float itemY = startItemY;

            // Desenha ícone
            if (gestorEstatico.getTexture(item.textureKey) != null) {
                batch.draw(gestorEstatico.getTexture(item.textureKey), itemX, itemY, 64, 64);
            }

            // Desenha Preço e Nome
            font.getData().setScale(1.2f);
            font.draw(batch, item.name, itemX, itemY - 10);

            // Muda a cor do preço (Verde se tiver dinheiro, Vermelho se não)
            if (Mc.getInstance().getBalanceCoins() >= item.price) font.setColor(0, 1, 0, 1);
            else font.setColor(1, 0, 0, 1);

            font.draw(batch, "Koins: " + item.price, itemX, itemY - 40);
            font.setColor(1, 1, 1, 1); // Reset

            // Guardar área de clique deste item (para o rato funcionar)
            // Define uma caixa à volta do ícone e texto
            itemBounds.add(new Rectangle(itemX, itemY - 50, 100, 120));
        }

        batch.end();
    }

    private void spawnDoorsForCurrentRoom() {
        doors.clear();
        doorsVisible = true;
        int numDoors = 1;
        if(roomManager.getNextOptions().get(0).getType()== RoomType.BOSS){
             int numDoorsBoss = 1;
            float baseX = 700f;
            float spacing = 250f;
            float y = 350f;
            Position doorPos = new Position(baseX + 0 * spacing, y);
            doors.add(new Door(doorPos,0));
        }else {
            numDoors = currentRoom != null ? currentRoom.getNumberOfDoors() : 2;

            float baseX = 700f;
            float spacing = 250f;
            float y = 350f;

            for (int i = 0; i < numDoors; i++) {
                Position doorPos = new Position(baseX + i * spacing, y);
                doors.add(new Door(doorPos, i));
            }
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
                int skinroom = (int) Distribuicoes.gerarUniforme(0,3);
                switch(skinroom){
                    case 0:
                        mapaKey = "mapa1";
                        break;
                    case 1:
                        mapaKey = "mapa2";
                        break;
                    case 2:
                        mapaKey = "mapa3";
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

        switch (currentRoom.getType()) {
            case COMBAT:
            ArrayList<Enemy> mortos = new ArrayList<>();

            for (Enemy e : enemies) {
                if (mcPos.isWithinRange(e.getPosition().getX(), e.getPosition().getY(), PLAYER_ATTACK_RANGE)) {
                    int dano = Mc.getInstance().getatkDMc();
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
            break;
            case BOSS:
                if (currentBoss == null) return;
                Enemy bossKilled = null;

                if (Mc.getInstance().getPosition().isWithinRange(currentBoss.getPosition().getX(), currentBoss.getPosition().getY(), PLAYER_ATTACK_RANGE)) {
                    int dano = Mc.getInstance().getatkDMc();
                    currentBoss.takeDamage(dano);
                    System.out.println("Acertaste no boss! Vida do boss: " + currentBoss.getHealth());

                    // (Código do texto de dano mantém-se igual...)
                    DamageText dt = new DamageText(
                            currentBoss.getPosition().getX(),
                            currentBoss.getPosition().getY() + 40,
                            String.valueOf(dano),
                            DAMAGE_TEXT_DURATION
                    );
                    damageTexts.add(dt);

                    if (currentBoss.isDead()) {
                        bossKilled = currentBoss; // Marcar que matámos o boss

                        // --- ALTERAÇÃO AQUI ---
                        defeatedBosses++; // Incrementa o contador de bosses mortos
                        System.out.println("Boss derrotado! Total: " + defeatedBosses + "/" + RandomConfig.TOTAL_BOSSES);

                        // Verifica se o número de mortes iguala ou supera o total de bosses
                        if (defeatedBosses >= RandomConfig.TOTAL_BOSSES){
                            System.out.println("Último Boss morto! Parabéns, completaste o jogo!");
                            Gdx.app.exit();
                            System.exit(0);
                        }
                        // ----------------------
                    }
                }

                if(bossKilled != null) { // Se matámos o boss neste frame
                    currentBoss = null;
                    System.out.println("Boss removido da sala!");
                }
                break;
        }
    }

    // Dano por contacto com inimigos
    private void handleEnemyContactDamage(float delta) {
        Position mcPos = Mc.getInstance().getPosition();

        switch(currentRoom.getType()){
            case COMBAT:
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
        break;
            case BOSS:
                if (currentBoss == null) return;
                if (mcPos.isWithinRange(currentBoss.getPosition().getX(), currentBoss.getPosition().getY(), ENEMY_CONTACT_RANGE)) {
                    if (timeSinceLastPlayerHit >= PLAYER_HIT_COOLDOWN) {
                        Mc.getInstance().takeDamage(currentBoss.getatkD());
                        timeSinceLastPlayerHit = 0f;
                        System.out.println("O slime levou dano do boss! Vida atual: " + Mc.getInstance().getHealth());
                    }
                }
                break;
        }
    }

    // NÃO deixar o slime ficar em cima dos inimigos
    private void resolvePlayerEnemyCollision() {
        Position mcPos = Mc.getInstance().getPosition();

        switch (currentRoom.getType()) {
            case COMBAT:
                for (Enemy e : enemies) {
                    Position posene = new Position(mcPos.getX(),mcPos.getY());
                    float dist = posene.distanceTo(e.getPosition().getX(),e.getPosition().getY());

                    if (dist > 0f && dist < MIN_DISTANCE_BETWEEN_CHARACTERS) {
                        float overlap = MIN_DISTANCE_BETWEEN_CHARACTERS - dist;

                        // Direção normalizada
                        float nx = posene.getLastDx() / dist;
                        float ny = posene.getLastDy() / dist;

                        // Empurrar o slime para fora do inimigo
                        mcPos.setX(mcPos.getX() + nx * overlap);
                        mcPos.setY(mcPos.getY() + ny * overlap);
                    }
                }
                break;
            case BOSS:
                if (currentBoss == null) return;

                Position posbs = new Position(mcPos.getX(), mcPos.getY());
                float dist = posbs.distanceTo(currentBoss.getPosition().getX(), currentBoss.getPosition().getY());
                if (dist > 0f && dist < MIN_DISTANCE_BETWEEN_CHARACTERS) {
                    float overlap = MIN_DISTANCE_BETWEEN_CHARACTERS - dist;

                    // Direção normalizada
                    float nx = posbs.getLastDx() / dist;
                    float ny = posbs.getLastDy() / dist;

                    // Empurrar o slime para fora do inimigo
                    mcPos.setX(mcPos.getX() + nx * overlap);
                    mcPos.setY(mcPos.getY() + ny * overlap);
                }
        }
    }

    private void resolvePlayerMerchantCollision() {
        // Se não houver merchant (não estamos numa loja), não faz nada
        if (merchant == null) return;
        float merchantSize = 100f; // Tamanho fixo do mercador
        float playerSize = 45f;
        if (animAll.get("player") != null && animAll.get("player").getKeyFrames().length > 0) {
            playerSize = animAll.get("player").getKeyFrame(0).getRegionWidth();
        } else {
            playerSize = 45f;
        }

        Position mcPos = Mc.getInstance().getPosition();
        Position merchPos = merchant.getPosition();


        //  Definir o raio de colisão (Tamanho do "corpo" dos personagens)
        // Como o merchant é estático, podemos dar-lhe um raio fixo um pouco maior (ex: 45f)
        float collisionRadius = playerSize / 2;

        float mcCenterX = mcPos.getX() + collisionRadius;
        float mcCenterY = mcPos.getY() + collisionRadius;

        float closestX = MathUtils.clamp(mcCenterX, merchPos.getX(), merchPos.getX() + merchantSize);
        float closestY = MathUtils.clamp(mcCenterY, merchPos.getY(), merchPos.getY() + merchantSize);

        //  Calcular vetor de distância entre o centro do jogador e esse ponto
        float distanceX = mcCenterX - closestX;
        float distanceY = mcCenterY - closestY;

        float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);

        if (distanceSquared < (collisionRadius * collisionRadius)) {

            float distance = (float) Math.sqrt(distanceSquared);
            float overlap = collisionRadius - distance;

            // Vetor de direção normalizado (para onde empurrar)
            float normalX, normalY;

            if (distance > 0) {
                // Colisão normal (fora ou na borda)
                normalX = distanceX / distance;
                normalY = distanceY / distance;
            } else {
                // Caso raro: Jogador está exatamente dentro do mercador (distance = 0)
                // Empurrar para longe do centro do mercador
                float merchCenterX = merchPos.getX() + merchantSize / 2f;
                float merchCenterY = merchPos.getY() + merchantSize / 2f;
                float diffX = mcCenterX - merchCenterX;
                float diffY = mcCenterY - merchCenterY;
                float len = (float) Math.sqrt(diffX*diffX + diffY*diffY);

                if (len > 0) {
                    normalX = diffX / len;
                    normalY = diffY / len;
                } else {
                    normalX = 1; normalY = 0; // Direção arbitrária se estiver no pixel exato do centro
                }
            }

            // Aplicar a correção na posição do jogador
            mcPos.setX(mcPos.getX() + normalX * overlap);
            mcPos.setY(mcPos.getY() + normalY * overlap);
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