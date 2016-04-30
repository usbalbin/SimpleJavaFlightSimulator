package se.liu.ida.albhe417.tddd78.game.game_object.weapons;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import se.liu.ida.albhe417.tddd78.game.game_object.vehicles.AbstractVehicle;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;
import java.util.Deque;
import java.util.LinkedList;

/**
 * A fully automatic weapon firing large bullets at a high rate of fire
 */
public class Gun extends Weapon {
    private AbstractVehicle owner;
    private final DynamicsWorld physics;
    private final Vector3 offsetPosition;
    private float timeLastShotSec = 0;
    private final float fireRate;
    private static final float BULLET_RADIUS = 0.5f;
    private float currTimeSec = 0;

    private final Deque<RigidBody> bullets;
    private final ProjectileMesh projectile;

    public Gun(Vector3 offsetPosition, AbstractVehicle owner, DynamicsWorld physics, int shaderProgram, String playerName){
        super(offsetPosition, physics, playerName);
        this.offsetPosition = offsetPosition;
        this.fireRate = 0.1f; //= 1/shots per sec
        this.owner = owner;
        this.physics = physics;
        this.bullets = new LinkedList<>();
        this.projectile = new ProjectileMesh(BULLET_RADIUS, shaderProgram, physics);
    }

    @Override
    public void fire(float deltaTime) {
        currTimeSec += deltaTime;
        if(currTimeSec - timeLastShotSec < fireRate || noAmmo())
            return;

        timeLastShotSec = currTimeSec;
        Matrix4x4 modelMatrix = owner.getModelMatrix();
        Vector3 position = modelMatrix.multiply(offsetPosition, true);

        final float muzzleVelocity = 900;
        Vector3 velocity = owner.getDirection().multiply(muzzleVelocity);
        velocity.add(owner.getVelocity());

        //Physics
        Matrix4x4 matrix = Matrix4x4.createTranslation(position);
        Transform transform = new Transform(matrix.toMatrix4f());
        MotionState motionState = new DefaultMotionState(transform);
        SphereShape collisionShape = new SphereShape(BULLET_RADIUS);

        float bulletMass = 1;
        Vector3f inertia = new Vector3f();
        collisionShape.calculateLocalInertia(bulletMass, inertia);
        RigidBody physicsObject = new RigidBody(bulletMass, motionState, collisionShape, inertia);
        physicsObject.setLinearVelocity(velocity.toVector3f());
        physicsObject.setCollisionFlags(physicsObject.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);
        physicsObject.setUserPointer(owner);
        physicsObject.setCcdMotionThreshold(BULLET_RADIUS * BULLET_RADIUS);
        physicsObject.setCcdSweptSphereRadius(BULLET_RADIUS / 5);
        bullets.add(physicsObject);
        physics.addRigidBody(physicsObject);

        final float maxBulletsInAir = 25;
        if(bullets.size() > maxBulletsInAir)
            physics.removeRigidBody(bullets.pop());
    }

    public boolean noAmmo() {
        return false;
    }

    public void draw(Matrix4x4 cameraMatrix, int modelViewProjectionMatrixId, int modelMatrixId){
        for (RigidBody bullet: bullets) {
            projectile.setBullet(bullet);
            projectile.draw(cameraMatrix, modelViewProjectionMatrixId, modelMatrixId);
        }
    }

    @Override
    public void destroy(){
        super.destroy();
        projectile.destroy();
        bullets.forEach(physics::removeRigidBody);
    }
}
