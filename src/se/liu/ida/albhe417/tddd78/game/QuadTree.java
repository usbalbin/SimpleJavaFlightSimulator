package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Created by Albin_Hedman on 2016-03-14.
 */
public class QuadTree {
    //TODO implement me
    //Info: http://victorbush.com/2015/01/tessellated-terrain/

    private QuadTree leftFront;
    private QuadTree rightFront;
    private QuadTree leftBottom;
    private QuadTree rightBottom;

    private Vector3 position;
    private float size;
    private short level;


    public QuadTree(Vector3 position,  float size, short level, Vector3 cameraPos, float detailFactor, short maxLevels) {
        this.position = position;
        this.size = size;
        this.level = level;
        funct(cameraPos, detailFactor, maxLevels);
    }

    private void funct(Vector3 cameraPos, float detailFactor, short maxLevels){
        float distSquared = cameraPos.sub(position).length2();
        float distLimit = detailFactor * level;
        if(distSquared < distLimit * distLimit && level < maxLevels){
            final float childSize = size / 2;
            final short childLevel = (short)(1 + level);

            leftFront = new QuadTree(position.add(new Vector3(-childSize, 0, -childSize)), childSize, childLevel, cameraPos, detailFactor, maxLevels);
            rightFront = new QuadTree(position.add(new Vector3(+childSize, 0, -childSize)), childSize, childLevel, cameraPos, detailFactor, maxLevels);
            leftBottom = new QuadTree(position.add(new Vector3(-childSize, 0, +childSize)), childSize, childLevel, cameraPos, detailFactor, maxLevels);
            rightBottom = new QuadTree(position.add(new Vector3(+childSize, 0, +childSize)), childSize, childLevel, cameraPos, detailFactor, maxLevels);
        }
        else {
            leftFront = null;
            rightFront = null;
            leftBottom = null;
            rightBottom = null;
        }
    }

    private boolean hasChildren(){
        return
            leftFront != null &&
            rightFront != null &&
            leftBottom != null &&
            rightBottom != null;
    }
}