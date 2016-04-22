package se.liu.ida.albhe417.tddd78.game.GameObject.Misc;

import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.GameObject.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Project TDDD78
 *
 * File created by Albin on 07/04/2016.
 */
public abstract class Weapon extends AbstractGameObject{

    Weapon(Vector3 position, DynamicsWorld physics, Game game, float maxHealth, String gunName){
        super(position, physics, game, maxHealth, gunName);
    }

    abstract public void fire(float currTimeSec);
    abstract public boolean noAmmo();
    abstract public void draw(Matrix4x4 cameraMatrix, int MVPMatrixId, int modelMatrixId);
}
