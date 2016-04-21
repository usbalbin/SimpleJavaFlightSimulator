package se.liu.ida.albhe417.tddd78.game.GameObject;

import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.GameObjectPart.GameObjectPart;
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
    protected final Game game;
    private final float DAMAGE_RESISTANCE;
    private float health;
    public final AtomicInteger score;
    public final String playerName;
    public AbstractGameObject killedBy;

    protected List<GameObjectPart> parts;
    protected List<TypedConstraint> constaints;

    protected AbstractGameObject(Vector3 position, DynamicsWorld physics, Game game, float maxHealth, String playerName){
        modelMatrix = new Matrix4x4();
        modelMatrix = modelMatrix.getTranslated(position);
        this.physics = physics;
        this.game = game;
        this.health = maxHealth;
        this.DAMAGE_RESISTANCE = maxHealth / 10;
        this.score = new AtomicInteger(0);
        this.playerName = playerName;
        this.parts = new ArrayList<>();
        this.constaints = new ArrayList<>();
    }


    public void draw(Matrix4x4 cameraMatrix, int MVPMatrixId, int modelMatrixId){

        for(GameObjectPart part : parts){
            part.draw(cameraMatrix, MVPMatrixId, modelMatrixId);
        }
    }

    public void hitCalculation(ManifoldPoint cp){health -= Math.max(cp.appliedImpulse - DAMAGE_RESISTANCE, 0);}

    public void hitScore(AbstractGameObject other){
        if(other.shouldDie()) {
            other.killedBy = this;
            this.score.incrementAndGet();
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

        constaints.forEach(physics::removeConstraint);
    }

    public void update(){

    }

    public void addConnection(TypedConstraint constraint){
        constaints.add(constraint);

    }
}
