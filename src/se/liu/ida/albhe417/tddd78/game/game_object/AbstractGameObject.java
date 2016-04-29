package se.liu.ida.albhe417.tddd78.game.game_object;

import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.game_object_Part.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project TDDD78
 *
 * File created by Albin.
 */
public abstract class AbstractGameObject
{
    //protected Vector3 position;
    //protected float yaw, pitch, roll;
    protected Matrix4x4 modelMatrix;
    private final DynamicsWorld physics;
    private final float damageResistance;
    private float health;
    protected final AtomicInteger score;
    public final String playerName;
    public AbstractGameObject killedBy;

    protected List<GameObjectPart> parts;
    protected List<TypedConstraint> constraints;

    protected AbstractGameObject(Vector3 position, DynamicsWorld physics, Game game, float maxHealth, String playerName){
        modelMatrix = new Matrix4x4();
        modelMatrix = modelMatrix.getTranslated(position);
        this.physics = physics;
        this.health = maxHealth;
        this.damageResistance = maxHealth / 10;
        this.score = new AtomicInteger(0);
        this.playerName = playerName;
        this.parts = new ArrayList<>();
        this.constraints = new ArrayList<>();
    }


    public void draw(Matrix4x4 cameraMatrix, int MVPMatrixId, int modelMatrixId){

        for(GameObjectPart part : parts){
            part.draw(cameraMatrix, MVPMatrixId, modelMatrixId);
        }
    }

    public void hitCalculation(ManifoldPoint cp){health -= Math.max(cp.appliedImpulse - damageResistance, 0);}

    public void hitRegister(AbstractGameObject other){
        if(shouldDie() && killedBy == null) {
            killedBy = other;
        }
    }

    public boolean shouldDie(){
        return health <= 0;
    }

    public void destroy(){
        for (GameObjectPart part : parts) {
            part.destroy(physics);
            physics.removeRigidBody(part.getPhysicsObject());
        }
        if(killedBy != null)
            killedBy.score.incrementAndGet();

        constraints.forEach(physics::removeConstraint);
    }

    public void update(){
        parts.forEach(GameObjectPart::update);
    }

    public void addConnection(TypedConstraint constraint){
        constraints.add(constraint);
    }

}
