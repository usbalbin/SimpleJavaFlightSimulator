package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;
import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

/**
 * Project TDDD78
 *
 * File created by Albin.
 */
public class VertexPositionColorNormal implements Vertex
{
    private static int POSITION_INDEX;
    private static int COLOR_INDEX;
    private static int NORMAL_INDEX;

    private static final int FLOATS_PER_VECTOR = 3;
    private static final int FLOAT_COUNT = FLOATS_PER_VECTOR * 3;
    public Vector3 position;
    private Vector3 color;
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

    public static void init(int shaderProgram){
        int positionIndex = glGetAttribLocation(shaderProgram, "position");
        int colorIndex = glGetAttribLocation(shaderProgram, "color");
        int normalIndex = glGetAttribLocation(shaderProgram, "normal");

        VertexPositionColorNormal.POSITION_INDEX 	= positionIndex;
        VertexPositionColorNormal.COLOR_INDEX		= colorIndex;
        VertexPositionColorNormal.NORMAL_INDEX		= normalIndex;
    }

    @Override
    public int getFloatCount(){
        return FLOAT_COUNT;
    }

    @Override
    public float[] getFloats(){
        float[] result = new float[FLOAT_COUNT];
        System.arraycopy(position.values,   0, result, 0, FLOATS_PER_VECTOR);
        System.arraycopy(color.values,      0, result, FLOATS_PER_VECTOR, FLOATS_PER_VECTOR);
        System.arraycopy(normal.values,     0, result, FLOATS_PER_VECTOR * 2, FLOATS_PER_VECTOR);


        return result;
    }

    @Override
    public void enableVertexAttributes(){
        glEnableVertexAttribArray(POSITION_INDEX);
        glEnableVertexAttribArray(COLOR_INDEX);
        glEnableVertexAttribArray(NORMAL_INDEX);
    }

    @Override
    public void setupVertexAttributes() {
        //Show gpu how to interpret the vertex data
        int floatsPerVector = 3;
        glVertexAttribPointer(POSITION_INDEX,   floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, 0L                                     );
        glVertexAttribPointer(COLOR_INDEX,      floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, (long)floatsPerVector * Float.BYTES    );
        glVertexAttribPointer(NORMAL_INDEX,     floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, (long)floatsPerVector * Float.BYTES * 2);
    }

    @Override
    public boolean equals(Object obj) {
        if(this.getClass() != obj.getClass())
            return false;
        VertexPositionColorNormal other = (VertexPositionColorNormal)obj;
        return this.position.equals(other.position);
    }

}