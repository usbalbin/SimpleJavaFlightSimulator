package se.liu.ida.albhe417.tddd78.gui;

import net.miginfocom.swing.MigLayout;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.Settings;
import se.liu.ida.albhe417.tddd78.game.game_object.VehicleType;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * MenuFrame is the gui for the game. There is buttons for starting the game.
 * The gui also contains bars and buttons for manipulating game setting in real time while playing.
 */
public class MenuFrame extends JFrame {
    private final Settings settings;


    public MenuFrame() throws IOException, FileNotFoundException {
        super("Title");
        this.settings = new Settings();
        this.setLayout(new MigLayout(
                "",
                "[][][grow][][]",
                "[][][][grow][]"
        ));

        this.add(new JLabel("Some nice text"), "span 6, center, wrap");

        this.add(new JSeparator(), "span 6, growx, wrap");

        setupThreadingBox("");
        setupWireFrameBox("wrap");

        this.add(new JSeparator(), "span 6, growx, wrap");

        setupTickPerFrameSlider("");
        setupQualityFactorSlider("center");
        setupDrawDistSlider("wrap");

        this.add(new JSeparator(), "span 6, growx, wrap");

        startButton("gapright");
        setupVehicleSelector("");
        setupSaveButton("");
        terrainFileChooser("right");
        helpButton("right");
        exitButton("right, wrap");

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.pack();
    }

    private void setupDrawDistSlider(String constraints){
        final int min = 512, max = 8192;
        final int defaultValue = (int) settings.getDrawDistance();

        JSlider maxDrawDistanceSlider = new JSlider(SwingConstants.VERTICAL, min, max, defaultValue);

        //JSlider.setLabelTable requires obsolete Hashtable
        Hashtable<Integer, JLabel> labels = new Hashtable<>(3);
        JLabel label = new JLabel("Draw distance: " + defaultValue);

        labels.put(min, new JLabel(min + " m"));
        labels.put((max - min) / 2, label);
        labels.put(max, new JLabel(max + " m"));
        maxDrawDistanceSlider.setLabelTable(labels);
        maxDrawDistanceSlider.setPaintLabels(true);

        ChangeListener sliderListener = e -> {
            float maxDrawDistance = maxDrawDistanceSlider.getValue();
            settings.setDrawDistance(maxDrawDistance);
            label.setText("Draw distance: " + maxDrawDistance);
        };
        sliderListener.stateChanged(null);
        maxDrawDistanceSlider.addChangeListener(sliderListener);
        this.add(maxDrawDistanceSlider, constraints);
    }

    private void setupVehicleSelector(String constraints){

        JComboBox<VehicleType> vehicleSelector = new JComboBox<>(VehicleType.values());

        ActionListener listener =
                e -> settings.setVehicleType((VehicleType) vehicleSelector.getSelectedItem());

        vehicleSelector.addActionListener(listener);
        vehicleSelector.setSelectedItem(settings.getVehicleType());
        this.add(vehicleSelector, constraints);
    }

    private void setupQualityFactorSlider(String constraints){
        final int min = 50, max = 1000;
        final int defaultValue = settings.getDetailFactor();

        JSlider qualityFactorSlider = new JSlider(SwingConstants.VERTICAL, min, max, defaultValue);

        //JSlider.setLabelTable requires obsolete Hashtable
        Hashtable<Integer, JLabel> labels = new Hashtable<>(3);
        JLabel label = new JLabel("Quality factor: " + defaultValue);

        labels.put(min, new JLabel(min + " m"));
        labels.put((max - min) / 2, label);
        labels.put(max, new JLabel(max + " m"));
        qualityFactorSlider.setLabelTable(labels);
        qualityFactorSlider.setPaintLabels(true);

        ChangeListener sliderListener = e -> {
            int qualityFactor = qualityFactorSlider.getValue();
            settings.setDetailFactor(qualityFactor);
            label.setText("Quality factor: " + qualityFactor);
        };
        sliderListener.stateChanged(null);
        qualityFactorSlider.addChangeListener(sliderListener);
        this.add(qualityFactorSlider, constraints);
    }

    private void setupTickPerFrameSlider(String constraints){
        final int min = 1, max = 25;
        final int defaultValue = settings.getTicksPerFrame();

        JSlider ticksPerFrameSlider = new JSlider(SwingConstants.VERTICAL, min, max, defaultValue);

        //JSlider.setLabelTable requires obsolete Hashtable
        Hashtable<Integer, JLabel> labels = new Hashtable<>(3);
        JLabel label = new JLabel("Ticks / frame: " + defaultValue);

        labels.put(min, new JLabel(min + ""));
        labels.put((max - min) / 2, label);
        labels.put(max, new JLabel(max + ""));
        ticksPerFrameSlider.setLabelTable(labels);
        ticksPerFrameSlider.setPaintLabels(true);

        ChangeListener sliderListener = e -> {
            int tickPerFrame = ticksPerFrameSlider.getValue();
            settings.setTicksPerFrame(tickPerFrame);
            label.setText("Ticks / frame: " + tickPerFrame);
        };
        sliderListener.stateChanged(null);
        ticksPerFrameSlider.addChangeListener(sliderListener);
        this.add(ticksPerFrameSlider, constraints);
    }

    private void setupWireFrameBox(String constraints){
        JCheckBox wireFrameCheckBox = new JCheckBox("Enable wire frame");
        ActionListener listener = e -> settings.setWireFrame(wireFrameCheckBox.isSelected());
        listener.actionPerformed(null);
        wireFrameCheckBox.addActionListener(listener);
        this.add(wireFrameCheckBox, constraints);
    }

    private void setupThreadingBox(String constraints){
        JCheckBox threadingCheckBox = new JCheckBox("Multi threading");
        threadingCheckBox.setSelected(settings.isThreaded());
        ChangeListener listener = e -> settings.setThreaded(threadingCheckBox.isSelected());
        threadingCheckBox.addChangeListener(listener);
        this.add(threadingCheckBox, constraints);
    }

    private void startButton(String constraints){

        JButton startButton = new JButton("Start game");
        startButton.setMnemonic(KeyEvent.VK_ACCEPT);

        ActionListener listener = e -> {
            Game game = new Game(settings);
            Thread gameThread = new Thread(game);
            gameThread.start();
        };
        startButton.addActionListener(listener);
        this.add(startButton, constraints);
    }

    private void setupSaveButton(String constraints){
        JButton saveButton = new JButton("Save settings");

        ActionListener listener = e ->
                settings.saveProperties();
        saveButton.addActionListener(listener);
        this.add(saveButton, constraints);
    }

    private void helpButton(String constraints){

        JButton helpButton = new JButton("Help");

        ActionListener listener = e ->
            JOptionPane.showMessageDialog(this, "Steer with W,A,S,D and arrows");
        helpButton.addActionListener(listener);

        this.add(helpButton, constraints);
    }

    private void exitButton(String constraints){
        JButton exitButton = new JButton("Exit");

        exitButton.addActionListener(e ->
            System.exit(0)
        );

        this.add(exitButton, constraints);
    }

    private void terrainFileChooser(String constraints){
        JButton selectTerrainButton = new JButton("Select terrain");

        selectTerrainButton.addActionListener(e ->
            settings.loadImage()
        );
        this.add(selectTerrainButton, constraints);


    }

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("lwjgl/native").getAbsolutePath());

        try{
            MenuFrame m = new MenuFrame();
            m.setVisible(true);
        }catch (IOException e){
            String msg = "Failed to start game!:\n " + e.getMessage();
            JOptionPane.showMessageDialog(null, msg);
            Logger.getGlobal().severe(msg);
        }
    }
}
