package se.liu.ida.albhe417.tddd78.game.Collision;

import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Created by Albin_Hedman on 2016-03-17.
 */
public class CollisionBoxAA implements CollisionObject{
    protected Vector3 lowerCorner;
    protected Vector3 higherCorner;

    public CollisionBoxAA(Vector3 lowerCorner, Vector3 higherCorner){
        this.lowerCorner = lowerCorner;
        this.higherCorner = higherCorner;
    }

    @Override
    public boolean intersects(CollisionBoxAA box) {
        return this.lowerCorner.getX() <= box.higherCorner.getX() &&
                box.lowerCorner.getX() <= this.higherCorner.getX() &&
                this.lowerCorner.getY() <= box.higherCorner.getY() &&
                box.lowerCorner.getY() <= this.higherCorner.getY() &&
                this.lowerCorner.getZ() <= box.higherCorner.getZ() &&
                box.lowerCorner.getZ() <= this.higherCorner.getZ();
    }

    @Override
    public boolean intersects(CollisionSphere sphere) {
        float radius = sphere.getRadius();
        return this.lowerCorner.getX() <= sphere.center.getX() + radius &&
                sphere.center.getX() - radius <= this.higherCorner.getX() &&
                this.lowerCorner.getY() <= sphere.center.getY() + radius &&
                sphere.center.getY() - radius <= this.higherCorner.getY() &&
                this.lowerCorner.getZ() <= sphere.center.getZ() + radius &&
                sphere.center.getZ() - radius <= this.higherCorner.getZ();
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
