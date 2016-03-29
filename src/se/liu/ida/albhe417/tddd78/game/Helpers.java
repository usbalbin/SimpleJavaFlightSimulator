package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Albin on 09/03/2016.
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


        Vector3[][] colors = new Vector3[height][width];;

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
            case 4://TODO kolla att alpha sköter sig på 4-kanaliga bilder
                for (int row = 0; row < height; row++) {
                    for (int column = 0; column < width; column++) {
                        colors[row][column] = new Vector3(
                                byteToFloatColor(pixels[j + 0]),
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
     * Returns symetrical 2d array of byte-values representing heightvalues in heightmap.
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

        int size = Math.min(Math.min(width, height), 8192);

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

    public static float byteToFloatColor(short color){
        final short toUnsigedByte = 0x00FF;
        final float unsignedByteMaxVal = 256f;

        //Interprete color as unsigned byte(colors are represented by unsigned byte)
        return (color & toUnsigedByte ) / unsignedByteMaxVal;
    }

    //TODO use fixed arrays
    public void createSphere(List<VertexPositionColor> vertices, List<Integer> indices, float radius, Vector3 color, int qualityFactor){
        float step = 2f * (float)Math.PI / qualityFactor;

        for(float alpha = 0; alpha < 2f * (float)Math.PI; alpha += step){
            float xFactor = (float)Math.cos(alpha) * radius;
            float zFactorAlpha = (float)Math.sin(alpha) * radius;

            for(float beta = -(float)Math.PI / 2f; beta < (float)Math.PI / 2f; beta += step){
                float yFactor = (float)Math.sin(beta) * radius;
                float zFactorBeta = (float)Math.cos(beta);

                float x = xFactor;
                float y = yFactor;
                float z = zFactorAlpha * zFactorBeta;

                Vector3 position = new Vector3(x, y, z);
                vertices.add(new VertexPositionColor(position, color));

            }

        }

        setupSphereIndices(qualityFactor, indices, vertices.size());
    }

    public void createNormalSphere(List<VertexPositionColorNormal> vertices, List<Integer> indices, float radius, Vector3 color, int qualityFactor){
        float step = (float)Math.PI / qualityFactor;

        for(float alpha = 0; alpha < 2f * (float)Math.PI; alpha += step){
            float xFactor = (float)Math.cos(alpha) * radius;
            float zFactorAlpha = (float)Math.sin(alpha) * radius;

            for(float beta = -(float)Math.PI / 2f; beta < (float)Math.PI / 2f; beta += step){
                float yFactor = (float)Math.sin(beta) * radius;
                float zFactorBeta = (float)Math.cos(beta);

                float x = xFactor;
                float y = yFactor;
                float z = zFactorAlpha * zFactorBeta;

                Vector3 position = new Vector3(x, y, z);
                vertices.add(new VertexPositionColorNormal(position, color, position));

            }

        }

        setupSphereIndices(qualityFactor, indices, vertices.size());
    }

    private void setupSphereIndices(int qualityFactor, List<Integer> indices, int vertexCount){
        for(int i = 0; i < vertexCount; i++){
            indices.add(i                                       );
            indices.add((i - 1 - qualityFactor) % vertexCount   );
            indices.add((i - 1)                 % vertexCount   );

            indices.add(i                                       );
            indices.add((i - 0 - qualityFactor) % vertexCount   );
            indices.add((i - 1 - qualityFactor) % vertexCount   );
        }
    }
}
