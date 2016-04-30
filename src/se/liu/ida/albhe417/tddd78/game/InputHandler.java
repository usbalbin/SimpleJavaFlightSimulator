package se.liu.ida.albhe417.tddd78.game;

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * InputHandler if implemented right gets notified on every keyboard change and keeps this information for querying by others.
 * Thus it makes sense to have it as a singleton.
 */
public final class InputHandler extends GLFWKeyCallback {
    private static final InputHandler INSTANCE = new InputHandler();

    private final boolean[] keys;

    private InputHandler(){
        final int numberOfKeys = java.lang.Short.MAX_VALUE + 1;
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
