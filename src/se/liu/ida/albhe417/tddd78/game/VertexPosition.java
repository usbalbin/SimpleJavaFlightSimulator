package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

public class VertexPosition implements Vertex
{
    public static final int FLOAT_COUNT = 3;
    public Vector3 position;

    //TODO convert to use Vector2
    public VertexPosition(Vector3 position){
	    this.position = position;
    }

    public int getFloatCount(){
        return FLOAT_COUNT;
    }

    public float[] getFloats(){
        return position.values;
    }
}