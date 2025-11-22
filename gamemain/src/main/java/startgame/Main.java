package startgame;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Main {
    public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        // Configurações da Janela
        config.setTitle("Projeto PI - Game Engine");
        config.setWindowedMode(1280, 720);
        config.setForegroundFPS(60);

        // Iniciar o jogo
        new Lwjgl3Application(new gameinit(), config);
    }
}