package se.liu.ida.albhe417.tddd78.game.game_object.weapons;

import se.liu.ida.albhe417.tddd78.math.Matrix4x4;

/**
 * Weapon might be implemented when one wants mostly any form of weapon
 */
public interface Weapon{
    void fire(float deltaTime);

    void draw(Matrix4x4 cameraMatrix, int modelViewProjectionMatrixId, int modelMatrixId);

    void destroy();
}
