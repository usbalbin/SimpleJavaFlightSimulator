package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;

/**
 * Project TDDD78
 * <p>
 * File created by Albin on 26/04/2016.
 */
class Heightmap {
    public final int SIZE;
    public final float HEIGHT_FACTOR;
    public float MAX_HEIGHT;
    public float MIN_HEIGHT;

    private float[] heights;



    public Heightmap(BufferedImage heightMapBuff) {
        int size = Math.min(heightMapBuff.getHeight(), heightMapBuff.getWidth());


        if (Integer.bitCount(size) > 1)
            SIZE = Integer.highestOneBit(size) + 1;
        else
            SIZE = Integer.highestOneBit(size) / 2 + 1;


        switch (heightMapBuff.getRaster().getDataBuffer().getDataType()){
            case 1:
                shortImageToFloats(heightMapBuff, SIZE);
                break;
            default:
                byteImageToFloats(heightMapBuff, SIZE);
        }

        HEIGHT_FACTOR = 256f / MAX_HEIGHT;
        MAX_HEIGHT *= HEIGHT_FACTOR;
        MIN_HEIGHT *= HEIGHT_FACTOR;
    }

    /**
     * Returns symmetrical 2d array of float-values.
     * Observe that real values will have to be read using value | 0x00FFFF to compensate for overflow caused by java having signed shorts.
     * @param fileName
     * @return
     */
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
                float color = (pixels[row * width * componentsPerPixel + column * componentsPerPixel + offset] & 0x00FFFF);

                if(maxHeight < color)
                    maxHeight = color;
                else if(minHeight > color)
                    minHeight = color;

                colors[row * size + column] = color;
            }
        }

        heights = colors;
        MAX_HEIGHT = maxHeight;
        MIN_HEIGHT = minHeight;
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
                float color = (pixels[row * width * componentsPerPixel + column * componentsPerPixel + offset] & 0x00FF);

                if(maxHeight < color)
                    maxHeight = color;
                else if(minHeight > color)
                    minHeight = color;

                colors[row * size + column] = color;
            }
        }

        heights = colors;
        MAX_HEIGHT = maxHeight;
        MIN_HEIGHT = minHeight;
    }

    public void getHeight(Vector3 position){
        int x = SIZE / 2 + (int)position.getX();
        int z = SIZE / 2 + (int)position.getZ();
        float y =  heights[z * SIZE + x];
        position.setY(-MAX_HEIGHT / 2.0f + y * HEIGHT_FACTOR);
    }

    public float[] getHeights(){
        return heights;
    }
}
