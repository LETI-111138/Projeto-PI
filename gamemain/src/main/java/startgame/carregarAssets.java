package startgame;

import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;

public interface carregarAssets {
    public ArrayList<FileHandle> carregarListaAssets(String path);

    public ArrayList<FileHandle> getImages();
    public void dispose(); // Importante para limpar a memória gráfica

}
