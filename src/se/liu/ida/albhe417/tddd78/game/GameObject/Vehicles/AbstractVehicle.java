package se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles;

import com.bulletphysics.dynamics.DynamicsWorld;

import javax.vecmath.Vector3f;

import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.GameObject.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.ida.albhe417.tddd78.math.Vector4;

public abstract class AbstractVehicle extends AbstractGameObject
{
    protected final float MASS;
    protected final float THRUST_FACTOR;
    protected float throttle = 0;
    protected Vector3 velocity;
    protected Vector3 cameraPosition;
    protected GameObjectPart partBody;


    public AbstractVehicle(Vector3 position, float mass, float thrustFactor, DynamicsWorld physics, Game game){
	    super(position, physics, game);
        this.MASS = mass;
        this.THRUST_FACTOR = thrustFactor;
        velocity = new Vector3();
    }

    abstract public void handleInput(float deltaTime);

    abstract public void update(float deltaTime);

    public Matrix4x4 getViewMatrix(){
        Matrix4x4 modelMatrix = partBody.getMatrix();
        final Vector3 cameraOffset = new Vector3(0, 5.0f, 15.0f);

        cameraPosition = modelMatrix.getInverse().multiply(cameraOffset, false);
        cameraPosition = cameraPosition.add(modelMatrix.getPosition());

        Vector3 cameraTarget = modelMatrix.getPosition();

        Vector3 cameraUp = modelMatrix.getInverse().multiply(Vector4.DIR_UP).toVector3();


        Matrix4x4 viewMatrix = Matrix4x4.createViewMatrix(cameraPosition, cameraTarget, cameraUp);

        return viewMatrix;
    }

    protected void changeThrottle(float deltaThrottle){
        final float maxThrottle = 1.0f;
        final float minThrottle = -1.0f;
        final float throttleSettle = 0.99f;

        throttle += deltaThrottle;
        throttle = Math.max(minThrottle, Math.min(throttle, maxThrottle));
        throttle *= throttleSettle;
    }

    public Vector3 getCameraPosition(){
        return cameraPosition;
    }

    public Vector3 getPosition(){
        Matrix4x4 modelMatrix = partBody.getMatrix();

        return  modelMatrix.getPosition();
    }

    public Vector3 getVelocity(){
        Vector3f velocity3f = new Vector3f();
        partBody.getPhysicsObject().getLinearVelocity(velocity3f);
        return new Vector3(velocity3f);
    }

    public Vector3 getDirection(){
        Vector3 direction = new Vector3(0, 0, -1);
        Matrix4x4 modelMatrix = partBody.getMatrix();

        direction = modelMatrix.getInverse().multiply(direction, false);
        return direction;
    }

    public Matrix4x4 getModelMatrix(){
        return partBody.getMatrix();
    }
}
