package se.liu.ida.albhe417.tddd78.game;

public class Main
{
    public static void main(String[] args) {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

	    Game game = new Game();
 	    game.run();
    }
}
