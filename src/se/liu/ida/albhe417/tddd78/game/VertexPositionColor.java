package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.ida.albhe417.tddd78.math.Vector4;

public class VertexPositionColor extends AbstractVertex
{
    public static final int FLOAT_COUNT = 6;
    public Vector3 color;

    public VertexPositionColor(Vector3 position, Vector3 color){
	    super(position);
	    this.color = color;
    }
}