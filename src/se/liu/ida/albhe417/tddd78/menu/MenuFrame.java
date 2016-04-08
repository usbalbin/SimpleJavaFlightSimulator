package se.liu.ida.albhe417.tddd78.menu;

import net.miginfocom.swing.MigLayout;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.QuadTree;

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
    private Game game;

    private int maxDrawDistance;
    private int qualityFactor = 350;
    private int tickPerFrame = 10;
    private float tickSize = 1/60.0f/tickPerFrame;



    public MenuFrame(){
        super("Title");
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
                maxDrawDistance = maxDrawDistanceSlider.getValue();
                label.setText("Draw distance: " + maxDrawDistance);
            }
        };
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
                qualityFactor = qualityFactorSlider.getValue();
                label.setText("Quality factor: " + qualityFactor);
            }
        };
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
                tickPerFrame = ticksPerFrameSlider.getValue();
                tickSize = 1/60.0f/tickPerFrame;
                label.setText("Ticks / frame: " + tickPerFrame);
            }
        };
        ticksPerFrameSlider.addChangeListener(sliderListener);
        this.add(ticksPerFrameSlider, constraints);
    }

    private void setupWireFrameBox(Object constraints){
        JCheckBox wireFrameCheckBox = new JCheckBox("Enable wire frame");
        this.add(wireFrameCheckBox, constraints);
    }

    private void setupThreadingBox(Object constraints){
        JCheckBox threadingCheckBox = new JCheckBox("Threading frame");
        this.add(threadingCheckBox, constraints);
    }

    private void startButton(Object constraints){

        JButton startButton = new JButton("Start game");

        ActionListener listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                game = new Game(qualityFactor);
                game.run();
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
