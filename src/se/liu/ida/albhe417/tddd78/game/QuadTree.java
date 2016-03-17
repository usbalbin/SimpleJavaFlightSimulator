package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
    private QuadTree parent;

    /*private QuadTree neighborLF;
    private QuadTree neighborRF;
    private QuadTree neighborLB;
    private QuadTree neighborRB;
   */

   //TODO replace with bitfield to save some space
    private boolean leftStitchPnt = false;
    private boolean frontStitchPnt = false;
    private boolean rightStitchPnt = false;
    private boolean bottomStitchPnt = false;

    //TODO: Check need for precision
    private Vector3 position;
    private int size;
    private short level;

    private static byte[][] heightmap;
    private static float HEIGHT_FACTOR;
    private static int rootSize;

    //TODO: try to get rid of me
    private static QuadTree root;

    /**
     * Create Root QuadTree
     * @param fileNameHeightmap heightmap
     */
    public QuadTree(byte[][] heightmap, final float heightFactor){
        this.HEIGHT_FACTOR = heightFactor;
        this.heightmap = heightmap;
        this.rootSize = this.size = Math.min(heightmap.length, heightmap[0].length);
        this.size -= size % 2;//Make sure size is even
        this.position = new Vector3(size / 2, 0, size / 2);
        this.level = 0;
        root = this;
    }

    /**
     * Setup sub-branches
     * @param position center position
     * @param size size of quad
     * @param level LOD-level
     * @param cameraPos camera position
     * @param detailFactor
     * @param maxLevels max LOD-levels
     */
    private QuadTree(final Vector3 position, final int size, final short level, final Vector3 cameraPos, final float detailFactor, final short maxLevels, QuadTree parent) {
        this.position = position;
        this.size = size;
        this.level = level;
        this.parent = parent;
        generateTree(cameraPos, detailFactor, maxLevels);
    }

    public void update(Vector3 cameraPosition, List<VertexPositionColor> vertices, List<Integer> indices){
        final float detailFactor = 300;
        final short maxLevels = 12;//11;

        generateTree(cameraPosition, detailFactor, maxLevels);

        stitch(null, null, null, null);
        generateVerticesAndIndices(vertices, indices);

    }




























    private void generateTree(final Vector3 cameraPos, final float detailFactor, final short maxLevels){
        //TODO: Change to length2() to save CPU
        final Float[] dists = {
                cameraPos.sub(position).length(),
                cameraPos.sub(position.add(-size / 2, 0, 0)).length(),
                cameraPos.sub(position.add(0, 0, -size / 2)).length(),
                cameraPos.sub(position.add(size / 2, 0, 0)).length(),
                cameraPos.sub(position.add(0, 0, size / 2)).length()
        };

        final float dist = Collections.min(Arrays.asList(dists));


        //TODO make working formula
        //int desiredLevelSquared = (int)((detailFactor * detailFactor) / distSquared);
        //int desiredLevel = (int)Math.max(maxLevels - (dist * dist / (detailFactor * 1000)), 0);
        final int desiredLevel = (int)Math.max(maxLevels - (Math.sqrt(dist) * 50/ (detailFactor)), 0);


        if(desiredLevel > level && level < maxLevels){
            final int childSize = size / 2;
            final int halfChildSize = childSize / 2;
            final short childLevel = (short)(1 + level);

            leftFront = new QuadTree(position.add(new Vector3(-halfChildSize, 0, -halfChildSize)), childSize, childLevel, cameraPos, detailFactor, maxLevels, this);
            rightFront = new QuadTree(position.add(new Vector3(+halfChildSize, 0, -halfChildSize)), childSize, childLevel, cameraPos, detailFactor, maxLevels, this);
            leftBottom = new QuadTree(position.add(new Vector3(-halfChildSize, 0, +halfChildSize)), childSize, childLevel, cameraPos, detailFactor, maxLevels, this);
            rightBottom = new QuadTree(position.add(new Vector3(+halfChildSize, 0, +halfChildSize)), childSize, childLevel, cameraPos, detailFactor, maxLevels, this);
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

    /**
     * Make sure that adjacent quads have same LOD at their common border
     */
    //TODO fix me
    protected void stitch(QuadTree neighborLeft, QuadTree neighborFront, QuadTree neighborRight, QuadTree neighborBottom){
        //Stuff....
        //..
        //..
        if(hasChildren()){
            //                left front   right      bottom
            leftFront.stitch(null, null, rightFront, leftBottom);
            rightFront.stitch(leftFront, null, null, rightBottom);
            leftBottom.stitch(null, leftFront, rightBottom, null);
            rightBottom.stitch(leftBottom, rightFront, null, null);

        }
        else {
            if(neighborLeft == null)
                neighborLeft = findNode(this.position.add(-size, 0, 0));
            if(neighborLeft != null && neighborLeft.level + 1 == this.level)
                stitchLeft(neighborLeft);

            if(neighborFront == null)
                neighborFront = findNode(this.position.add(0, 0, -size));
            if(neighborFront != null && neighborFront.level + 1 == this.level)
                stitchFront(neighborFront);

            if(neighborRight == null)
                neighborRight = findNode(this.position.add(size, 0, 0));
            if(neighborRight != null && neighborRight.level + 1 == this.level)
                stitchRight(neighborRight);

            if(neighborBottom == null)
                neighborBottom = findNode(this.position.add(0, 0, size));
            if(neighborBottom != null && neighborBottom.level + 1 == this.level)
                stitchBottom(neighborBottom);
        }
    }

    //TODO: reuse code better
    private void stitchLeft(final QuadTree leftNeighbor){
        //If our neighbor has children then either the matching quad has same(do nothing) or higher
        // LOD-level(neighbor will do the stitching)
        if(leftNeighbor.hasChildren())
            return;
        leftNeighbor.addRightStitchPnt();
    }

    private void stitchFront(final QuadTree frontNeighbor){
        //If our neighbor has children then either the matching quad has same(do nothing) or higher
        // LOD-level(neighbor will do the stitching)
        if(frontNeighbor.hasChildren())
            return;
        frontNeighbor.addBottomStitchPnt();
    }

    private void stitchRight(final QuadTree rightNeighbor){
        //If our neighbor has children then either the matching quad has same(do nothing) or higher
        // LOD-level(neighbor will do the stitching)
        if(rightNeighbor.hasChildren())
            return;
        rightNeighbor.addLeftStitchPnt();
    }

    private void stitchBottom(final QuadTree bottomNeighbor){
        //If our neighbor has children then either the matching quad has same(do nothing) or higher
        // LOD-level(neighbor will do the stitching)
        if(bottomNeighbor.hasChildren())
            return;
        bottomNeighbor.addFrontStitchPnt();
    }

    protected void generateVerticesAndIndices(final List<VertexPositionColor> vertices, final List<Integer> indices){
        if(hasChildren()){
            leftFront.generateVerticesAndIndices(vertices, indices);
            rightFront.generateVerticesAndIndices(vertices, indices);
            leftBottom.generateVerticesAndIndices(vertices, indices);
            rightBottom.generateVerticesAndIndices(vertices, indices);
        }

        else{
            float halfSide = size / 2;

            Vector3 leftFrontPos = position.add(-halfSide, 0, -halfSide);
            Vector3 rightFrontPos = position.add(+halfSide, 0, -halfSide);
            Vector3 leftBottomPos = position.add(-halfSide, 0, +halfSide);
            Vector3 rightBottomPos = position.add(+halfSide, 0, +halfSide);



            setHeight(leftFrontPos);
            setHeight(rightFrontPos);
            setHeight(leftBottomPos);
            setHeight(rightBottomPos);



            final int index = vertices.size();

            if(!isStitched()){
                vertices.add(new VertexPositionColor(leftFrontPos));//0
                vertices.add(new VertexPositionColor(rightFrontPos));//1
                vertices.add(new VertexPositionColor(leftBottomPos));//2
                vertices.add(new VertexPositionColor(rightBottomPos));//3

                indices.add(0 + index);indices.add(3 + index);indices.add(2 + index);
                indices.add(0 + index);indices.add(1 + index);indices.add(3 + index);
            }else{
                final int numVertices = 5 + numStitchPnts();
                Vector3 center = position;
                setHeight(center);
                Vector3 leftPos = position.add(-halfSide, 0, 0);
                Vector3 frontPos = position.add(0, 0, -halfSide);
                Vector3 rightPos = position.add(+halfSide, 0, 0);
                Vector3 bottomPos = position.add(0, 0, +halfSide);

                setHeight(leftPos);
                setHeight(frontPos);
                setHeight(rightPos);
                setHeight(bottomPos);

                vertices.add(new VertexPositionColor(center));

                vertices.add(new VertexPositionColor(leftFrontPos));
                if(frontStitchPnt)
                    vertices.add(new VertexPositionColor(frontPos));

                vertices.add(new VertexPositionColor(rightFrontPos));
                if(rightStitchPnt)
                    vertices.add(new VertexPositionColor(rightPos));

                vertices.add(new VertexPositionColor(rightBottomPos));
                if(bottomStitchPnt)
                    vertices.add(new VertexPositionColor(bottomPos));

                vertices.add(new VertexPositionColor(leftBottomPos));
                if(leftStitchPnt)
                    vertices.add(new VertexPositionColor(leftPos));


                indices.add(index + (numVertices - 1));   //last vertex
                indices.add(index + 1);         //First vertex
                indices.add(index + 0);         //Center

                for(int i = 0; i < numVertices - 1; i++){
                    indices.add(index + i);
                    indices.add(index + i + 1);
                    indices.add(index + 0);
                }
            }
        }
    }

    protected void addLeftStitchPnt(){
        leftStitchPnt = true;
    }

    protected void addFrontStitchPnt(){
        frontStitchPnt = true;
    }

    protected void addRightStitchPnt(){
        rightStitchPnt = true;
    }

    protected void addBottomStitchPnt(){
        bottomStitchPnt = true;
    }

    protected boolean isStitched(){
        return leftStitchPnt || frontStitchPnt || rightStitchPnt || bottomStitchPnt;
    }

    protected boolean isRoot(){
        return size == rootSize;
    }

    protected int numStitchPnts(){
        return (leftStitchPnt ? 1 : 0) + (frontStitchPnt ? 1 : 0) + (rightStitchPnt ? 1 : 0) + (bottomStitchPnt ? 1 : 0);
    }

    private void setHeight(Vector3 position){
        //position = position.add(this.position);
        int x = Math.min((int)position.getX(), rootSize - 1);
        int z = Math.min((int)position.getZ(), rootSize - 1);
        float y = heightmap[z][x] & 0x00FF;
        position.setY(y / 256f * HEIGHT_FACTOR);
    }

    public int getSize(){
        return size;
    }

    /*private QuadTree getNortNeighbor(QuadType currentQuadType, QuadTree parent){
        if(parent == null)
            return null;
        switch (currentQuadType){
            case NW:
                return parent.leftFront;
            break;
            case NE:
                return parent.rightFront;
                break;
            default:


        }
    }*/

    //TODO decide which is faster this one or the one with the ugly name. If this one wins, remove root-field
    protected QuadTree findNode(final Vector3 position){
        final Vector3 delta = position.sub(this.position);
        final int dx = Math.round(delta.getX());
        final int dz = Math.round(delta.getZ());

        final float radius = size / 2.0f;

        if(dx < -radius || radius < dx ||
           dz < -radius || radius < dz){
            if(isRoot())
                return null;
            else
                return parent.findNode(position);
        }

        if((dz == 0 && dx == 0) || !hasChildren())
            return this;

        if(0 > dz){
            if(0 > dx)
                return leftFront.findNode(position);
            else
                return rightFront.findNode(position);
        }
        else{
            if(0 > dx)
                return leftBottom.findNode(position);
            else
                return rightBottom.findNode(position);
        }
    }

    protected QuadTree findNode_onlyDown(final Vector3 position){
        final Vector3 delta = position.sub(this.position);
        final int dx = Math.round(delta.getX());
        final int dz = Math.round(delta.getZ());

        final float radius = size / 2.0f;

        if(dx < -radius || radius < dx ||
                dz < -radius || radius < dz){
            return null;
        }

        if((dz == 0 && dx == 0) || !hasChildren())
            return this;

        if(0 > dz){
            if(0 > dx)
                return leftFront.findNode_onlyDown(position);
            else
                return rightFront.findNode_onlyDown(position);
        }
        else{
            if(0 > dx)
                return leftBottom.findNode_onlyDown(position);
            else
                return rightBottom.findNode_onlyDown(position);
        }
    }
}