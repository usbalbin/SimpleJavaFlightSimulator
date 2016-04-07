package se.liu.ida.albhe417.tddd78.game;

import com.bulletphysics.ContactProcessedCallback;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.GameObject.AbstractGameObject;

/**
 * Created by Albin_Hedman on 2016-04-07.
 */
public class TargetHitCallback extends ContactProcessedCallback {
    private Game game;
    private DynamicsWorld physics;

    public TargetHitCallback(Game game, DynamicsWorld physics){
        this.game = game;
        this.physics = physics;
    }

    @Override
    public boolean contactProcessed(ManifoldPoint cp, Object body0, Object body1) {
        CollisionObject collisionObject0 = (CollisionObject)body0;
        CollisionObject collisionObject1 = (CollisionObject)body1;

        AbstractGameObject gameObject0 = (AbstractGameObject)collisionObject0.getUserPointer();
        AbstractGameObject gameObject1 = (AbstractGameObject)collisionObject1.getUserPointer();

        gameObject0.hit(cp, gameObject1);
        gameObject1.hit(cp, gameObject0);


        return true;
    }
}
