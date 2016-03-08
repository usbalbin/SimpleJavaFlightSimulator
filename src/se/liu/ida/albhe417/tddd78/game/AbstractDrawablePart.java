package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Matrix4x4;

public abstract class AbstractDrawablePart
{
    /*
     *  TODO: add these
     *  partMatrix
     */
    protected Matrix4x4 partMatrix;
    protected int indexCount;
    protected int vertexArray;
    protected int vertexBuffer;
    protected int indexBuffer;
    protected int shaderProgram;

    abstract public void draw(Matrix4x4 modelMatrix);

}
