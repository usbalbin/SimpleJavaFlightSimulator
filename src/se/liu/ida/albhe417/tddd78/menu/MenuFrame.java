package se.liu.ida.albhe417.tddd78.menu;

import net.miginfocom.swing.MigLayout;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.QuadTree;
import se.liu.ida.albhe417.tddd78.game.Settings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Objects;

/**
 * Created by Albin on 08/04/2016.
 */
public class MenuFrame extends JFrame {
    private Settings settings;
    private Game game;


    public MenuFrame(){
        super("Title");
        this.settings = new Settings();
        this.setLayout(new MigLayout(
                "",
                "[][grow][]",
                "[][][][grow][]"
        ));

        this.add(new JLabel("Some nice text"), "span 3, center, wrap");

        this.add(new JSeparator(), "span 3, growx, wrap");

        setupThreadingBox("");
        setupWireFrameBox("wrap");

        this.add(new JSeparator(), "span 3, growx, wrap");

        setupTickPerFrameSlider("");
        setupQualityFactorSlider("center");
        setupDrawDistSlider("wrap");

        this.add(new JSeparator(), "span 3, growx, wrap");

        startButton("gapright unrelated");
        this.add(new JButton("Help"), "gapright unrelated");
        this.add(new JButton("Exit"), "right, wrap");

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //this.add(new JTextPane());
        this.pack();
        this.setVisible(true);

    }

    private void setupDrawDistSlider(Object constraints){
        final int min = 512, max = 8192;
        final int defaultValue = 3072;

        JSlider maxDrawDistanceSlider = new JSlider(JSlider.VERTICAL, min, max, defaultValue);
        Hashtable<Integer, JLabel> labels = new Hashtable<>(3);
        JLabel label = new JLabel("Draw distance: " + defaultValue);

        labels.put(min, new JLabel(min + " m"));
        labels.put((max - min) / 2, label);
        labels.put(max, new JLabel(max + " m"));
        maxDrawDistanceSlider.setLabelTable(labels);
        maxDrawDistanceSlider.setPaintLabels(true);

        ChangeListener sliderListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float maxDrawDistance = maxDrawDistanceSlider.getValue();
                settings.setDrawDistance(maxDrawDistance);
                label.setText("Draw distance: " + maxDrawDistance);
            }
        };
        sliderListener.stateChanged(null);
        maxDrawDistanceSlider.addChangeListener(sliderListener);
        this.add(maxDrawDistanceSlider, constraints);
    }

    private void setupQualityFactorSlider(Object constraints){
        final int min = 50, max = 1000;
        final int defaultValue = 350;

        JSlider qualityFactorSlider = new JSlider(JSlider.VERTICAL, min, max, defaultValue);
        Hashtable<Integer, JLabel> labels = new Hashtable<>(3);
        JLabel label = new JLabel("Quality factor: " + defaultValue);

        labels.put(min, new JLabel(min + " m"));
        labels.put((max - min) / 2, label);
        labels.put(max, new JLabel(max + " m"));
        qualityFactorSlider.setLabelTable(labels);
        qualityFactorSlider.setPaintLabels(true);

        ChangeListener sliderListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int qualityFactor = qualityFactorSlider.getValue();
                settings.setDetailFactor(qualityFactor);
                label.setText("Quality factor: " + qualityFactor);
            }
        };
        sliderListener.stateChanged(null);
        qualityFactorSlider.addChangeListener(sliderListener);
        this.add(qualityFactorSlider, constraints);
    }

    private void setupTickPerFrameSlider(Object constraints){
        final int min = 1, max = 25;
        final int defaultValue = 10;

        JSlider ticksPerFrameSlider = new JSlider(JSlider.VERTICAL, min, max, defaultValue);
        Hashtable<Integer, JLabel> labels = new Hashtable<>(3);
        JLabel label = new JLabel("Ticks / frame: " + defaultValue);

        labels.put(min, new JLabel(min + ""));
        labels.put((max - min) / 2, label);
        labels.put(max, new JLabel(max + ""));
        ticksPerFrameSlider.setLabelTable(labels);
        ticksPerFrameSlider.setPaintLabels(true);

        ChangeListener sliderListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int tickPerFrame = ticksPerFrameSlider.getValue();
                settings.setTicksPerFrame(tickPerFrame);
                label.setText("Ticks / frame: " + tickPerFrame);
            }
        };
        sliderListener.stateChanged(null);
        ticksPerFrameSlider.addChangeListener(sliderListener);
        this.add(ticksPerFrameSlider, constraints);
    }

    private void setupWireFrameBox(Object constraints){
        JCheckBox wireFrameCheckBox = new JCheckBox("Enable wire frame");
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.setWireFrame(wireFrameCheckBox.isSelected());
            }
        };
        listener.actionPerformed(null);
        wireFrameCheckBox.addActionListener(listener);
        this.add(wireFrameCheckBox, constraints);
    }

    private void setupThreadingBox(Object constraints){
        JCheckBox threadingCheckBox = new JCheckBox("Multi threading");
        ChangeListener listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                settings.setThreaded(threadingCheckBox.isSelected());
            }
        };
        listener.stateChanged(null);
        threadingCheckBox.addChangeListener(listener);
        this.add(threadingCheckBox, constraints);
    }

    private void startButton(Object constraints){

        JButton startButton = new JButton("Start game");

        ActionListener listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                game = new Game(settings);
                Thread gameThread = new Thread(game);
                gameThread.start();
            }
        };
        startButton.addActionListener(listener);
        this.add(startButton, constraints);
    }

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("lwjgl/native").getAbsolutePath());
        MenuFrame m = new MenuFrame();
    }
}
