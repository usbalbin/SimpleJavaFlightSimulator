package se.liu.ida.albhe417.tddd78.game;

import com.bulletphysics.ContactProcessedCallback;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.GameObject.Misc.Projectile;
import se.liu.ida.albhe417.tddd78.game.GameObject.Misc.Target;

/**
 * Created by Albin_Hedman on 2016-04-07.
 */
public class TargetHitCallback extends ContactProcessedCallback {
    Game game;
    DynamicsWorld physics;

    public TargetHitCallback(Game game, DynamicsWorld physics){
        this.game = game;
        this.physics = physics;
    }

    @Override
    public boolean contactProcessed(ManifoldPoint cp, Object body0, Object body1) {
        CollisionObject collisionObject0 = (CollisionObject)body0;
        CollisionObject collisionObject1 = (CollisionObject)body1;

        Projectile projectile;
        Target target;

        if((collisionObject0.getCollisionFlags() &
            CollisionFlags.CUSTOM_MATERIAL_CALLBACK) !=
            0)
        {

            projectile = (Projectile)collisionObject0.getUserPointer();
            target = (Target)collisionObject1.getUserPointer();
        }
        else {
            projectile = (Projectile)collisionObject1.getUserPointer();
            target = (Target)collisionObject0.getUserPointer();
        }



        if(projectile != null)
            projectile.hit(target);
        if(target != null)
            target.hit(physics);
        game.hit(target);

        System.out.println("Hej hopp");

        return true;
    }
}
