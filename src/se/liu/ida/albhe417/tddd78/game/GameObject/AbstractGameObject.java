package se.liu.ida.albhe417.tddd78.game.GameObject;

import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;

public abstract class AbstractGameObject
{
    //protected Vector3 position;
    //protected float yaw, pitch, roll;
    protected Matrix4x4 modelMatrix;
    protected DynamicsWorld physics;
    protected Game game;

    protected ArrayList<GameObjectPart> parts;

    public AbstractGameObject(Vector3 position, float yaw, DynamicsWorld physics, Game game){
    	/*this.position = position;
        this.yaw = 0;
        this.pitch = 0;
        this.roll = 0;*/
        modelMatrix = new Matrix4x4();
        modelMatrix = modelMatrix.getTranslated(position);
        modelMatrix = modelMatrix.getRotated(yaw, 0, 0);
        this.physics = physics;
        this.game = game;
    }

    public void update(float deltaTime){

    }

    public void draw(Matrix4x4 cameraMatrix, int MVPmatrixId, int modelMatrixId){

        for(GameObjectPart part : parts){
            part.draw(cameraMatrix, MVPmatrixId, modelMatrixId);
        }
    }

    public void hit(ManifoldPoint cp, AbstractGameObject other){};

    public void destroy(){
        for (GameObjectPart part : parts) {
            part.destroy(physics);
            physics.removeRigidBody(part.getPhysicsObject());
        }
        game.remove(this);
    };
}
