package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.game.game_object_part.Wing;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Thruster might be attached to a wing for propelling air vehicles or similar
 */
public class Thruster {
    private final float thrustFactor;
    private final Vector3 direction;

    private float throttle = 0;
    private final Wing wingHoldingThruster;

    public Thruster(Vector3 direction, float thrustFactor, Wing wingHoldingThruster) {
        assert Helpers.fEquals(direction.length2(), 1) : "Non-normalized direction!";
        this.direction = direction;
        this.thrustFactor = thrustFactor;
        this.wingHoldingThruster = wingHoldingThruster;
    }

    public void update(float deltaThrottle, float deltaTime){
        changeThrust(deltaThrottle * deltaTime);

        float thrust = throttle * thrustFactor;
        Matrix4x4 wingMatrix = wingHoldingThruster.getMatrix();

        Vector3 thrustForce = direction.multiply(thrust);
        thrustForce = wingMatrix.multiply(thrustForce, false);

        assert Helpers.fEquals(thrustForce.length(), thrust) : "Transforming thrust force failed";

        wingHoldingThruster.getPhysicsObject().applyCentralForce(thrustForce.toVector3f());
    }

    private void changeThrust(float deltaThrottle){
        final float maxThrottle = 1.0f;
        final float minThrottle = -1.0f;
        final float throttleSettle = 0.99f;

        throttle += deltaThrottle;
        throttle = Math.max(minThrottle, Math.min(throttle, maxThrottle));
        throttle *= throttleSettle;
    }
}
