package se.liu.ida.albhe417.tddd78.game.Collision;

import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Created by Albin_Hedman on 2016-03-17.
 */
public class CollisionFrustum implements CollisionObject{
    protected Matrix4x4 frustumMatrix;

    //TODO might not work, check me!!!
    public boolean intersects(CollisionBoxAA box, Matrix4x4 modelMatrix) {
        Matrix4x4 frustumModelMatrix = frustumMatrix.multiply(modelMatrix);

        Vector3 translatedFrustumLowerCorner = new Vector3(-1, -1, -1);
        Vector3 translatedFrustumHigherCorner = new Vector3(1, 1, 1);

        Vector3 translatedBoxLowerCorner = frustumModelMatrix.multiply(box.lowerCorner);
        Vector3 translatedBoxHigherCorner = frustumModelMatrix.multiply(box.higherCorner);

        CollisionBoxAA translatedBox = new CollisionBoxAA(translatedBoxLowerCorner, translatedBoxHigherCorner);
        CollisionBoxAA translatedFrustumBox = new CollisionBoxAA(translatedFrustumLowerCorner, translatedFrustumHigherCorner);

        return translatedFrustumBox.intersects(translatedBox);
    }

    @Override
    public boolean intersects(CollisionBoxAA box) {
        return intersects(box, new Matrix4x4());
    }

    //TODO might not work, check me!!!
    public boolean intersects(CollisionSphere sphere, Matrix4x4 modelMatrix) {
        Matrix4x4 frustumModelMatrix = frustumMatrix.multiply(modelMatrix);

        Vector3 translatedFrustumLowerCorner = new Vector3(-1, -1, -1);
        Vector3 translatedFrustumHigherCorner = new Vector3(1, 1, 1);

        Vector3 translatedSphereCenter = frustumModelMatrix.multiply(sphere.center);

        CollisionSphere translatedSphere = new CollisionSphere(translatedSphereCenter, sphere.getRadius());
        CollisionBoxAA translatedFrustumBox = new CollisionBoxAA(translatedFrustumLowerCorner, translatedFrustumHigherCorner);

        return translatedFrustumBox.intersects(translatedSphere);
    }

    @Override
    public boolean intersects(CollisionSphere sphere) {
        return intersects(sphere, new Matrix4x4());
    }

    public boolean intersects(Vector3 point, Matrix4x4 modelMatrix){
        return intersects(new CollisionSphere(point, 0.0f), modelMatrix);
    }

    @Override
    public boolean intersects(Vector3 point) {
        return intersects(point, new Matrix4x4());
    }

    @Override
    public boolean intersects(CollisionFrustum frustum) {
        return false;
    }
}
