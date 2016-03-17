package se.liu.ida.albhe417.tddd78.game.Collision;

import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Created by Albin_Hedman on 2016-03-17.
 */
public interface CollisionObject {
    public boolean intersects(CollisionSphere sphere);
    public boolean intersects(CollisionBoxAA box);
    public boolean intersects(Vector3 point);
    public boolean intersects(CollisionFrustum frustum);
}
