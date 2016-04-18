package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Project TDDD78
 *
 * File created by Albin on 09/03/2016.
 */
public class Helpers {
    public static Vector3[][] imageToColors(String fileName){
        BufferedImage heightMapBuff;
        InputStream fileStream = Helpers.class.getResourceAsStream(fileName);

        if(fileStream == null)
            throw new RuntimeException("Failed to load " + fileName);

        try{
            heightMapBuff = ImageIO.read(fileStream);
        }catch (IOException e){
            throw new RuntimeException("Failed to load " + fileName);
        }

        int width = heightMapBuff.getWidth();
        int height = heightMapBuff.getHeight();

        byte[] pixels = ((DataBufferByte)heightMapBuff.getRaster().getDataBuffer()).getData();//Access buffers pixel array


        Vector3[][] colors = new Vector3[height][width];

        final int componentsPerPixel = heightMapBuff.getColorModel().getNumColorComponents();

        int j = 0;
        switch (componentsPerPixel) {

            case 1:
                for (int row = 0; row < height; row++) {
                    for (int column = 0; column < width; column++) {
                        float color = byteToFloatColor(pixels[j]);
                        colors[row][column] = new Vector3(
                                color,
                                color,
                                color
                        );
                        j += componentsPerPixel;
                    }
                }
                break;
            case 3:
            case 4://TODO make sure alpha behaves as expected on 4-chanel images
                for (int row = 0; row < height; row++) {
                    for (int column = 0; column < width; column++) {
                        colors[row][column] = new Vector3(
                                byteToFloatColor(pixels[j]),
                                byteToFloatColor(pixels[j + 1]),
                                byteToFloatColor(pixels[j + 2])
                        );
                        j += 3;
                    }
                }
                break;
            default:
                throw new RuntimeException("Invalid image " + fileName);
        }

        return colors;
    }

    /**
     * Returns symmetrical 2d array of byte-values representing height values in heightmap.
     * Observe that real values will have to be read using value | 0x00FF to compensate for overflow caused by java having signed bytes.
     * @param fileName
     * @return
     */
    public static byte[] imageToHeights(String fileName){
        BufferedImage heightMapBuff;
        InputStream fileStream = Helpers.class.getResourceAsStream(fileName);

        if(fileStream == null)
            throw new RuntimeException("Failed to load " + fileName);

        try{
            heightMapBuff = ImageIO.read(fileStream);
        }catch (IOException e){
            throw new RuntimeException("Failed to load " + fileName);
        }

        int width = heightMapBuff.getWidth();
        int height = heightMapBuff.getHeight();

        int size = Math.min(Math.min(width, height), 8192);

        byte[] pixels = ((DataBufferByte)heightMapBuff.getRaster().getDataBuffer()).getData();//Access buffers pixel array


        byte[] colors = new byte[size * size];

        final int componentsPerPixel = heightMapBuff.getColorModel().getNumColorComponents();

        //int j = 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                byte color = pixels[row * width * componentsPerPixel + column * componentsPerPixel];
                colors[row * size + column] = color;
                //j += componentsPerPixel;
            }
        }


        return colors;
    }

    public static float[] imageToFloatHeights(String fileName){
        BufferedImage heightMapBuff;
        InputStream fileStream = Helpers.class.getResourceAsStream(fileName);

        if(fileStream == null)
            throw new RuntimeException("Failed to load " + fileName);

        try{
            heightMapBuff = ImageIO.read(fileStream);
        }catch (IOException e){
            throw new RuntimeException("Failed to load " + fileName);
        }

        int width = heightMapBuff.getWidth();
        int height = heightMapBuff.getHeight();

        int size = Math.min(Math.min(width, height), 8193);

        byte[] pixels = ((DataBufferByte)heightMapBuff.getRaster().getDataBuffer()).getData();//Access buffers pixel array


        float[] colors = new float[size * size];

        final int componentsPerPixel = pixels.length / (width * height);//heightMapBuff.getColorModel().getNumColorComponents();
        int offset = componentsPerPixel == 4 ? 1 : 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                float color = (pixels[row * width * componentsPerPixel + column * componentsPerPixel + offset] & 0x00FF);
                colors[row * size + column] = color;
            }
        }


        return colors;
    }

    public static float[] shortImageToFloatHeights(String fileName){
        BufferedImage heightMapBuff;
        InputStream fileStream = Helpers.class.getResourceAsStream(fileName);

        if(fileStream == null)
            throw new RuntimeException("Failed to load " + fileName);

        try{
            heightMapBuff = ImageIO.read(fileStream);
        }catch (IOException e){
            throw new RuntimeException("Failed to load " + fileName);
        }

        int width = heightMapBuff.getWidth();
        int height = heightMapBuff.getHeight();

        int size = Math.min(Math.min(width, height), 8193);

        short[] pixels = ((DataBufferUShort)heightMapBuff.getRaster().getDataBuffer()).getData();//Access buffers pixel array


        float[] colors = new float[size * size];

        final int componentsPerPixel = pixels.length / (width * height);//heightMapBuff.getColorModel().getNumColorComponents();
        int offset = componentsPerPixel == 4 ? 1 : 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                float color = (pixels[row * width * componentsPerPixel + column * componentsPerPixel + offset] & 0x00FFFF);
                colors[row * size + column] = color;
            }
        }


        return colors;
    }

    private static float byteToFloatColor(short color){
        final short toUnsignedByte = 0x00FF;
        final float unsignedByteMaxVal = 256.0f;

        //Interpret color as unsigned byte(colors are represented by unsigned byte)
        return (color & toUnsignedByte ) / unsignedByteMaxVal;
    }

    public static void createNormalSphere(List<VertexPositionColorNormal> vertices, List<Integer> indices, float radius, Vector3 color, int qualityFactor){
        float step = (float)Math.PI / qualityFactor;
        for(float pitch = 0; pitch < 2.0f * (float)Math.PI; pitch += step){
            for(float yaw = -(float)Math.PI / 2.0f; yaw <= (float)Math.PI / 2.0f; yaw += step){

                Vector3 position = new Vector3(0, radius, 0).getRotatedAroundX(pitch).getRotatedAroundY(yaw);
                vertices.add(new VertexPositionColorNormal(position, color, position));

            }

        }

        setupSphereIndices(qualityFactor, indices, vertices.size());
    }

    private static void setupSphereIndices(int qualityFactor, List<Integer> indices, int vertexCount){
        for(int i = 0; i < vertexCount; i++){
            indices.add(i                                                       );
            indices.add((i - 1 - qualityFactor + vertexCount) % vertexCount     );
            indices.add((i - 1                 + vertexCount) % vertexCount     );

            indices.add(i                                                       );
            indices.add((i -     qualityFactor + vertexCount) % vertexCount     );
            indices.add((i - 1 - qualityFactor + vertexCount) % vertexCount     );
        }
    }

    public static boolean fEquals(float left, float right){
        final float epsilon = 0.1f;
        return Math.abs(left - right) < epsilon;
    }
}
