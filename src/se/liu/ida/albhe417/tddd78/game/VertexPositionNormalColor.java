package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;
import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

public class VertexPositionNormalColor implements Vertex
{
    private static final int POSITION_INDEX = 2;
    private static final int NORMAL_INDEX = 1;
    private static final int COLOR_INDEX = 0;

    public static final int FLOAT_COUNT = 3 + 3 + 3;
    public Vector3 position;
    public Vector3 normal;
    public Vector3 color;

    public VertexPositionNormalColor(){}

    public VertexPositionNormalColor(Vector3 position){
        this.position = position;
        this.normal = new Vector3();
    }

    public VertexPositionNormalColor(Vector3 position, Vector3 normal, Vector3 color){
	    this.position = position;
        this.normal = normal;
	    this.color = color;
    }

    @Override
    public int getFloatCount(){
        return FLOAT_COUNT;
    }

    @Override
    public float[] getFloats(){
        final int floatsPerVector = 3;
        float[] result = new float[FLOAT_COUNT];
        /*System.arraycopy(position.values, 0, result, 0, floatsPerVector);

        System.arraycopy(color.values, 0, result, floatsPerVector * 2, floatsPerVector * 2);
        */
        return result;
    }

    @Override
    public void enableVertexAttribs(){
        glEnableVertexAttribArray(POSITION_INDEX);
        glEnableVertexAttribArray(NORMAL_INDEX);
        glEnableVertexAttribArray(COLOR_INDEX);
    }

    @Override
    public void setupVertexAttribs() {
        //Show gpu how to interprete the vertex data
        int floatsPerVector = 3;
        glVertexAttribPointer(POSITION_INDEX, floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, floatsPerVector * Float.BYTES * 0);
        glVertexAttribPointer(NORMAL_INDEX, floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, floatsPerVector * Float.BYTES * 1);
        glVertexAttribPointer(COLOR_INDEX, floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, floatsPerVector * Float.BYTES * 2);
    }
}