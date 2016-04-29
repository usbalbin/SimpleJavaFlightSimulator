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
    public final int size;
    public final float heightFactor;
    public float maxHeight;
    public float minHeight;

    private float[] heights;



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

        heightFactor = 256f / maxHeight;
        maxHeight *= heightFactor;
        minHeight *= heightFactor;
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

    public void getHeight(Vector3 position){
        final float yOffset = -maxHeight / 2.0f;
        int x = size / 2 + (int)position.getX();
        int z = size / 2 + (int)position.getZ();
        float y =  heights[z * size + x];
        position.setY(yOffset + y * heightFactor);
    }

    public float[] getHeights(){
        return heights;
    }
}
