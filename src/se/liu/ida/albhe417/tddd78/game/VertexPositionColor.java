package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.ida.albhe417.tddd78.math.Vector4;

public class VertexPositionColor implements Vertex
{

    //TODO: get rid of me, i'm awful!!
    public static float HEIGHT_FACTOR = 10;

    public static final int FLOAT_COUNT = 6;
    public Vector3 position;
    public Vector3 color;

    public VertexPositionColor(Vector3 position){
        this.position = position;
        this.color = new Vector3(position.getY() / HEIGHT_FACTOR);
    }

    public VertexPositionColor(Vector3 position, Vector3 color){
	    this.position = position;
	    this.color = color;
    }

    public int getFloatCount(){
        return FLOAT_COUNT;
    }

    public float[] getFloats(){
        final int floatsPerVector = 3;
        float[] result = new float[FLOAT_COUNT];
        System.arraycopy(position.values, 0, result, 0, floatsPerVector);
        System.arraycopy(color.values, 0, result, floatsPerVector, floatsPerVector);
        return result;
    }
}