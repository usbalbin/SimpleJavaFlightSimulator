package se.liu.ida.albhe417.tddd78.game.GameObject;

import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.List;

public abstract class AbstractGameObject
{
    //protected Vector3 position;
    //protected float yaw, pitch, roll;
    protected Matrix4x4 modelMatrix;
    protected DynamicsWorld physics;
    protected Game game;

    protected List<GameObjectPart> parts;

    public AbstractGameObject(Vector3 position, DynamicsWorld physics, Game game){
        modelMatrix = new Matrix4x4();
        modelMatrix = modelMatrix.getTranslated(position);
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
