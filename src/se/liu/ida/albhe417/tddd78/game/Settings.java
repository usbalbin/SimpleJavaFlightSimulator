package se.liu.ida.albhe417.tddd78.game;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Project TDDD78
 *
 * File created by Albin on 09/04/2016.
 */
public class Settings {

    private final AtomicInteger windowWidth = new AtomicInteger(1400);
    private final AtomicInteger windowHeight = new AtomicInteger(800);

    private final AtomicInteger fov = new AtomicInteger(Float.floatToIntBits((90 * (float)Math.PI / 180.0f)));
    private final AtomicInteger drawDistance = new AtomicInteger();// = new AtomicInteger(Float.floatToIntBits(3072));
    private final AtomicInteger drawDistanceNearLimit = new AtomicInteger(Float.floatToIntBits(1.0f));
    private final AtomicInteger detailFactor = new AtomicInteger();
    private final AtomicInteger maxLevels = new AtomicInteger(11);
    private final AtomicInteger ticksPerFrame = new AtomicInteger();
    private final AtomicInteger preferredTimeStep = new AtomicInteger();

    private final AtomicBoolean wireFrame = new AtomicBoolean(false);// = false;
    private final AtomicBoolean threaded = new AtomicBoolean(false);// = true;
    public final int AA_LEVEL = 16;
    public final float OPENGL_VERSION = 3.0f;

    private String playerName = "Player1";
    private final AtomicReference<String> rPlayerName = new AtomicReference<>(playerName);


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

    public String getPlayerName(){
        return rPlayerName.get();
    }

    public void setPlayerName(String playerName){
        rPlayerName.set(playerName);
    }

    private void setFloat(float value, AtomicInteger res){
        res.set(Float.floatToIntBits(value));
    }

    private float getFloat(AtomicInteger value){
        return Float.intBitsToFloat(value.get());
    }

}
