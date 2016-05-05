package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.game.graphics.VertexPositionColorNormal;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * Helpers contains a handful of handy functions that might be useful at multiple places.
 */
public final class Helpers {

    private Helpers() {}

    public static void createNormalSphere(Collection<VertexPositionColorNormal> vertices, Collection<Integer> indices, float radius, Vector3 color, int qualityFactor){
        if(radius <= 0) {
            String msg = "A sphere should have a radius > 0";
            Logger.getGlobal().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        final float twoPi = 2.0f * (float)Math.PI;
        final float halfPi = (float)Math.PI / 2.0f;

        float step = (float)Math.PI / qualityFactor;
        for(float pitch = 0; pitch < twoPi; pitch += step){
            for(float yaw = -halfPi; yaw <= halfPi; yaw += step){

                Vector3 position = new Vector3(0, radius, 0).getRotatedAroundX(pitch).getRotatedAroundY(yaw);
                vertices.add(new VertexPositionColorNormal(position, color, position));

            }

        }

        setupSphereIndices(qualityFactor, indices, vertices.size());
    }

    private static void setupSphereIndices(int qualityFactor, Collection<Integer> indices, int vertexCount){
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

    /**
     * Map value from input range to output range
     *
     * Formula from https://www.arduino.cc/en/Reference/Map
     *
     * @param value Value to map
     * @param inMin Start of input range
     * @param inMax End of input range
     * @param outMin Start of output range
     * @param outMax End of output range
     * @return mapped value
     */
    public static float map(float value, float inMin, float inMax, float outMin, float outMax){
        final float deltaIn = inMax - inMin;
        final float deltaOut = outMax - outMin;

        final float scale = deltaOut / deltaIn;

        final float displacedValue = value - inMin;

        return displacedValue * scale + outMin;
    }

    public static float linearInterpolate(float percentOfSecond, float first, float second){
        return first * percentOfSecond + second * (1- percentOfSecond);
    }

    public static float cosineInterpolate(float percentOfSecond, float first, float second){
        percentOfSecond = (float)(1 - Math.cos(percentOfSecond* 3.14159265))/2;
        return first * percentOfSecond + second * (1 - percentOfSecond);
    }

}
