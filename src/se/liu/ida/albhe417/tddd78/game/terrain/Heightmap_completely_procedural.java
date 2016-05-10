package se.liu.ida.albhe417.tddd78.game.terrain;

import com.bulletphysics.collision.shapes.Heightfield;
import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.itn.stegu.simplex_noise.SimplexNoise;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;

/**
 * Heightmap holds heights for every point in a terrain.
 * Thus it is useful for holding data for creating terrain mesh as well as terrain collision shape.
 */
class Heightmap_completely_procedural implements Heightfield{
    public final float heightFactor = 0.05f;

    public float maxHeight;
    public float minHeight;

    private float yOffset;
    private final int levelsOfNoise;


    Heightmap_completely_procedural(int levelsOfNoise){
        this.levelsOfNoise = levelsOfNoise;

        this.maxHeight = 1 << (levelsOfNoise + 1) - 1;
        this.maxHeight *= heightFactor;
        this.minHeight = -maxHeight;
        //            Terrain top-down radius     +    base height
        this.yOffset = (maxHeight - minHeight) / 2.0f + minHeight;
    }

    public void getHeight(Vector3 position){
        int x = (int)position.getX();
        int z = (int)position.getZ();

        position.setY(getHeight(x, z));
    }

    public float getHeight(float x, float y){
        double height = 0;

        int maxAmplitude = 1 << levelsOfNoise;

        for (int amplitude = 1; amplitude <= maxAmplitude; amplitude <<= 1) {
            double period = 1.0 / amplitude;
            height += amplitude * SimplexNoise.noise(x * period, y * period);
        }

        return (float)height * heightFactor;
    }

    @Override
    public float getMaxHeight() {
        return maxHeight;
    }

    @Override
    public float getMinHeight() {
        return minHeight;
    }

    @Override
    public float getWidth() {
        return Float.MAX_VALUE;
    }

    @Override
    public float getLength() {
        return Float.MAX_VALUE;
    }

    public float getYOffset() {
        return yOffset;
    }

}
