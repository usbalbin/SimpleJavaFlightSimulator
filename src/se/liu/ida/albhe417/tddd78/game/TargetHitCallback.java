package se.liu.ida.albhe417.tddd78.game;

import com.bulletphysics.ContactProcessedCallback;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.GameObject.AbstractGameObject;

/**
 * Project TDDD78
 *
 * File created by Albin on 2016-04-07.
 */
class TargetHitCallback extends ContactProcessedCallback {

    public TargetHitCallback(){ }

    @Override
    public boolean contactProcessed(ManifoldPoint cp, Object body0, Object body1) {
        CollisionObject collisionObject0 = (CollisionObject)body0;
        CollisionObject collisionObject1 = (CollisionObject)body1;

        AbstractGameObject gameObject0 = (AbstractGameObject)collisionObject0.getUserPointer();
        AbstractGameObject gameObject1 = (AbstractGameObject)collisionObject1.getUserPointer();

        gameObject0.hitCalculation(cp);
        gameObject1.hitCalculation(cp);

        gameObject0.hitScore(gameObject1);
        gameObject1.hitScore(gameObject0);

        //gameObject0.hitAction();
        //gameObject1.hitAction();

        return true;
    }
}
