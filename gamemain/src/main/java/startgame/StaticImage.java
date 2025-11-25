package startgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import java.util.ArrayList;
import java.util.HashMap;

public class StaticImage implements carregarAssets {

    // HashMap para guardar as texturas: "NomeDoFicheiro" -> Textura
    private HashMap<String, Texture> texturasMap;

    public StaticImage() {
        texturasMap = new HashMap<>();

        // 1. Carregar a lista de ficheiros da pasta Static
        ArrayList<FileHandle> ficheiros = carregarListaAssets("assets/Static");

        // 2. Criar Texturas para cada ficheiro encontrado
        for (FileHandle file : ficheiros) {
            try {
                Texture t = new Texture(file);
                // Guarda no mapa com o nome sem extensão (ex: "mapa1.png" vira chave "mapa1")
                texturasMap.put(file.nameWithoutExtension(), t);
                System.out.println("Imagem Estática Carregada: " + file.nameWithoutExtension());
            } catch (Exception e) {
                System.err.println("Erro ao carregar textura: " + file.name());
            }
        }
    }

    // Método para obter uma textura pelo nome
    public Texture getTexture(String nome) {
        return texturasMap.get(nome);
    }

    @Override
    public ArrayList<FileHandle> carregarListaAssets(String path) {
        ArrayList<FileHandle> lista = new ArrayList<>();
        FileHandle pasta = Gdx.files.internal(path);

        if (pasta.exists() && pasta.isDirectory()) {
            for (FileHandle file : pasta.list()) {
                if (!file.isDirectory() && (file.extension().equals("png") || file.extension().equals("jpg"))) {
                    lista.add(file);
                }
            }
        }
        return lista;
    }

    @Override
    public void dispose() {
        for (Texture t : texturasMap.values()) {
            t.dispose();
        }
    }

    // Método não usado mas exigido pela interface antiga (pode remover se limpar a interface)
    public ArrayList<FileHandle> getImages() { return null; }
}