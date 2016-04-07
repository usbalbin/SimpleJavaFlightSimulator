package se.liu.ida.albhe417.tddd78.game;

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Albin on 10/03/2016.
 */
public class InputHandler extends GLFWKeyCallback {
    private static int NUMBER_OF_KEYS = 1 << 16;
    private static final InputHandler INSTANCE = new InputHandler();

    private boolean[] keys;

    private InputHandler(){
        keys = new boolean[NUMBER_OF_KEYS];
    }

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if(key == -1)
            return;
        keys[key] = action != GLFW_RELEASE;
    }

    public boolean isPressed(int key){
        return keys[key];
    }

    public static InputHandler getInstance(){
        return INSTANCE;
    }
}
