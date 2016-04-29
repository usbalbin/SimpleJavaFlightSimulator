package se.liu.ida.albhe417.tddd78.game;

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * Project TDDD78
 *
 * File created by Albin on 10/03/2016.
 */
public final class InputHandler extends GLFWKeyCallback {
    private static final InputHandler INSTANCE = new InputHandler();

    private final boolean[] keys;

    private InputHandler(){
        int numberOfKeys = java.lang.Short.MAX_VALUE + 1;
        keys = new boolean[numberOfKeys];
    }

    @Override
    public void invoke(long window, int key, int scanCode, int action, int mods) {
        assert key >= 0 : "Something went wrong, should not happen";
        if(key < 0)
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
