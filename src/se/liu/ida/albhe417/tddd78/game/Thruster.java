package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.game.game_object_Part.Wing;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Project TDDD78
 * <p>
 * File created by Albin on 21/04/2016.
 */
public class Thruster {
    private final float thrustFactor;
    private final Vector3 offsetPosition;
    private final Vector3 direction;

    private float throttle = 0;
    private final Wing wingHoldingThruster;

    public Thruster(Vector3 offsetPosition, Vector3 direction, float thrustFactor, Wing wingHoldingThruster) {
        this.offsetPosition = offsetPosition;
        this.direction = direction;
        assert Helpers.fEquals(direction.length2(), 1) : "Non-normalized direction!";
        this.thrustFactor = thrustFactor;
        this.wingHoldingThruster = wingHoldingThruster;
    }

    public void update(float deltaThrottle, float deltaTime){
        changeThrust(deltaThrottle * deltaTime);

        float lift = throttle * thrustFactor;
        Matrix4x4 wingMatrix = wingHoldingThruster.getMatrix();

        Vector3 thrustForce = new Vector3(0, 0, -lift);//direction.multiply(lift);
        thrustForce = wingMatrix.multiply(thrustForce, false);//

        assert Helpers.fEquals(thrustForce.length(), lift) : "Transforming thrust force failed";

        //TODO: uncomment

        Vector3 transformedThrustForcePoint = new Vector3();
        //Vector3 transformedThrustForcePoint = wingMatrix.multiply(offsetPosition, true);
        //transformedThrustForcePoint = wingMatrix.multiply(transformedThrustForcePoint, false);

        wingHoldingThruster.getPhysicsObject().applyForce(thrustForce.toVector3f(), transformedThrustForcePoint.toVector3f());
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
