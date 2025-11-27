package startgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.HashMap;

public class AnimatedImage implements carregarAssets {

    // Guarda as animações prontas a usar
    public static HashMap<String, Animation<TextureRegion>> animacoesMap;
    // Guarda as folhas originais para limpar memória
    public static ArrayList<Texture> sheets;

    public AnimatedImage() {
        animacoesMap = new HashMap<>();
        sheets = new ArrayList<>();
        // Não carregamos tudo automaticamente no construtor porque precisamos saber as colunas/linhas de cada um
    }


    public void criarAnimacao(String nomeFicheiro, String nomeChave, int cols, int rows, float duracao) {
        try {
            Texture sheet = new Texture(Gdx.files.internal("assets/Animated/" + nomeFicheiro));
            sheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // Pixel Art nítido
            sheets.add(sheet);

            TextureRegion[][] tmp = TextureRegion.split(sheet,
                    sheet.getWidth() / cols,
                    sheet.getHeight() / rows);

            TextureRegion[] frames = new TextureRegion[cols * rows];
            int index = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    frames[index++] = tmp[i][j];
                }
            }

            Animation<TextureRegion> anim = new Animation<>(duracao, frames);
            animacoesMap.put(nomeChave, anim);
            System.out.println("Animação criada: " + nomeChave);

        } catch (Exception e) {
            System.err.println("Erro ao criar animação " + nomeFicheiro + ": " + e.getMessage());
        }
    }

    public Animation<TextureRegion> getAnimacao(String nomeChave) {
        return animacoesMap.get(nomeChave);
    }

    //public void cameraAnim(){}


    public static HashMap<String,Animation<TextureRegion>> getAnimAll(){
        return animacoesMap;
    }

    public ArrayList<Texture> getSheets() {return sheets;}

    @Override
    public void dispose() {
        for (Texture t : sheets) {
            t.dispose();
        }
    }

    // Métodos da interface
    @Override
    public ArrayList<FileHandle> carregarListaAssets(String path) { return null; }
    public ArrayList<FileHandle> getImages() { return null; }
}