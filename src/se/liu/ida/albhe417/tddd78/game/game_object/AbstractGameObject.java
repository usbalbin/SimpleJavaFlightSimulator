package se.liu.ida.albhe417.tddd78.game.game_object;

import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import se.liu.ida.albhe417.tddd78.game.game_object_part.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AbstractGameObject is any object in the scene that holds one or more GameObjectParts that are both visible and that responds to physics.
 *
 * AbstractGameObject is responsible for making sure that its parts are updating and drawing themselves and that they are
 * properly destroyed along with any constraints. It also holds information about its health(whether it should die or not),
 * score which is number of kills, as well as an entityName.
 *
 * If the object holds multiple parts, it is often useful to bind them together using constraints.
 */
public abstract class AbstractGameObject //Is abstract because it would not be of any use to create a AbstractGameObject
{
    protected Matrix4x4 modelMatrix;
    private final DynamicsWorld physics;
    private final float damageResistance;
    private float health;
    protected final AtomicInteger score;

	/**
	 * Name of player
     */
    public final String entityName;

	/**
	 * If this game object has been killed then this field will be populated with the object killing it
     */
    public AbstractGameObject killedBy = null;

    protected List<GameObjectPart> parts;
    protected List<TypedConstraint> constraints;

    protected AbstractGameObject(Vector3 position, DynamicsWorld physics, float maxHealth, String playerName){
        modelMatrix = new Matrix4x4();
        modelMatrix = modelMatrix.getTranslated(position);
        this.physics = physics;
        this.health = maxHealth;
        this.damageResistance = maxHealth / 10;
        this.score = new AtomicInteger(0);
        this.entityName = playerName;
        this.parts = new ArrayList<>();
        this.constraints = new ArrayList<>();
    }

    //it would be too long to call MVPMatrixId for modelViewProjectionMatrixId
    public void draw(Matrix4x4 cameraMatrix, int modelViewProjectionMatrixId, int modelMatrixId){

        for(GameObjectPart part : parts)
            part.draw(cameraMatrix, modelViewProjectionMatrixId, modelMatrixId);
    }

    public void hitCalculation(ManifoldPoint cp){health -= Math.max(cp.appliedImpulse - damageResistance, 0);}

    public void hitRegister(AbstractGameObject other){
        if(shouldDie() && killedBy == null)
            killedBy = other;
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
