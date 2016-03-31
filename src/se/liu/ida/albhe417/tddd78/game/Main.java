package se.liu.ida.albhe417.tddd78.game;

import java.io.File;

public class Main
{
    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("lwjgl/native").getAbsolutePath());
	    Game game = new Game();
 	    game.run();
    }
}
