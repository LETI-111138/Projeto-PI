package startgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;

import java.awt.*;
import java.util.ArrayList;

public class Main {
    public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        // Configurações da Janela
        config.setTitle("Last Blob Standing");
        config.setWindowedMode(1920, 1080);
        config.setForegroundFPS(60);
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        config.setWindowIcon("assets/Static/gameicon.png");

        // Iniciar o jogo
        new Lwjgl3Application(new gameinit(), config);

    }


}