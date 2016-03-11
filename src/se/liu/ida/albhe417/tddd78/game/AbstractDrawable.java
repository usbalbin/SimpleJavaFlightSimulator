package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;

public abstract class AbstractDrawable
{
    //protected Vector3 position;
    //protected float yaw, pitch, roll;
    protected Matrix4x4 modelMatrix;

    protected ArrayList<AbstractDrawablePart> parts;

    public AbstractDrawable(Vector3 position, float yaw){
    	/*this.position = position;
        this.yaw = 0;
        this.pitch = 0;
        this.roll = 0;*/
        modelMatrix = new Matrix4x4();
        modelMatrix = modelMatrix.getTranslated(position);
        modelMatrix = modelMatrix.getRotated(yaw, 0, 0);
    }

    protected void setupParts(ArrayList<AbstractDrawablePart> parts){
    	this.parts = parts;
    }

    public void update(float deltaTime){

    }

    public void draw(Matrix4x4 cameraMatrix, int matrixId){

        Matrix4x4 modelViewProjectionMatrix = cameraMatrix.multiply(modelMatrix);


        for(AbstractDrawablePart part : parts){
            part.draw(modelViewProjectionMatrix, matrixId);
        }
    }
}
