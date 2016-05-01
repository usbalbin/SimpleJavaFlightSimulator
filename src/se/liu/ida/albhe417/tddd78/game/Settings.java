package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.game.game_object.VehicleType;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Settings contains all settings for the game by storing them in a property
 */
public final class Settings
{
    private Properties properties;

	/**
	 * Path to user config, relative to JAR-file
     */
    public static final String PROPERTY_PATH = "config.properties";

	/**
	 * Path to default config, inside JAR
     */
    public static final String DEFAULT_PROPERTY_PATH = "content/default.properties";

    private final AtomicReference<BufferedImage> rTerrainFile = new AtomicReference<>();


    public Settings() throws IOException, FileNotFoundException {
        loadProperties();

        try(InputStream inputStream =  Game.class.getResourceAsStream(getDefaultTerrainPath())){
            setTerrainImage(ImageIO.read(inputStream));
            if(getTerrainImage() == null){
                JOptionPane.showMessageDialog(null, "Failed to load default heightmap");
                loadImage();
            }
        }catch (IOException e){
            JOptionPane.showMessageDialog(null,
                "Failed to load default heightmap\n" +
                e.getMessage()
            );
            loadImage();
        }


    }

    private void loadProperties() throws IOException, FileNotFoundException {
        Properties defaults;
        defaults = new Properties();

        try(InputStream inputStream = Game.class.getResourceAsStream(DEFAULT_PROPERTY_PATH)){
            if(inputStream == null)
                loadDefaultProperties(defaults);
            defaults.load(inputStream);
        }catch (IOException ignored){
            loadDefaultProperties(defaults);
        }

        properties = new Properties(defaults);

        try(FileInputStream inputStream = new FileInputStream(PROPERTY_PATH)){
            properties.load(inputStream);
        }catch (IOException ignored){
            String msg = "User properties could not be loaded, falling back to defaults";
            Logger.getGlobal().warning(msg);
        }
    }

    private void loadDefaultProperties(Properties defaults) throws IOException, FileNotFoundException {
        JOptionPane.showMessageDialog(null, "Could not load default settings! Try specifying the path manually");
        JFileChooser defaultFileChooser = new JFileChooser();
        defaultFileChooser.showOpenDialog(null);

        if(defaultFileChooser.getSelectedFile() == null)
            throw new IOException("Failed to load file property file");
        FileInputStream inputStream = new FileInputStream(defaultFileChooser.getSelectedFile());

        defaults.load(inputStream);
    }

    public void saveProperties(){
        try(OutputStream outputStream = new FileOutputStream(PROPERTY_PATH)) {

            properties.store(outputStream, "User config for Simple Java Flight Simulator");
        }catch (IOException e){
            String msg = "Could not save user config: " + e.getMessage();
            JOptionPane.showMessageDialog(null, msg);
            Logger.getGlobal().warning(msg);
        }

    }

    public void loadImage(){

        JFileChooser terrainFileSelector = new JFileChooser(getDefaultTerrainPath());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg and png files", "jpg", "png");
        terrainFileSelector.setFileFilter(filter);

        terrainFileSelector.showOpenDialog(null);

        if (terrainFileSelector.getSelectedFile() == null) {
            String filePath = getDefaultTerrainPath();

            try(InputStream inputStream = Game.class.getResourceAsStream(filePath)) {
                setTerrainImage(ImageIO.read(inputStream));
            } catch (IOException ex) {
                String msg = "Failed to load default heightmap image\n" + ex.getMessage();
                Logger.getGlobal().warning(msg);
                JOptionPane.showMessageDialog(null,
                                              msg
                );
                loadImage();
            }
        }
        else {
            File file = terrainFileSelector.getSelectedFile();

            try{
                setTerrainImage(ImageIO.read(file));
            }catch (IOException ex){
                String msg = "Failed to load heightmap\n" + ex.getMessage();
                if (Logger.getGlobal().isLoggable(Level.FINE))
                    Logger.getGlobal().fine(msg);
                JOptionPane.showMessageDialog(null,
                                              msg
                );
                loadImage();
            }
        }

    }

    public int getWindowWidth() {
        return Integer.parseInt(properties.getProperty("windowWidth"));
    }

    public void setWindowWidth(int windowWidth) {
        properties.setProperty("windowWidth", Integer.toString(windowWidth));
    }

    public int getWindowHeight() {
        return Integer.parseInt(properties.getProperty("windowHeight"));
    }

    public float getAspectRatio(){
        return (float) getWindowWidth() / getWindowHeight();
    }

    public void setWindowHeight(int windowHeight) {
        properties.setProperty("windowHeight", Integer.toString(windowHeight));
    }

    public float getFov() {
        return Float.parseFloat(properties.getProperty("fov"));
    }

    public float getDrawDistance() {
        return Float.parseFloat(properties.getProperty("drawDistance"));
    }

    public void setDrawDistance(float drawDistance) {
        properties.setProperty("drawDistance", Float.toString(drawDistance));
    }

    public float getDrawDistanceNearLimit() {
        return Float.parseFloat(properties.getProperty("drawDistanceNearLimit"));
    }

    public int getDetailFactor() {
        return Integer.parseInt(properties.getProperty("detailFactor"));
    }

    public void setDetailFactor(int detailFactor) {
        properties.setProperty("detailFactor",Integer.toString(detailFactor));
    }

    public int getTicksPerFrame() {
        return Integer.parseInt(properties.getProperty("ticksPerFrame"));
    }

    public void setTicksPerFrame(int ticksPerFrame) {
        properties.setProperty("ticksPerFrame", Integer.toString(ticksPerFrame));
    }

    public float getPreferredTimeStep(){
        final float tickToTimeStep = 1/60.0f;
        return tickToTimeStep/getTicksPerFrame();
    }

    public boolean isWireFrame() {
        return Boolean.parseBoolean(properties.getProperty("wireFrame"));
    }

    public void setWireFrame(boolean wireFrame) {
        properties.setProperty("wireFrame", Boolean.toString(wireFrame));
    }

    public boolean isThreaded() {
        return Boolean.parseBoolean(properties.getProperty("threaded"));
    }

    public void setThreaded(boolean threaded) {
        properties.setProperty("threaded", Boolean.toString(threaded));
    }

    public String getPlayerName(){
        return properties.getProperty("playerName");
    }

    public BufferedImage getTerrainImage(){
        return rTerrainFile.get();
    }

    private void setTerrainImage(BufferedImage terrainImage){
        rTerrainFile.set(terrainImage);
    }

    public void setVehicleType(VehicleType vehicleType){
        properties.setProperty("vehicleType", vehicleType.toString());
    }

    public VehicleType getVehicleType(){
        return VehicleType.valueOf(properties.getProperty("vehicleType"));
    }

    public int getAALevel(){
        return Integer.parseInt(properties.getProperty("AALevel"));
    }

    public String getOpenglVersion(){
        return properties.getProperty("openglVersion");
    }

    public int getOpenglVersionMajor(){
        return Integer.parseInt(getOpenglVersion().split("\\.")[0]);
    }

    public int getOpenglVersionMinor(){
        return Integer.parseInt(getOpenglVersion().split("\\.")[1]);
    }

    public String getDefaultTerrainPath(){
        return properties.getProperty("defaultTerrainPath");
    }

}
