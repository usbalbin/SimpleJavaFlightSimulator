package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.ida.albhe417.tddd78.math.Vector4;

public class VertexPositionColor implements Vertex
{

    //TODO: get rid of me, i'm awful!!
    public static final float HEIGHT_FACTOR = 10;
    public static final int FLOATS_PER_VECTOR = 3;

    public static final int FLOAT_COUNT = 6;
    public float[] values;

    public VertexPositionColor(Vector3 position){
        Vector3 color = new Vector3(position.getY() / HEIGHT_FACTOR);
        setupValues(position, color);
    }

    public VertexPositionColor(Vector3 position, Vector3 color){
        setupValues(position, color);
    }

    public int getFloatCount(){
        return FLOAT_COUNT;
    }

    public float[] getFloats(){
        return values;
    }

    private void setupValues(Vector3 position, Vector3 color){
        values = new float[FLOAT_COUNT];
        System.arraycopy(position.values, 0, this.values, 0, FLOATS_PER_VECTOR);
        System.arraycopy(color.values, 0, this.values, FLOATS_PER_VECTOR, FLOATS_PER_VECTOR);
    }
}