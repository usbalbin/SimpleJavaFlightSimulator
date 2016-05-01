package se.liu.ida.albhe417.tddd78.game.game_object.vehicles;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.game_object_part.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;


/**
 * AbstractVehicle is useful when you want some sort of controllable game object with support for a chasing camera.
 *
 * Thus it has methods for finding a suitable camera position and projection matrix although handling user input will have
 * to be implemented by the extending class. Because of its inheritance from AbstractGameObject it is also fully visible
 * and has proper physics as well as book keeping of player score.
 */
public abstract class AbstractVehicle extends AbstractGameObject
{
    private Vector3 cameraPosition;

	/**
	 * The main part of the vehicle which the camera will have as its target
     */
    protected GameObjectPart partBody = null;


    AbstractVehicle(Vector3 position, DynamicsWorld physics, float maxHealth, String playerName){
	    super(position, physics, maxHealth, playerName);
        this.cameraPosition = new Vector3(0);
    }

    abstract public void handleInput(float deltaTime);

    public Matrix4x4 getViewMatrix(){
        Matrix4x4 modelMatrix = partBody.getMatrix();
        final Vector3 cameraOffset = new Vector3(0, 5.0f, 15.0f);

        cameraPosition = modelMatrix.multiply(cameraOffset, true);

        Vector3 cameraTarget = modelMatrix.getPosition();

        Vector3 cameraUp = modelMatrix.multiply(Vector3.UPWARD, false);


        return Matrix4x4.createViewMatrix(cameraPosition, cameraTarget, cameraUp);
    }

    public Vector3 getCameraPosition(){
        return cameraPosition;
    }

    public Vector3 getDirection(){
        Vector3 direction = new Vector3(0, 0, -1);
        Matrix4x4 modelMatrix = partBody.getMatrix();

        direction = modelMatrix.multiply(direction, false);
        return direction;
    }

    public Vector3 getVelocity(){
        Vector3f velocity3f = new Vector3f();
        partBody.getPhysicsObject().getLinearVelocity(velocity3f);
        return new Vector3(velocity3f);
    }

    public int getScore(){
        return score.get();
    }

    public Matrix4x4 getModelMatrix(){
        return partBody.getMatrix();
    }
}
