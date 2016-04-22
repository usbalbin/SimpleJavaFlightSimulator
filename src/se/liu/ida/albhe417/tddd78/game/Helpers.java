package se.liu.ida.albhe417.tddd78.game;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Project TDDD78
 *
 * File created by Albin on 09/03/2016.
 */
public class Helpers {
        /**
     * Returns symmetrical 2d array of float-values.
     * Observe that real values will have to be read using value | 0x00FFFF to compensate for overflow caused by java having signed shorts.
     * @param fileName
     * @return
     */
    public static float[] shortImageToFloats(String fileName)throws IOException{
        BufferedImage heightMapBuff;
        InputStream fileStream;

        fileStream = Helpers.class.getResourceAsStream(fileName);

        if(fileStream == null)
            throw new FileNotFoundException("Failed to load image");

        try {
            heightMapBuff = ImageIO.read(fileStream);
        }catch (IOException e){
            throw e;
        }

        int width = heightMapBuff.getWidth();
        int height = heightMapBuff.getHeight();

        int size = Math.min(width, height);

        short[] pixels = ((DataBufferUShort)heightMapBuff.getRaster().getDataBuffer()).getData();//Access buffers pixel array


        float[] colors = new float[size * size];

        final int componentsPerPixel = pixels.length / (width * height);
        int offset = componentsPerPixel == 4 ? 1 : 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                float color = (pixels[row * width * componentsPerPixel + column * componentsPerPixel + offset] & 0x00FFFF);
                colors[row * size + column] = color;
            }
        }


        return colors;
    }

    public static void createNormalSphere(List<VertexPositionColorNormal> vertices, List<Integer> indices, float radius, Vector3 color, int qualityFactor){
        if(radius <= 0)
            throw new IllegalArgumentException("A sphere should have a radius > 0");

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
