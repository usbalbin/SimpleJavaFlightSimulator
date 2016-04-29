package se.liu.ida.albhe417.tddd78.game;

import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Project TDDD78
 *
 * File created by Albin on 15/03/2016.
 */
abstract class Terrain extends AbstractGameObject {
    int width;
    int height;

    final int shaderProgram;

    Terrain(Vector3 position, final float height_factor, final int shaderProgram, DynamicsWorld physics, Game game){
        super(position, physics, game, Float.POSITIVE_INFINITY, "Ground");
        this.shaderProgram = shaderProgram;
    }

    abstract public void update(Vector3 cameraPos, Matrix4x4 cameraMatrix);
    abstract public void updateGraphics();

}
