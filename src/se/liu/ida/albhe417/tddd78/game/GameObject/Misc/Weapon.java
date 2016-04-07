package se.liu.ida.albhe417.tddd78.game.GameObject.Misc;

import se.liu.ida.albhe417.tddd78.math.Matrix4x4;

/**
 * Created by Albin on 07/04/2016.
 */
public interface Weapon {
    public void fire(float currTimeSec);
    public void reload();
    public boolean noAmmo();
    public void draw(Matrix4x4 cameraMatrix, int MVPmatrixId, int modelMatrixId);
}
