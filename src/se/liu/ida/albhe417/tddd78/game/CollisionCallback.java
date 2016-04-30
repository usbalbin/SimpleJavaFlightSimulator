package se.liu.ida.albhe417.tddd78.game;

import com.bulletphysics.ContactProcessedCallback;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;

/**
 * CollisionCallback is useful for snapping up all collisions from the physics engine.
 *
 * When implemented properly, every collision will call the contactProcessed method
 * which in turn will notify the colliding parties about the event
 */
class CollisionCallback extends ContactProcessedCallback {

    @Override
    public boolean contactProcessed(ManifoldPoint cp, Object body0, Object body1) {
        CollisionObject collisionObject0 = (CollisionObject)body0;
        CollisionObject collisionObject1 = (CollisionObject)body1;

        AbstractGameObject gameObject0 = (AbstractGameObject)collisionObject0.getUserPointer();
        AbstractGameObject gameObject1 = (AbstractGameObject)collisionObject1.getUserPointer();

        gameObject0.hitCalculation(cp);
        gameObject1.hitCalculation(cp);

        gameObject0.hitRegister(gameObject1);
        gameObject1.hitRegister(gameObject0);

        return true;
    }
}
