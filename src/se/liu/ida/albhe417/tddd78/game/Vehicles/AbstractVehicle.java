package se.liu.ida.albhe417.tddd78.game.Vehicles;

import se.liu.ida.albhe417.tddd78.game.AbstractDrawable;
import se.liu.ida.albhe417.tddd78.game.Terrain;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.ida.albhe417.tddd78.math.Vector4;

public abstract class AbstractVehicle extends AbstractDrawable
{
    protected Terrain terrain;
    protected float MASS;
    protected final float THRUST_FACTOR;
    protected float throttle = 0;
    protected Vector3 velocity;
    protected final static Vector3 GRAVITY = new Vector3(0, -9.82f, 0);
    protected Vector3 cameraPosition;

    public AbstractVehicle(Vector3 position, float yaw, float mass, float thrustFactor, Terrain terrain){
	    super(position, yaw);
        this.terrain = terrain;
        this.MASS = mass;
        this.THRUST_FACTOR = thrustFactor;
        velocity = new Vector3();
    }

    abstract public void handleInput(float deltaTime);

    abstract public void update(float deltaTime);

    public Matrix4x4 getViewMatrix(){
        cameraPosition = modelMatrix.getInverse().multiply(new Vector4(0, 5f, 15f, 0)).toVector3();
        cameraPosition = cameraPosition.add(modelMatrix.getPosition());

        Vector3 cameraTarget = modelMatrix.getPosition();

        Vector3 cameraUp = modelMatrix.getInverse().multiply(Vector4.DIR_UP).toVector3();


        Matrix4x4 viewMatrix = Matrix4x4.createViewMatrix(cameraPosition, cameraTarget, cameraUp);
        return viewMatrix;
    }

    protected void changeThrottle(float deltaThrottle){
        final float MAX_THROTTLE = 1.0f;
        final float MIN_THROTTLE = -0.25f;
        final float THROTTLE_SETTLE = 0.99f;

        throttle += deltaThrottle;
        throttle = Math.max(MIN_THROTTLE, Math.min(throttle, MAX_THROTTLE));
        throttle *= THROTTLE_SETTLE;
    }

    public Vector3 getCameraPosition(){
        return cameraPosition;
    }
}
