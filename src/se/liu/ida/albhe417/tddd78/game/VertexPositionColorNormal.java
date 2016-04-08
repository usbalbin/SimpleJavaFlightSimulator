package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;
import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

public class VertexPositionColorNormal implements Vertex
{
    public static int POSITION_INDEX;
    public static int COLOR_INDEX;
    public static int NORMAL_INDEX;

    public static final int FLOAT_COUNT = 3 + 3 + 3;
    public static final int FLOATS_PER_VECTOR = 3;
    public Vector3 position;
    public Vector3 color;
    public Vector3 normal;

    public VertexPositionColorNormal(){}

    public VertexPositionColorNormal(Vector3 position, Vector3 color){
        this.position = position;
        this.color = color;
        this.normal = new Vector3();
    }

    public VertexPositionColorNormal(Vector3 position, Vector3 color, Vector3 normal){
	    this.position = position;
	    this.color = color;
        this.normal = normal;
    }

    @Override
    public int getFloatCount(){
        return FLOAT_COUNT;
    }

    @Override
    public float[] getFloats(){
        float[] result = new float[FLOAT_COUNT];
        System.arraycopy(position.values,   0, result, FLOATS_PER_VECTOR * 0, FLOATS_PER_VECTOR);
        System.arraycopy(color.values,      0, result, FLOATS_PER_VECTOR * 1, FLOATS_PER_VECTOR);
        System.arraycopy(normal.values,     0, result, FLOATS_PER_VECTOR * 2, FLOATS_PER_VECTOR);


        return result;
    }

    @Override
    public void enableVertexAttribs(){
        glEnableVertexAttribArray(POSITION_INDEX);
        glEnableVertexAttribArray(COLOR_INDEX);
        glEnableVertexAttribArray(NORMAL_INDEX);
    }

    @Override
    public void setupVertexAttribs() {
        //Show gpu how to interprete the vertex data
        int floatsPerVector = 3;
        glVertexAttribPointer(POSITION_INDEX, floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, (long)floatsPerVector * Float.BYTES * 0);
        glVertexAttribPointer(COLOR_INDEX, floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, (long)floatsPerVector * Float.BYTES * 1);
        glVertexAttribPointer(NORMAL_INDEX, floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, (long)floatsPerVector * Float.BYTES * 2);
    }

    @Override
    public boolean equals(Object obj) {
        VertexPositionColorNormal other = (VertexPositionColorNormal)obj;
        return this.position.equals(other.position);
    }

}