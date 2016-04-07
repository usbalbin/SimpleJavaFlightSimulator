package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class VertexPositionColor implements Vertex
{
    public static int POSITION_INDEX;
    public static int COLOR_INDEX;

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
        glVertexAttribPointer(POSITION_INDEX, FLOATS_PER_VECTOR, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, FLOATS_PER_VECTOR * Float.BYTES * 0);
        glVertexAttribPointer(COLOR_INDEX, FLOATS_PER_VECTOR, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, FLOATS_PER_VECTOR * Float.BYTES * 1);
    }
}