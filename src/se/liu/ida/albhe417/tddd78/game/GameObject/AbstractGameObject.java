package se.liu.ida.albhe417.tddd78.game.GameObject;

import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractGameObject
{
    //protected Vector3 position;
    //protected float yaw, pitch, roll;
    protected Matrix4x4 modelMatrix;
    protected DynamicsWorld physics;
    protected Game game;
    protected final float MAX_HEALTH;
    protected final float DAMAGE_RESISTANCE;
    protected float health;
    public AtomicInteger score;
    public String playerName;
    public AbstractGameObject killedBy;

    protected List<GameObjectPart> parts;

    public AbstractGameObject(Vector3 position, DynamicsWorld physics, Game game, float maxHealth, String playerName){
        modelMatrix = new Matrix4x4();
        modelMatrix = modelMatrix.getTranslated(position);
        this.physics = physics;
        this.game = game;
        this.MAX_HEALTH = this.health = maxHealth;
        this.DAMAGE_RESISTANCE = maxHealth / 10;
        this.score = new AtomicInteger(0);
        this.playerName = playerName;
    }


    public void update(float deltaTime){

    }

    public void draw(Matrix4x4 cameraMatrix, int MVPmatrixId, int modelMatrixId){

        for(GameObjectPart part : parts){
            part.draw(cameraMatrix, MVPmatrixId, modelMatrixId);
        }
    }

    public void hitCalculation(ManifoldPoint cp, AbstractGameObject other){health -= Math.max(cp.appliedImpulse - DAMAGE_RESISTANCE, 0);}

    public void hitScore(AbstractGameObject other){
        if(other.shouldDie()) {
            other.killedBy = this;
            this.score.incrementAndGet();
        }
    }

    public void hit(){
        if(shouldDie())
            destroy();
    }

    public boolean shouldDie(){
        return health <= 0;
    }

    public void destroy(){
        for (GameObjectPart part : parts) {
            part.destroy(physics);
            physics.removeRigidBody(part.getPhysicsObject());
        }
        game.remove(this);
    };
}
