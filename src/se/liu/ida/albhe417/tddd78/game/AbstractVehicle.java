package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

public abstract class AbstractVehicle extends AbstractDrawable
{
    public AbstractVehicle(Vector3 position){
	super(position);
    }

    abstract public void handleInput();

    abstract public void update();

    public Matrix4x4 getViewMatrix(){
        Vector3 cameraPosition = new Vector3(0, 1, 1.5f).getRotated(yaw, pitch, roll);
        cameraPosition = cameraPosition.add(position);

        Vector3 cameraTarget = position;

        Vector3 cameraUp = Vector3.UP.getRotated(yaw, pitch, roll);

        Matrix4x4 viewMatrix = Matrix4x4.createViewMatrix(cameraPosition, cameraTarget, cameraUp);
        return viewMatrix;
    }
}
