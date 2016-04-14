package se.liu.ida.albhe417.tddd78.game.GameObject.Misc;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles.AbstractVehicle;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by Albin on 07/04/2016.
 */
public class Gun extends Weapon {
    private AbstractVehicle owner;
    private DynamicsWorld physics;
    private Vector3 offsetPosition;
    private float timeLastShotSec = 0;
    private final float FIRE_RATE;
    private final float BULLET_RADIUS = 0.5f;
    private final float BULLET_MASS = 1;
    private final float MUZZLE_VELOCITY = 400;
    private final float MAX_BULLETS_IN_AIR = 100;
    private float currTimeSec = 0;

    private Deque<RigidBody> bullets;
    private ProjectileMesh projectile;

    public Gun(Vector3 offsetPosition, AbstractVehicle owner, DynamicsWorld physics, int shaderProgram, Game game){
        super(offsetPosition, physics, game);
        this.offsetPosition = offsetPosition;
        this.FIRE_RATE = 0.1f; //= 1/shots per sec
        this.owner = owner;
        this.physics = physics;
        this.bullets = new LinkedList<>();
        this.projectile = new ProjectileMesh(BULLET_RADIUS, shaderProgram, physics, game);
    }

    @Override
    public void fire(float deltaTime) {
        currTimeSec += deltaTime;
        if(currTimeSec - timeLastShotSec < FIRE_RATE)
            return;

        Matrix4x4 modelMatrix = owner.getModelMatrix();
        Vector3 position = modelMatrix.multiply(offsetPosition, true);
        Vector3 velocity = owner.getDirection().multiply(MUZZLE_VELOCITY);
        velocity.add(owner.getVelocity());

        //Physics
        Matrix4x4 matrix = Matrix4x4.createTranslation(position);
        Transform transform = new Transform(matrix.toMatrix4f());
        MotionState motionState = new DefaultMotionState(transform);
        SphereShape collisionShape = new SphereShape(BULLET_RADIUS);

        Vector3f inertia = new Vector3f();
        collisionShape.calculateLocalInertia(BULLET_MASS, inertia);
        RigidBody physicsObject = new RigidBody(BULLET_MASS, motionState, collisionShape, inertia);
        physicsObject.setLinearVelocity(velocity.toVector3f());
        physicsObject.setCollisionFlags(physicsObject.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);
        physicsObject.setUserPointer(this);
        bullets.add(physicsObject);
        physics.addRigidBody(physicsObject);

        if(bullets.size() > MAX_BULLETS_IN_AIR)
            physics.removeRigidBody(bullets.pop());
    }

    @Override
    public void reload() {

    }

    @Override
    public boolean noAmmo() {
        return false;
    }

    public void draw(Matrix4x4 cameraMatrix, int MVPmatrixId, int modelMatrixId){
        for (RigidBody bullet: bullets) {
            projectile.setBullet(bullet);
            projectile.draw(cameraMatrix, MVPmatrixId, modelMatrixId);
        }
    }

    public void destroy(){
        projectile.destroy();
        for (RigidBody bullet: bullets) {
            physics.removeRigidBody(bullet);
        }
    }
}
