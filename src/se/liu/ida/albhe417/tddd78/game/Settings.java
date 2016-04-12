package se.liu.ida.albhe417.tddd78.game;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Albin on 09/04/2016.
 */
public class Settings {
    private final int windowPosX = 50;
    private final int windowPosY = 50;

    private AtomicInteger windowWidth = new AtomicInteger(1400);
    private AtomicInteger windowHeight = new AtomicInteger(800);

    private AtomicInteger fov = new AtomicInteger(Float.floatToIntBits((90 * (float)Math.PI / 180.0f)));
    private AtomicInteger drawDistance = new AtomicInteger();// = new AtomicInteger(Float.floatToIntBits(3072));
    private AtomicInteger drawDistanceNearLimit = new AtomicInteger(Float.floatToIntBits(1.0f));
    private AtomicInteger detailFactor = new AtomicInteger();
    private AtomicInteger maxLevels = new AtomicInteger(11);
    private AtomicInteger ticksPerFrame = new AtomicInteger();
    private AtomicInteger preferredTimeStep = new AtomicInteger();

    private AtomicBoolean wireFrame = new AtomicBoolean(false);// = false;
    private AtomicBoolean threaded = new AtomicBoolean(false);// = true;
    public final int AA_LEVEL = 16;
    public final float OPENGL_VERSION = 3.0f;


    public Settings() {

    }

    public int getWindowWidth() {
        return windowWidth.get();
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth.set(windowWidth);
    }

    public int getWindowHeight() {
        return windowHeight.get();
    }

    public float getAspectRatio(){
        return (float) getWindowWidth() / getWindowHeight();
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight.set(windowHeight);
    }

    public int getWindowPosX() {
        return windowPosX;
    }

    public int getWindowPosY() {
        return windowPosY;
    }

    public float getFov() {
        return getFloat(fov);
    }

    public void setFov(float fov) {
        setFloat(fov, this.fov);
    }

    public float getDrawDistance() {
        return getFloat(drawDistance);
    }

    public void setDrawDistance(float drawDistance) {
        setFloat(drawDistance, this.drawDistance);
    }

    public float getDrawDistanceNearLimit() {
        return getFloat(drawDistanceNearLimit);
    }

    public void setDrawDistanceNearLimit(float drawDistanceNearLimit) {
        setFloat(drawDistanceNearLimit, this.drawDistanceNearLimit);
    }

    public int getDetailFactor() {
        return detailFactor.get();
    }

    public int getMaxLevels(){
        return maxLevels.get();
    }

    public void setDetailFactor(int detailFactor) {
        this.detailFactor.set(detailFactor);
    }

    public int getTicksPerFrame() {
        return ticksPerFrame.get();
    }

    public void setTicksPerFrame(int ticksPerFrame) {
        this.ticksPerFrame.set(ticksPerFrame);
        setPreferredTimeStep(1/60.0f/ticksPerFrame);
    }

    public float getPreferredTimeStep(){
        return getFloat(preferredTimeStep);
    }

    private void setPreferredTimeStep(float preferredTimeStep){
        setFloat(preferredTimeStep, this.preferredTimeStep);
    }

    public boolean isWireFrame() {
        return wireFrame.get();
    }

    public void setWireFrame(boolean wireFrame) {
        this.wireFrame.set(wireFrame);
    }

    public boolean isThreaded() {
        return threaded.get();
    }

    public void setThreaded(boolean threaded) {
        this.threaded.set(threaded);
    }

    private void setFloat(float value, AtomicInteger res){
        res.set(Float.floatToIntBits(value));
    }

    private float getFloat(AtomicInteger value){
        return Float.intBitsToFloat(value.get());
    }

}
