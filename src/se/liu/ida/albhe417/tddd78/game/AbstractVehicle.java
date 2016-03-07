package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

public abstract class AbstractVehicle extends Drawable
{
    public AbstractVehicle(Vector3 position){
	super(position);
    }

    abstract public void update();
}
