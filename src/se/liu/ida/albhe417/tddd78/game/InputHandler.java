package se.liu.ida.albhe417.tddd78.game;

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Project TDDD78
 *
 * File created by Albin on 10/03/2016.
 */
public class InputHandler extends GLFWKeyCallback {
    private static final InputHandler INSTANCE = new InputHandler();

    private final boolean[] keys;

    private InputHandler(){
        int numberOfKeys = 1 << 16;
        keys = new boolean[numberOfKeys];
    }

    @Override
    public void invoke(long window, int key, int scanCode, int action, int mods) {
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
