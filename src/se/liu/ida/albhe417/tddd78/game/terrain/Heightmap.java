package se.liu.ida.albhe417.tddd78.game.terrain;

import com.bulletphysics.collision.shapes.Heightfield;

import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.itn.stegu.simplex_noise.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;

/**
 * Heightmap holds heights for every point in a terrain.
 * Thus it is useful for holding data for creating terrain mesh as well as terrain collision shape.
 */
class Heightmap{
    public final int size;
    public final float heightFactor;
    public float maxHeight;
    public float minHeight;

    private float[] heights;

    private float yOffset;


    Heightmap(int size){
        this.size = size;
        generateRandomFloats(size);
        this.heightFactor = 1;// / 10f;
        this.maxHeight *= heightFactor;
        this.minHeight *= heightFactor;
        //            Terrain top-down radius     +    base height
        this.yOffset = (maxHeight - minHeight) / 2.0f + minHeight;
    }

    Heightmap(BufferedImage heightMapBuff) {
        int size = Math.min(heightMapBuff.getHeight(), heightMapBuff.getWidth());


        if (Integer.bitCount(size) > 1)
            this.size = Integer.highestOneBit(size) + 1;
        else
            this.size = Integer.highestOneBit(size) / 2 + 1;

        int dataType = heightMapBuff.getRaster().getDataBuffer().getDataType();

        assert dataType < 2 : "Data should never be other than 0 or 1: True color or Deep color";

        switch (dataType){
            case 1://Deep color
                shortImageToFloats(heightMapBuff, this.size);
                break;
            default://True color
                byteImageToFloats(heightMapBuff, this.size);
        }

        this.heightFactor = 256.0f / maxHeight;
        this.maxHeight *= heightFactor;
        this.minHeight *= heightFactor;
        this.yOffset = (maxHeight - minHeight) / 2.0f + minHeight;
    }

    private void shortImageToFloats(BufferedImage heightMapBuff, int size){
        float maxHeight = Float.NEGATIVE_INFINITY;
        float minHeight = Float.POSITIVE_INFINITY;

        int width = heightMapBuff.getWidth();
        int height = heightMapBuff.getHeight();

        short[] pixels = ((DataBufferUShort)heightMapBuff.getRaster().getDataBuffer()).getData();//Access buffers pixel array


        float[] colors = new float[size * size];

        final int componentsPerPixel = pixels.length / (width * height);
        int offset = componentsPerPixel == 4 ? 1 : 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                final int unsignedShortToInt = 0x00FFFF;
                float color = (pixels[row * width * componentsPerPixel + column * componentsPerPixel + offset] & unsignedShortToInt);

                if(maxHeight < color)
                    maxHeight = color;
                else if(minHeight > color)
                    minHeight = color;

                colors[row * size + column] = color;
            }
        }

        heights = colors;
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
        this.yOffset = (maxHeight - minHeight) / 2.0f + minHeight;
    }

    private void byteImageToFloats(BufferedImage heightMapBuff, int size){
        float maxHeight = Float.NEGATIVE_INFINITY;
        float minHeight = Float.POSITIVE_INFINITY;

        int width = heightMapBuff.getWidth();
        int height = heightMapBuff.getHeight();

        byte[] pixels = ((DataBufferByte)heightMapBuff.getRaster().getDataBuffer()).getData();//Access buffers pixel array

        float[] colors = new float[size * size];

        final int componentsPerPixel = pixels.length / (width * height);
        int offset = componentsPerPixel == 4 ? 1 : 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                final int unsignedByteToInt = 0x00FF;
                float color = (pixels[row * width * componentsPerPixel + column * componentsPerPixel + offset] & unsignedByteToInt);

                if(maxHeight < color)
                    maxHeight = color;
                else if(minHeight > color)
                    minHeight = color;

                colors[row * size + column] = color;
            }
        }

        heights = colors;
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
    }

    private void generateRandomFloats(int size){
        final double persistance = 0.05;
        final int maxAmplitude = 1 << 12;
        heights = new float[size * size];

        Thread[] threads = new Thread[4];
        for(int z = 0; z < size; z+=threads.length){
            ;

            for(int i = 0; i < threads.length && z + i < size; i++){
                threads[i] = new Thread(new HeightRowGenerator(z + i, maxAmplitude, persistance));
                threads[i].start();
            }

            for (Thread t : threads) {
                try {
                    t.join();
                }catch (InterruptedException e) {

                }
            }
        }
    }


    public void getHeight(Vector3 position){
        int x = size / 2 + (int)position.getX();
        int z = size / 2 + (int)position.getZ();

        float y =  heights[z * size + x];
        position.setY(y * heightFactor - yOffset);
    }

    public void getHeight___(Vector3 position){
        int x = size / 2 + (int)position.getX();
        int z = size / 2 + (int)position.getZ();
        double y = 0;

        int maxAmplitude = 8192;
        double persistance = 0.05;

        for (int amplitude = 1; amplitude <= maxAmplitude; amplitude <<= 1) {
            double period = 1.0 / amplitude;
            y += persistance * amplitude * (float) SimplexNoise.noise(x * period, z * period);
        }
        position.setY(getHeight(x, z));
    }

    public float getHeight(int x, int y){
        double height = 0;

        int maxAmplitude = 8192;
        double persistance = 0.05;

        for (int amplitude = 1; amplitude <= maxAmplitude; amplitude <<= 1) {
            double period = 1.0 / amplitude;
            height += persistance * amplitude * (float) SimplexNoise.noise(x * period, y * period);
        }

        return (float)height * heightFactor - yOffset;
    }

    public float getYOffset() {
        return yOffset;
    }

    public float[] getHeights(){
        return heights;
    }

    class HeightRowGenerator implements Runnable {
        HeightRowGenerator(int row, int maxAmplitude, double persistance){
            this.row = row;
            this.maxAmplitude = maxAmplitude;
            this.persistance = persistance;
        }

        int row;
        int maxAmplitude;
        double persistance;

        @Override
        public void run() {
            for (int x = 0; x < size; x++) {
                double height = 0;

                for (int amplitude = 1; amplitude <= maxAmplitude; amplitude <<= 1) {
                    double period = 1.0 / amplitude;
                    height += persistance * amplitude * (float) SimplexNoise.noise(x * period, row * period);
                }
                heights[row * size + x] = (float) height;
            }
        }
    }
}
