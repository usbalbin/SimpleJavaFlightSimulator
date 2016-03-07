package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;

public abstract class Drawable
{
    protected Vector3 position;
    protected Vector3 up;
    protected Vector3 forward;

    protected ArrayList<AbstractDrawablePart> parts;

    public Drawable(Vector3 position){
    	this.position = position;
        this.up = new Vector3(0, 1, 0);
        this.forward = new Vector3(0, 0, -1);
    }

    protected void setupParts(ArrayList<AbstractDrawablePart> parts){
    	this.parts = parts;
    }

    public void draw(){
        //TODO: add these
        //Matrix4x4 modelMatrix = ....
        Matrix4x4 modelMatrix = new Matrix4x4();
        for(AbstractDrawablePart part : parts){
            part.draw(modelMatrix);
        }
    }
}
