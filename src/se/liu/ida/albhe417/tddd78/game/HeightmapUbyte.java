package se.liu.ida.albhe417.tddd78.game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Albin_Hedman on 2016-03-17.
 */
public class HeightmapUbyte implements Heightmap{
    private final byte[][] values;
    private int height;
    private int width;
    private final float Y_SCALE;

    public HeightmapUbyte(String fileName, float yScale){
        final boolean even = false, square = false, pow2plus1 = false;
        this.values = imageToHeights(fileName, even, square, pow2plus1);
        this.Y_SCALE = yScale;
    }

    public HeightmapUbyte(byte[][] values, float yScale){
        this.values = values;
        this.Y_SCALE = yScale;
    }

    public static Heightmap createWithSidePowerOf2Plus1(String fileName, float yScale){
        final boolean even = false, square = true, pow2plus1 = true;
        byte[][] values = imageToHeights(fileName, even, square, pow2plus1);
        return new HeightmapUbyte(values, yScale);
    }

    @Override
    public float getValueS(int x, int z) {
        return values[z][x] & 0x00FF;   //Undo overflow caused by image being unsigned byte while java uses signed
    }

    @Override
    public float getValueF(int x, int z) {
        return getValueS(x, z) * Y_SCALE;
    }

    public float getHeight(float x, float z){
        x = Math.max(0, x);
        x = Math.min(x, width - 2);

        z = Math.max(0, z);
        z = Math.min(z, height - 2);

        //Get heights from closest vertices
        float leftFront =  getValueF((int)x + 0, (int)z + 0);
        float rightFront = getValueF((int)x + 1, (int)z + 0);

        float leftBack =   getValueF((int)x + 0, (int)z + 1);
        float rightBack =  getValueF((int)x + 1, (int)z + 1);

        float xRest = x % 1;
        float zRest = z % 1;

        //Interpolate heights depending on how close
        //TODO: fix math
        float height =
                leftBack * (1.0f - xRest)  * (1.0f - zRest) +
                rightBack * (xRest)        * (1.0f - zRest) +

                leftFront * (1.0f - xRest) * (zRest)        +
                rightFront * (xRest)       * (zRest);

        return height;
    }

    @Override
    public void increaseValueScaled(int x, int z, float value) {
        increaseValueUnscaled(x, z, (byte)(value * Y_SCALE));
    }


    public void increaseValueUnscaled(int x, int z, byte value) {
        values[z][x] = (byte)Math.min(value + getValueS(x, z), 0x00FF);
    }

    /**
     * Returns symetrical 2d array of byte-values representing heightvalues in heightmap.
     * Observe that real values will have to be read using value | 0x00FF to compensate for overflow caused by java having signed bytes.
     * @param fileName
     * @return
     */
    public static byte[][] imageToHeights(String fileName, boolean evenSize, boolean square, boolean powOf2Plus1){
        BufferedImage heightMapBuff;
        InputStream fileStream = Helpers.class.getResourceAsStream(fileName);

        if(fileStream == null)
            throw new RuntimeException("Failed to load " + fileName);

        try{
            heightMapBuff = ImageIO.read(fileStream);
        }catch (IOException e){
            throw new RuntimeException("Failed to load " + fileName);
        }

        int imageWidth = heightMapBuff.getWidth();
        int imageHeight = heightMapBuff.getHeight();

        int width = imageWidth;
        int height = imageHeight;

        if(evenSize && powOf2Plus1)
            throw new IllegalArgumentException("Seriosly! F in math?");

        if(square){
            width = height = Math.min(width, height);
        }
        if(evenSize) {
            width -= width % 2;
            height -= height % 2;
        }
        else if(powOf2Plus1){
            int size = Math.min(width, height);
            width = height = Integer.highestOneBit(size - 1) + 1;
        }

        byte[] pixels = ((DataBufferByte)heightMapBuff.getRaster().getDataBuffer()).getData();//Access buffers pixel array

        byte[][] values = new byte[height][width];
        final int componentsPerPixel = heightMapBuff.getColorModel().getNumColorComponents();

        //int j = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                byte color = pixels[row * imageWidth * componentsPerPixel + column * componentsPerPixel];
                values[row][column] = color;
            }
        }

        return values;
    }
}
