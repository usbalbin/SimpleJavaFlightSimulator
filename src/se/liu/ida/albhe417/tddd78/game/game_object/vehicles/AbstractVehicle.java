package se.liu.ida.albhe417.tddd78.game.game_object.vehicles;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.game_object_Part.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;


/**
 * Project TDDD78
 *
 * File created by Albin.
 */
public abstract class AbstractVehicle extends AbstractGameObject
{
    final float mass;
    final float thrustFactor;
    float throttle = 0;
    private Vector3 cameraPosition;
    GameObjectPart partBody;


    AbstractVehicle(Vector3 position, float mass, float thrustFactor, DynamicsWorld physics, Game game, float maxHealth, String playerName){
	    super(position, physics, game, maxHealth, playerName);
        this.mass = mass;
        this.thrustFactor = thrustFactor;
        this.cameraPosition = new Vector3(0);
    }

    protected void setPartBody(GameObjectPart partBody){
        this.partBody = partBody;
    }

    abstract public void handleInput(float deltaTime);

    public Matrix4x4 getViewMatrix(){
        Matrix4x4 modelMatrix = partBody.getMatrix();
        final Vector3 cameraOffset = new Vector3(0, 5.0f, 15.0f);

        cameraPosition = modelMatrix.multiply(cameraOffset, true);
        //cameraPosition = cameraPosition.add(modelMatrix.getPosition());

        Vector3 cameraTarget = modelMatrix.getPosition();

        Vector3 cameraUp = modelMatrix.multiply(Vector3.UP, false);


        return Matrix4x4.createViewMatrix(cameraPosition, cameraTarget, cameraUp);
    }

    void changeThrottle(float deltaThrottle){
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
