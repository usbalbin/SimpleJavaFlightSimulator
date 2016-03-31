package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.Arrays;
import java.util.Collections;
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

    private static float[] heightmap;
    private static float HEIGHT_FACTOR;
    private static int rootSize;
    private static int hmapSize;
    private static float maxHeight;

    //TODO: try to get rid of me
    private static QuadTree root;

    /**
     * Create Root QuadTree
     * @param fileNameHeightmap heightmap
     */
    public QuadTree(float[] heightmap, final float heightFactor, float maxHeight){
        this.HEIGHT_FACTOR = heightFactor;
        this.heightmap = heightmap;
        this.hmapSize = this.rootSize = this.size = (int)Math.sqrt(heightmap.length);
        this.rootSize = this.size -= size % 2;//Make sure size is even
        this.maxHeight = maxHeight;
        this.position = new Vector3(0, 0, 0);
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

    public void update(Vector3 cameraPosition, List<VertexPositionColorNormal> vertices, List<Integer> indices){
        final float detailFactor = 350;
        final short maxLevels = 11;//11;

        generateTree(cameraPosition, detailFactor, maxLevels);

        stitch(null, null, null, null);
        generateVerticesAndIndices(vertices, indices);
        calculateNormals(vertices, indices);
    }




























    private void generateTree(final Vector3 cameraPos, final float detailFactor, final short maxLevels){
        //TODO: Change to length2() to save CPU
        Vector3 center = position;
        Vector3 left = position.add(-size / 2f, 0, 0);
        Vector3 top = position.add(0, 0, -size / 2f);
        Vector3 right = position.add(size / 2f, 0, 0);
        Vector3 bottom = position.add(0, 0, size / 2f);

        setHeight(center);
        setHeight(left);
        setHeight(top);
        setHeight(right);
        setHeight(bottom);

        final Float[] dists = {
            cameraPos.sub(center).length(),
            cameraPos.sub(left).length(),
            cameraPos.sub(top).length(),
            cameraPos.sub(right).length(),
            cameraPos.sub(bottom).length()
        };

        final float dist = Collections.min(Arrays.asList(dists));


        //TODO make working formula
        //int desiredLevelSquared = (int)((detailFactor * detailFactor) / distSquared);
        //int desiredLevel = (int)Math.max(maxLevels - (dist * dist / (detailFactor * 1000)), 0);
        final int desiredLevel = (int)Math.max(maxLevels - (Math.sqrt(dist) * 50/ detailFactor), 0);


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

    protected void generateVerticesAndIndices(final List<VertexPositionColorNormal> vertices, final List<Integer> indices){
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

            Vector3 color = new Vector3(0.5f);

            final int index = vertices.size();

            if(!isStitched()){
                Vector3 col1 = new Vector3((leftFrontPos.getY() + maxHeight / 2f)/ HEIGHT_FACTOR / 256f);
                Vector3 col2 = new Vector3((rightFrontPos.getY() + maxHeight / 2f)/HEIGHT_FACTOR / 256f);
                Vector3 col3 = new Vector3((leftBottomPos.getY() + maxHeight / 2f)/HEIGHT_FACTOR  / 256f);
                Vector3 col4 = new Vector3((rightBottomPos.getY() + maxHeight / 2f)/HEIGHT_FACTOR / 256f);

                vertices.add(new VertexPositionColorNormal(leftFrontPos, color));//0
                vertices.add(new VertexPositionColorNormal(rightFrontPos, color));//1
                vertices.add(new VertexPositionColorNormal(leftBottomPos, color));//2
                vertices.add(new VertexPositionColorNormal(rightBottomPos,color));//3

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



                Vector3 col0 = new Vector3((center.getY() + maxHeight / 2f) / HEIGHT_FACTOR / 256f);

                Vector3 col1 = new Vector3((leftFrontPos.getY() + maxHeight / 2f) / HEIGHT_FACTOR / 256f);
                Vector3 col2 = new Vector3((rightFrontPos.getY() + maxHeight / 2f)/HEIGHT_FACTOR / 256f);
                Vector3 col3 = new Vector3((rightBottomPos.getY() + maxHeight / 2f)/HEIGHT_FACTOR / 256f);
                Vector3 col4 = new Vector3((leftBottomPos.getY() + maxHeight / 2f) /HEIGHT_FACTOR / 256f);

                Vector3 col5 = new Vector3((frontPos.getY() + maxHeight / 2f)/ HEIGHT_FACTOR / 256f);
                Vector3 col6 = new Vector3((rightPos.getY() + maxHeight / 2f)/HEIGHT_FACTOR / 256f);
                Vector3 col7 = new Vector3((bottomPos.getY() + maxHeight / 2f)/HEIGHT_FACTOR / 256f);
                Vector3 col8 = new Vector3((leftPos.getY() + maxHeight / 2f)/HEIGHT_FACTOR / 256f);



                vertices.add(new VertexPositionColorNormal(center, color));

                vertices.add(new VertexPositionColorNormal(leftFrontPos, color));
                if(frontStitchPnt)
                    vertices.add(new VertexPositionColorNormal(frontPos, color));

                vertices.add(new VertexPositionColorNormal(rightFrontPos, color));
                if(rightStitchPnt)
                    vertices.add(new VertexPositionColorNormal(rightPos, color));

                vertices.add(new VertexPositionColorNormal(rightBottomPos, color));
                if(bottomStitchPnt)
                    vertices.add(new VertexPositionColorNormal(bottomPos, color));

                vertices.add(new VertexPositionColorNormal(leftBottomPos, color));
                if(leftStitchPnt)
                    vertices.add(new VertexPositionColorNormal(leftPos, color));


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
        int x = rootSize / 2 + (int)position.getX();
        int z = rootSize / 2 + (int)position.getZ();
        float y =  heightmap[z * hmapSize + x];// & 0x00FF;
        position.setY(- maxHeight / 2f + y * HEIGHT_FACTOR);
    }

    public int getSize(){
        return size;
    }

    public int getHmapSize(){
        return hmapSize;
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

    private void calculateNormals(List<VertexPositionColorNormal> vertices, List<Integer> indices){
        //For each triangle calculate its normal and add this to each of its vertices's normals
        for(int triangle = 0; triangle < indices.size() / 3; triangle++){
            int index0 = indices.get(triangle * 3 + 0);
            int index1 = indices.get(triangle * 3 + 1);
            int index2 = indices.get(triangle * 3 + 2);

            Vector3 sideA = vertices.get(index0).position.sub(vertices.get(index2).position);
            Vector3 sideB = vertices.get(index0).position.sub(vertices.get(index1).position);
            Vector3 normal = sideA.cross(sideB);

            vertices.get(index0).normal = vertices.get(index0).normal.add(normal);
            vertices.get(index1).normal = vertices.get(index1).normal.add(normal);
            vertices.get(index2).normal = vertices.get(index2).normal.add(normal);
        }

        //Normalize all vertices
        //TODO: do this on GPU instead to save CPU and heap?
        //TODO: if done on cpu try to reduce heap usage caused
        // by new objects craeted fro Vector3.getNormalized()
        //for(VertexPositionColorNormal vertex : vertices)
            //vertex.normal = vertex.normal.getNormalized();
    }


}