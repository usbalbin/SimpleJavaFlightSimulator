package se.liu.ida.albhe417.tddd78.game.game_object.misc;

import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Project TDDD78
 *
 * File created by Albin on 07/04/2016.
 */
public abstract class Weapon extends AbstractGameObject{

    Weapon(Vector3 position, DynamicsWorld physics, String gunName){
        super(position, physics, Float.POSITIVE_INFINITY, gunName);
    }

    abstract public void fire(float deltaTime);

    //draw method of any type of weapon will have to be different from default draw() in order to reuse bullet mesh
    abstract public void draw(Matrix4x4 cameraMatrix, int modelViewProjectionMatrixId, int modelMatrixId);
}
