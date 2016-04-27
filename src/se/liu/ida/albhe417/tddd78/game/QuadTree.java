package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.ida.albhe417.tddd78.math.Vector4;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Project TDDD78
 *
 * File created by Albin on 2016-03-14.
 */
class QuadTree extends RecursiveAction{

    private QuadTree leftFront;
    private QuadTree rightFront;
    private QuadTree leftBottom;
    private QuadTree rightBottom;
    private QuadTree parent;

    private boolean leftStitchPnt = false;
    private boolean frontStitchPnt = false;
    private boolean rightStitchPnt = false;
    private boolean bottomStitchPnt = false;

    //TODO: Check need for precision
    private final Vector3 position;
    private int size;
    private final short level;

    private static Settings settings;
    private static Heightmap heightmap;
    private static Map<Vector3, Integer> positionMap;

    private static int detailFactor;
    private static int maxLevels;
    private static boolean isThreaded;
    private static Vector3 cameraPos;
    private static Matrix4x4 MVPMatrix;
    private static ForkJoinPool workerPool;


    /**
     * Create Root QuadTree
     * @param heightmap heightmap
     */
    public QuadTree(Heightmap heightmap, Settings settings){
        QuadTree.heightmap = heightmap;
        this.size = heightmap.SIZE;
        this.position = new Vector3(0, 0, 0);
        this.level = 0;
        workerPool = new ForkJoinPool();
        positionMap = new HashMap<>();
        QuadTree.settings = settings;
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
    private QuadTree(final Vector3 position, final int size, final short level, QuadTree parent) {
        this.position = position;
        this.size = size;
        this.level = level;
        this.parent = parent;
    }

    public void update(Vector3 cameraPosition, Matrix4x4 MVPMatrix, List<VertexPositionColorNormal> vertices, List<Integer> indices){
        QuadTree.positionMap.clear();
        QuadTree.cameraPos = cameraPosition;
        QuadTree.MVPMatrix = MVPMatrix;
        QuadTree.detailFactor = settings.getDetailFactor();
        QuadTree.maxLevels = Integer.numberOfTrailingZeros(size ^ 1);
        QuadTree.isThreaded = settings.isThreaded();

        if(isThreaded) {
            workerPool.invoke(this);

            this.reinitialize();
        }
        else
            compute();

        stitch(null, null, null, null);

        generateVerticesAndIndices(vertices, indices);

        calculateNormals(vertices, indices);

    }








    protected void compute(){
        final int halfSize = size / 2;
        leftStitchPnt = frontStitchPnt = rightStitchPnt = bottomStitchPnt = false;


        Vector3 center = position;
        Vector3 left = position.add(-halfSize, 0, 0);
        Vector3 top = position.add(0, 0, -halfSize);
        Vector3 right = position.add(halfSize, 0, 0);
        Vector3 bottom = position.add(0, 0, halfSize);

        heightmap.getHeight(center);
        heightmap.getHeight(left);
        heightmap.getHeight(top);
        heightmap.getHeight(right);
        heightmap.getHeight(bottom);

        //TODO: Change to length2() to save CPU
        final Float[] distances = {
            cameraPos.sub(center).length2(),
            cameraPos.sub(left).length2(),
            cameraPos.sub(top).length2(),
            cameraPos.sub(right).length2(),
            cameraPos.sub(bottom).length2()
        };

        final float dist = (float)Math.sqrt(Collections.min(Arrays.asList(distances)));


        //TODO make working formula
        //int desiredLevelSquared = (int)((detailFactor * detailFactor) / distSquared);
        //int desiredLevel = (int)Math.max(maxLevels - (dist * dist / (detailFactor * 1000)), 0);
        final int desiredLevel = (int)Math.max(maxLevels - (Math.sqrt(dist) * 50/ detailFactor), 0);


        if(desiredLevel <= level || level >= maxLevels || !inView(center, MVPMatrix)) {
            leftFront = rightFront = rightBottom = leftBottom = null;
            return;
        }

        final int halfChildSize = halfSize / 2;
        final short childLevel = (short)(level + 1);

        if(!hasChildren()) {
            leftFront = new QuadTree(position.add(new Vector3(-halfChildSize, 0, -halfChildSize)), halfSize, childLevel, this);
            rightFront = new QuadTree(position.add(new Vector3(+halfChildSize, 0, -halfChildSize)), halfSize, childLevel, this);
            leftBottom = new QuadTree(position.add(new Vector3(-halfChildSize, 0, +halfChildSize)), halfSize, childLevel, this);
            rightBottom = new QuadTree(position.add(new Vector3(+halfChildSize, 0, +halfChildSize)), halfSize, childLevel, this);
        }else if(isThreaded){
            leftFront.reinitialize();
            rightFront.reinitialize();
            leftBottom.reinitialize();
        }

        if(isThreaded) {
            //Start child processes
            leftFront.fork();
            rightFront.fork();
            leftBottom.fork();

            //Run last computation self
            rightBottom.compute();

            //Wait for child threads to finish
            leftFront.join();
            rightFront.join();
            leftBottom.join();

        }else {
            leftFront.compute();
            rightFront.compute();
            leftBottom.compute();
            rightBottom.compute();
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
    private void stitch(QuadTree neighborLeft, QuadTree neighborFront, QuadTree neighborRight, QuadTree neighborBottom){
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

    private void generateVerticesAndIndices(final List<VertexPositionColorNormal> vertices, final List<Integer> indices){
        if(hasChildren()){
            leftFront.generateVerticesAndIndices(vertices, indices);
            rightFront.generateVerticesAndIndices(vertices, indices);
            leftBottom.generateVerticesAndIndices(vertices, indices);
            rightBottom.generateVerticesAndIndices(vertices, indices);
        }

        else{
            int halfSide = size / 2;

            Vector3 leftFrontPos = position.add(-halfSide, 0, -halfSide);
            Vector3 rightFrontPos = position.add(+halfSide, 0, -halfSide);
            Vector3 leftBottomPos = position.add(-halfSide, 0, +halfSide);
            Vector3 rightBottomPos = position.add(+halfSide, 0, +halfSide);



            heightmap.getHeight(leftFrontPos);
            heightmap.getHeight(rightFrontPos);
            heightmap.getHeight(leftBottomPos);
            heightmap.getHeight(rightBottomPos);

            Vector3 color = calculateColor(position);


            if(!isStitched()){

                Integer leftFrontIndex = positionMap.get(leftFrontPos);
                if(leftFrontIndex == null) {
                    leftFrontIndex = vertices.size();
                    vertices.add(new VertexPositionColorNormal(leftFrontPos, color));//0
                    positionMap.put(leftFrontPos, leftFrontIndex);
                }

                Integer rightFrontIndex = positionMap.get(rightFrontPos);
                if(rightFrontIndex == null) {
                    rightFrontIndex = vertices.size();
                    vertices.add(new VertexPositionColorNormal(rightFrontPos, color));//1
                    positionMap.put(rightFrontPos, rightFrontIndex);
                }

                Integer leftBottomIndex = positionMap.get(leftBottomPos);
                if(leftBottomIndex == null) {
                    leftBottomIndex = vertices.size();
                    vertices.add(new VertexPositionColorNormal(leftBottomPos, color));//2
                    positionMap.put(leftBottomPos, leftBottomIndex);
                }

                Integer rightBottomIndex = positionMap.get(rightBottomPos);
                if(rightBottomIndex == null) {
                    rightBottomIndex = vertices.size();
                    vertices.add(new VertexPositionColorNormal(rightBottomPos, color));//3
                    positionMap.put(rightBottomPos, rightBottomIndex);
                }

                indices.add(leftFrontIndex);indices.add(rightBottomIndex);indices.add(leftBottomIndex);
                indices.add(leftFrontIndex);indices.add(rightFrontIndex);indices.add(rightBottomIndex);
            }else{
                final int numVertices = 5 + numStitchPoints();

                Vector3 center = position;
                heightmap.getHeight(center);
                Vector3 leftPos = position.add(-halfSide, 0, 0);
                Vector3 frontPos = position.add(0, 0, -halfSide);
                Vector3 rightPos = position.add(+halfSide, 0, 0);
                Vector3 bottomPos = position.add(0, 0, +halfSide);

                heightmap.getHeight(leftPos);
                heightmap.getHeight(frontPos);
                heightmap.getHeight(rightPos);
                heightmap.getHeight(bottomPos);


                int i = 0;
                Integer[] quadsIndices = new Integer[numVertices];

                quadsIndices[i] = vertices.size();
                vertices.add(new VertexPositionColorNormal(center, color));

                quadsIndices[++i] = positionMap.get(leftFrontPos);
                if(quadsIndices[i] == null) {
                    quadsIndices[i] = vertices.size();
                    vertices.add(new VertexPositionColorNormal(leftFrontPos, color));
                    positionMap.put(leftFrontPos, quadsIndices[i]);
                }

                if(frontStitchPnt) {
                    quadsIndices[++i] = positionMap.get(frontPos);
                    if(quadsIndices[i] == null) {
                        quadsIndices[i] = vertices.size();
                        vertices.add(new VertexPositionColorNormal(frontPos, color));
                        positionMap.put(frontPos, quadsIndices[i]);
                    }
                }

                quadsIndices[++i] = positionMap.get(rightFrontPos);
                if(quadsIndices[i] == null) {
                    quadsIndices[i] = vertices.size();
                    vertices.add(new VertexPositionColorNormal(rightFrontPos, color));
                    positionMap.put(rightFrontPos, quadsIndices[i]);
                }
                if(rightStitchPnt) {
                    quadsIndices[++i] = positionMap.get(rightPos);
                    if(quadsIndices[i] == null) {
                        quadsIndices[i] = vertices.size();
                        vertices.add(new VertexPositionColorNormal(rightPos, color));
                        positionMap.put(rightPos, quadsIndices[i]);
                    }
                }

                quadsIndices[++i] = positionMap.get(rightBottomPos);
                if(quadsIndices[i] == null) {
                    quadsIndices[i] = vertices.size();
                    vertices.add(new VertexPositionColorNormal(rightBottomPos, color));
                    positionMap.put(rightBottomPos, quadsIndices[i]);
                }
                if(bottomStitchPnt) {
                    quadsIndices[++i] = positionMap.get(bottomPos);
                    if(quadsIndices[i] == null) {
                        quadsIndices[i] = vertices.size();
                        vertices.add(new VertexPositionColorNormal(bottomPos, color));
                        positionMap.put(bottomPos, quadsIndices[i]);
                    }
                }

                quadsIndices[++i] = positionMap.get(leftBottomPos);
                if(quadsIndices[i] == null) {
                    quadsIndices[i] = vertices.size();
                    vertices.add(new VertexPositionColorNormal(leftBottomPos, color));
                    positionMap.put(leftBottomPos, quadsIndices[i]);
                }
                if(leftStitchPnt) {
                    quadsIndices[++i] = positionMap.get(leftPos);
                    if(quadsIndices[i] == null) {
                        quadsIndices[i] = vertices.size();
                        vertices.add(new VertexPositionColorNormal(leftPos, color));
                        positionMap.put(leftPos, quadsIndices[i]);
                    }
                }

                indices.add(quadsIndices[numVertices - 1]);   //last vertex
                indices.add(quadsIndices[1]);         //First vertex
                indices.add(quadsIndices[0]);         //Center

                for(int j = 0; j < numVertices - 1; j++){
                    indices.add(quadsIndices[j]);
                    indices.add(quadsIndices[j + 1]);
                    indices.add(quadsIndices[0]);
                }
            }
        }
    }

    private void addLeftStitchPnt(){
        leftStitchPnt = true;
    }

    private void addFrontStitchPnt(){
        frontStitchPnt = true;
    }

    private void addRightStitchPnt(){
        rightStitchPnt = true;
    }

    private void addBottomStitchPnt(){
        bottomStitchPnt = true;
    }

    private boolean isStitched(){
        return leftStitchPnt || frontStitchPnt || rightStitchPnt || bottomStitchPnt;
    }

    private boolean isRoot(){
        return level == 0;
    }

    private int numStitchPoints(){
        return (leftStitchPnt ? 1 : 0) + (frontStitchPnt ? 1 : 0) + (rightStitchPnt ? 1 : 0) + (bottomStitchPnt ? 1 : 0);
    }

    //TODO decide which is faster this one or the one with the ugly name. If this one wins, remove root-field
    private QuadTree findNode(final Vector3 position){
        final int dx = Math.round(position.getX() - this.position.getX());
        final int dz = Math.round(position.getZ() - this.position.getZ());

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

    private void calculateNormals(List<VertexPositionColorNormal> vertices, List<Integer> indices){
        //For each triangle calculate its normal and add this to each of its vertices's normals
        for(int triangle = 0; triangle < indices.size() / 3; triangle++){
            int index0 = indices.get(triangle * 3);
            int index1 = indices.get(triangle * 3 + 1);
            int index2 = indices.get(triangle * 3 + 2);

            Vector3 sideA = vertices.get(index0).position.sub(vertices.get(index2).position);
            Vector3 sideB = vertices.get(index0).position.sub(vertices.get(index1).position);
            Vector3 normal = sideA.cross(sideB);

            vertices.get(index0).normal.increase(normal);
            vertices.get(index1).normal.increase(normal);
            vertices.get(index2).normal.increase(normal);
        }

    }

    private boolean inView(Vector3 center, Matrix4x4 MVPMatrix) {
        int halfSize = size / 2;
        Vector3 frontLeft = position.add(-halfSize, 0, -halfSize);
        Vector3 frontRight = position.add(0, 0, -halfSize);
        Vector3 bottomRight = position.add(halfSize, 0, halfSize);
        Vector3 bottomLeft = position.add(-halfSize, 0, halfSize);

        heightmap.getHeight(frontLeft);
        heightmap.getHeight(frontRight);
        heightmap.getHeight(bottomRight);
        heightmap.getHeight(bottomLeft);

        float maxHeight = center.getY();
        float minHeight = center.getY();

        float[] heights = {frontLeft.getY(), frontRight.getY(), bottomRight.getY(), bottomLeft.getY()};

        for (float height : heights) {
            if (height > maxHeight)
                maxHeight = height;
            else if (height < minHeight)
                minHeight = height;
        }

        minHeight -= 50;
        maxHeight += 50;

        Vector4 centerZeroHeight = new Vector4(center, 1);
        centerZeroHeight.setY(0);

        //Corners of quads collision box
        Vector4 leftBottomFront = centerZeroHeight.add(-halfSize, minHeight, -halfSize, 1);
        Vector4 rightBottomFront = centerZeroHeight.add(halfSize, minHeight, -halfSize, 1);
        Vector4 rightBottomBack = centerZeroHeight.add(halfSize, minHeight, halfSize, 1);
        Vector4 leftBottomBack = centerZeroHeight.add(-halfSize, minHeight, halfSize, 1);

        Vector4 leftTopFront = centerZeroHeight.add(-halfSize, maxHeight, -halfSize, 1);
        Vector4 rightTopFront = centerZeroHeight.add(halfSize, maxHeight, -halfSize, 1);
        Vector4 rightTopBack = centerZeroHeight.add(halfSize, maxHeight, halfSize, 1);
        Vector4 leftTopBack = centerZeroHeight.add(-halfSize, maxHeight, halfSize, 1);

        Vector4[] corners = {
                leftBottomFront, rightBottomFront, rightBottomBack, leftBottomBack,
                leftTopFront, rightTopFront, rightTopBack, leftTopBack
        };

        for (int i = 0; i < corners.length; i++){
            corners[i] = MVPMatrix.multiply(corners[i]);
            corners[i] = corners[i].divide(corners[i].getW());//Compensate for things getting smaller farther away
        }

        for (int i = 0; i < 3; i++) {
            boolean cornerInside;

            cornerInside = false;
            for (Vector4 corner: corners) {
                if(corner.values[i] < +1) {
                    cornerInside = true;
                    break;
                }
            }

            if(!cornerInside)
                //All corners are outside view, no intersection
                return false;

            //

            cornerInside = false;
            for (Vector4 corner: corners) {
                if(corner.values[i] > -1){
                    cornerInside = true;
                    break;
                }
            }

            if(!cornerInside)
                //All corners are outside view, no intersection
                return false;
        }

        //All corners are inside some sides, thus box is either intersecting or completely inside the frustum
        return true;
    }

    private Vector3 calculateColor(Vector3 position){
        final Vector3 snow = Vector3.createColor(0xEE, 0xEE, 0xEE);
        final Vector3 rock = Vector3.createColor(0x88, 0x88, 0x55);
        final Vector3 grass = Vector3.createColor(0x00, 0x77, 0x00);
        final Vector3 water = Vector3.createColor(0x00, 0x00, 0x77);

        final float snowRockLine = 75;
        final float rockGrassLine = 30;
        final float grassWaterLine = 4;

        float percentOfMaxHeight = Helpers.map(
            position.getY() + heightmap.MAX_HEIGHT / 2.0f,
            heightmap.MIN_HEIGHT, heightmap.MAX_HEIGHT,
            0, 100
        );

        if(percentOfMaxHeight > snowRockLine)
            return snow;
        else if(percentOfMaxHeight > rockGrassLine)
            return rock;
        else if(percentOfMaxHeight > grassWaterLine)
            return grass;
        else
            return water;

    }
}