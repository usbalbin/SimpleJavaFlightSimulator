package se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles;

import com.bulletphysics.dynamics.DynamicsWorld;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.Transform;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.GameObject.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.ida.albhe417.tddd78.math.Vector4;

public abstract class AbstractVehicle extends AbstractGameObject
{
    protected float MASS;
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

        cameraPosition = modelMatrix.getInverse().multiply(new Vector3(0, 5f, 15f), false);
        cameraPosition = cameraPosition.add(modelMatrix.getPosition());

        Vector3 cameraTarget = modelMatrix.getPosition();

        Vector3 cameraUp = modelMatrix.getInverse().multiply(Vector4.DIR_UP).toVector3();


        Matrix4x4 viewMatrix = Matrix4x4.createViewMatrix(cameraPosition, cameraTarget, cameraUp);

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
