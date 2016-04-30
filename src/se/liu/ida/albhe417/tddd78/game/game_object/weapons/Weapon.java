package se.liu.ida.albhe417.tddd78.game.game_object.weapons;

import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Weapon might be implemented when one wants mostly any form of weapon
 */
public abstract class Weapon extends AbstractGameObject{

    Weapon(Vector3 position, DynamicsWorld physics, String gunName){
        super(position, physics, Float.POSITIVE_INFINITY, gunName);
    }

    abstract public void fire(float deltaTime);
}
