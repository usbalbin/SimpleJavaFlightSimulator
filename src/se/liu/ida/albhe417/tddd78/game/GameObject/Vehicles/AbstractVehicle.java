package se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles;

import se.liu.ida.albhe417.tddd78.game.GameObject.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.GameObjectPart;
import se.liu.ida.albhe417.tddd78.game.Terrain;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.ida.albhe417.tddd78.math.Vector4;

public abstract class AbstractVehicle extends AbstractGameObject
{
    protected float MASS;
    protected final float THRUST_FACTOR;
    protected float throttle = 0;
    protected Vector3 velocity;
    protected final static Vector3 GRAVITY = new Vector3(0, -9.82f, 0);
    protected Vector3 cameraPosition;
    protected GameObjectPart partBody;


    public AbstractVehicle(Vector3 position, float yaw, float mass, float thrustFactor){
	    super(position, yaw);
        this.MASS = mass;
        this.THRUST_FACTOR = thrustFactor;
        velocity = new Vector3();
    }

    abstract public void handleInput(float deltaTime);

    abstract public void update(float deltaTime);

    public Matrix4x4 getViewMatrix(){
        Matrix4x4 modelMatrix = partBody.getMatrix();

        cameraPosition = modelMatrix.getInverse().multiply(new Vector4(0, 5f, 15f, 0)).toVector3();
        cameraPosition = cameraPosition.add(modelMatrix.getPosition());

        Vector3 cameraTarget = modelMatrix.getPosition();

        Vector3 cameraUp = modelMatrix.getInverse().multiply(Vector4.DIR_UP).toVector3();


        Matrix4x4 viewMatrix = Matrix4x4.createViewMatrix(cameraPosition, cameraTarget, cameraUp);

        System.out.println(modelMatrix.getPosition());
        return viewMatrix;
    }

    protected void changeThrottle(float deltaThrottle){
        final float MAX_THROTTLE = 1.0f;
        final float MIN_THROTTLE = -1.0f;
        final float THROTTLE_SETTLE = 0.99f;

        throttle += deltaThrottle;
        throttle = Math.max(MIN_THROTTLE, Math.min(throttle, MAX_THROTTLE));
        throttle *= THROTTLE_SETTLE;
    }

    public Vector3 getCameraPosition(){
        return cameraPosition;
    }
}
