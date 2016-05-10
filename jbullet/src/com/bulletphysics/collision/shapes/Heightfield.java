package com.bulletphysics.collision.shapes;

/**
 * Created by Albin_Hedman on 2016-05-09.
 */
public interface Heightfield {
    float getHeight(float x, float y);

    float getWidth();
    float getLength();

    float getMinHeight();
    float getMaxHeight();
}
