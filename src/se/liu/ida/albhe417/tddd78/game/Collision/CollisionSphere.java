package se.liu.ida.albhe417.tddd78.game.Collision;

import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Created by Albin_Hedman on 2016-03-17.
 */
public class CollisionSphere implements CollisionObject{
    protected Vector3 center;
    protected float radius;

    public CollisionSphere(Vector3 center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    @Override
    public boolean intersects(CollisionBoxAA box) {
        return box.intersects(this);
    }

    @Override
    public boolean intersects(CollisionSphere sphere) {
        float radius2 = (sphere.getRadius() + this.radius);
        radius2 *= radius2;

        float dist2 = this.center.sub(sphere.center).length2();
        return dist2 < radius2;
    }

    @Override
    public boolean intersects(Vector3 point) {
        return intersects(new CollisionSphere(point, 0.0f));
    }

    @Override
    public boolean intersects(CollisionFrustum frustum) {
        return frustum.intersects(this);
    }

}
