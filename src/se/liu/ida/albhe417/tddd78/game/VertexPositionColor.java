package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.ida.albhe417.tddd78.math.Vector4;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class VertexPositionColor implements Vertex
{
    private static final int POSITION_INDEX = 1;
    private static final int COLOR_INDEX = 0;

    //TODO: get rid of me, i'm awful!!
    public static final float HEIGHT_FACTOR = 10;
    public static final int FLOATS_PER_VECTOR = 3;

    public static final int FLOAT_COUNT = 6;
    public float[] values;

    public VertexPositionColor(){}

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

    @Override
    public void enableVertexAttribs() {
        glEnableVertexAttribArray(POSITION_INDEX);
        glEnableVertexAttribArray(COLOR_INDEX);
    }

    @Override
    public void setupVertexAttribs() {
        int floatsPerVector = 3;
        glVertexAttribPointer(POSITION_INDEX, floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, floatsPerVector * Float.BYTES * 0);
        glVertexAttribPointer(COLOR_INDEX, floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, floatsPerVector * Float.BYTES * 1);
    }
}