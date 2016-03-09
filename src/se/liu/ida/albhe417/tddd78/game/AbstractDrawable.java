package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;

public abstract class AbstractDrawable
{
    protected Vector3 position;
    protected float yaw, pitch, roll;

    protected ArrayList<AbstractDrawablePart> parts;

    public AbstractDrawable(Vector3 position){
    	this.position = position;
        this.yaw = 0;
        this.pitch = 0;
        this.roll = 0;
    }

    protected void setupParts(ArrayList<AbstractDrawablePart> parts){
    	this.parts = parts;
    }

    public void draw(Matrix4x4 cameraMatrix){
        Matrix4x4 modelMatrix = new Matrix4x4();


        for(AbstractDrawablePart part : parts){
            part.draw(modelMatrix);
        }
    }
}
