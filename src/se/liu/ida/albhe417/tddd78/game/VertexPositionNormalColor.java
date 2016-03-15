package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

public class VertexPositionNormalColor implements Vertex
{
    public static final int FLOAT_COUNT = 3 + 3 + 3;
    public Vector3 position;
    public Vector3 normal;
    public Vector3 color;

    public VertexPositionNormalColor(Vector3 position, Vector3 normal, Vector3 color){
	    this.position = position;
        this.normal = normal;
	    this.color = color;
    }

    public int getFloatCount(){
        return FLOAT_COUNT;
    }

    public float[] getFloats(){
        final int floatsPerVector = 3;
        float[] result = new float[FLOAT_COUNT];
        /*System.arraycopy(position.values, 0, result, 0, floatsPerVector);

        System.arraycopy(color.values, 0, result, floatsPerVector * 2, floatsPerVector * 2);
        */
        return result;
    }
}