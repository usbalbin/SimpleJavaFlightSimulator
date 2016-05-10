package se.liu.ida.albhe417.tddd78.game.terrain;

import se.liu.ida.albhe417.tddd78.game.Settings;
import se.liu.ida.albhe417.tddd78.game.graphics.VertexPositionColorNormal;
import se.liu.ida.albhe417.tddd78.math.FloatMapper;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.ida.albhe417.tddd78.math.Vector4;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * QuadTree contains a quad tree for effectively holding the graphic mesh of objects like the TerrainLOD.
 *
 * The tree structure is perfect for recursively dividing the terrain into parts with different levels of detail.
 */
class QuadTree_procedural
{

    private Settings settings;
    private Heightmap_completely_procedural heightmap;
    private FloatMapper floatMapper;

    private int detailFactor;
    private int maxLevels;
    private boolean isThreaded;
    private Vector3 cameraPos = null;
    private Matrix4x4 modelViewProjectionMatrix = null;
    private ForkJoinPool workerPool = null;

    private Node rootNode;


    /**
     * Create Root QuadTree
     * @param heightmap heightmap
     */
    QuadTree_procedural(Heightmap_completely_procedural heightmap, Settings settings){
        this.heightmap = heightmap;
        this.rootNode = new Node(new Vector3(0, 0, 0), 4097, (short)0, null);
        this.workerPool = new ForkJoinPool();
        this.settings = settings;
        this.floatMapper = new FloatMapper(heightmap.minHeight, heightmap.maxHeight, 0, 100);
    }

    public void update(Vector3 cameraPosition, Matrix4x4 modelViewProjectionMatrix, List<VertexPositionColorNormal> vertices, List<Integer> indices){
        this.cameraPos = cameraPosition;
        this.modelViewProjectionMatrix = modelViewProjectionMatrix;
        this.detailFactor = settings.getDetailFactor();
        this.maxLevels = Integer.numberOfTrailingZeros(4097 & (~1));
        this.isThreaded = settings.isThreaded();

        for(int i = 0; i < cameraPosition.values.length; i++)
            this.rootNode.position.values[i] = cameraPosition.values[i];


        if(isThreaded) {
            workerPool.invoke(rootNode);

            rootNode.reinitialize();
        }
        else
            rootNode.compute();

        rootNode.stitch(null, null, null, null);

        Map<Vector3, Integer> positionMap = new HashMap<>();
        rootNode.generateVerticesAndIndices(vertices, indices, positionMap);

    }

	/**
     * Node is the actual nodes making up the tree. Once the tree itself is generated the nodes can be used to generate the
     * terrain mesh's vertices and indices.
     *
     * Node extends RecursiveAction, thus it has the ability to do some of its processing in parallel using ForkJoin
     */
    private final class Node extends RecursiveAction{

        private Node leftFront = null;
        private Node rightFront = null;
        private Node leftBottom = null;
        private Node rightBottom = null;
        private Node parent = null;

        private boolean leftStitchPnt = false;
        private boolean frontStitchPnt = false;
        private boolean rightStitchPnt = false;
        private boolean bottomStitchPnt = false;


        private Vector3 position;
        private int size;
        private final short level;

        /**
         * Create sub node
         *
         * @param position center position of node
         * @param size nodes size
         * @param level nodes level, 0 means root higher means smaller node
         * @param parent direct parent of this node
         */
        private Node(final Vector3 position, final int size, final short level, Node parent) {
            this.position = position;
            this.size = size;
            this.level = level;
            this.parent = parent;
        }

        protected void compute(){
            final int halfSize = size / 2;
            leftStitchPnt = false;
            frontStitchPnt = false;
            rightStitchPnt = false;
            bottomStitchPnt = false;

            heightmap.getHeight(position);

            float halfHeightExtent = size * heightmap.heightFactor;
            float radius = (float)Math.sqrt(2 * halfSize * halfSize + halfHeightExtent * halfHeightExtent);
            final float distance = Math.max(
                cameraPos.sub(position).length() - radius,
                0
            );

            final int desiredLevel = (int)Math.max(maxLevels - (Math.sqrt(distance) * 50/ detailFactor), 0);


            if(desiredLevel <= level || level >= maxLevels || (level > maxLevels / 2 && !inView())) {
                leftFront = null;
                rightFront = null;
                rightBottom = null;
                leftBottom = null;
                return;
            }

            final int halfChildSize = halfSize / 2;
            final short childLevel = (short)(level + 1);

            if(!hasChildren()) {
                leftFront = new Node(position.add(new Vector3(-halfChildSize, 0, -halfChildSize)), halfSize, childLevel, this);
                rightFront = new Node(position.add(new Vector3(+halfChildSize, 0, -halfChildSize)), halfSize, childLevel, this);
                leftBottom = new Node(position.add(new Vector3(-halfChildSize, 0, +halfChildSize)), halfSize, childLevel, this);
                rightBottom = new Node(position.add(new Vector3(+halfChildSize, 0, +halfChildSize)), halfSize, childLevel, this);
            }else{
                leftFront.position = position.add(new Vector3(-halfChildSize, 0, -halfChildSize));
                rightFront.position = position.add(new Vector3(+halfChildSize, 0, -halfChildSize));
                leftBottom.position = position.add(new Vector3(-halfChildSize, 0, +halfChildSize));
                rightBottom.position = position.add(new Vector3(+halfChildSize, 0, +halfChildSize));

                if(isThreaded) {
                    leftFront.reinitialize();
                    rightFront.reinitialize();
                    leftBottom.reinitialize();
                }
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
         * Makes sure that adjacent quads have same LOD at their common border
         *
         * @param neighborLeft left neighbor
         * @param neighborFront front neighbor
         * @param neighborRight right neighbor
         * @param neighborBottom neighbor bottom
         */
        private void stitch(Node neighborLeft, Node neighborFront, Node neighborRight, Node neighborBottom){
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

        private void stitchLeft(final Node leftNeighbor){
            //If our neighbor has children then either the matching quad has same(do nothing) or higher
            // LOD-level(neighbor will do the stitching)
            if(leftNeighbor.hasChildren())
                return;
            leftNeighbor.addRightStitchPnt();
        }

        private void stitchFront(final Node frontNeighbor){
            //If our neighbor has children then either the matching quad has same(do nothing) or higher
            // LOD-level(neighbor will do the stitching)
            if(frontNeighbor.hasChildren())
                return;
            frontNeighbor.addBottomStitchPnt();
        }

        private void stitchRight(final Node rightNeighbor){
            //If our neighbor has children then either the matching quad has same(do nothing) or higher
            // LOD-level(neighbor will do the stitching)
            if(rightNeighbor.hasChildren())
                return;
            rightNeighbor.addLeftStitchPnt();
        }

        private void stitchBottom(final Node bottomNeighbor){
            //If our neighbor has children then either the matching quad has same(do nothing) or higher
            // LOD-level(neighbor will do the stitching)
            if(bottomNeighbor.hasChildren())
                return;
            bottomNeighbor.addFrontStitchPnt();
        }

        /**
         * Generates vertices, indices and positionMap for every leaf in the tree
         *
         * @param vertices Resulting list of vertices
         * @param indices Resulting list of indices linking vertices into triangles
         * @param positionMap Results in a map between every vertex-position and its index in the vertices-list for fast check of duplicates
         */
        private void generateVerticesAndIndices(final List<VertexPositionColorNormal> vertices, final List<Integer> indices, Map<Vector3, Integer> positionMap){
            if(hasChildren()){
                leftFront.generateVerticesAndIndices(vertices, indices, positionMap);
                rightFront.generateVerticesAndIndices(vertices, indices, positionMap);
                leftBottom.generateVerticesAndIndices(vertices, indices, positionMap);
                rightBottom.generateVerticesAndIndices(vertices, indices, positionMap);
                return;
            }

            if(isStitched()){
                genVerticesIndicesStitched(vertices, indices, positionMap);
            }else{
                genVerticesIndicesNonStitched(vertices, indices, positionMap);
            }
        }

        private void genVerticesIndicesNonStitched(final List<VertexPositionColorNormal> vertices, final Collection<Integer> indices, Map<Vector3, Integer> positionMap){
            int halfSide = size / 2;

            Vector3 leftFrontPos = position.add(-halfSide, 0, -halfSide);
            Vector3 rightFrontPos = position.add(+halfSide, 0, -halfSide);
            Vector3 leftBottomPos = position.add(-halfSide, 0, +halfSide);
            Vector3 rightBottomPos = position.add(+halfSide, 0, +halfSide);


            Vector3 color = calculateColor(position);

            Integer leftFrontIndex = addVertexGetIndex(leftFrontPos, color, vertices, positionMap);

            Integer rightFrontIndex = addVertexGetIndex(rightFrontPos, color, vertices, positionMap);

            Integer leftBottomIndex = addVertexGetIndex(leftBottomPos, color, vertices, positionMap);

            Integer rightBottomIndex = addVertexGetIndex(rightBottomPos, color, vertices, positionMap);

            indices.add(leftFrontIndex);indices.add(rightBottomIndex);indices.add(leftBottomIndex);
            indices.add(leftFrontIndex);indices.add(rightFrontIndex);indices.add(rightBottomIndex);
        }

        private void genVerticesIndicesStitched(final List<VertexPositionColorNormal> vertices, final Collection<Integer> indices, Map<Vector3, Integer> positionMap){
            int halfSide = size / 2;

            Vector3 center = position;
            Vector3 leftFrontPos = position.add(-halfSide, 0, -halfSide);
            Vector3 rightFrontPos = position.add(+halfSide, 0, -halfSide);
            Vector3 leftBottomPos = position.add(-halfSide, 0, +halfSide);
            Vector3 rightBottomPos = position.add(+halfSide, 0, +halfSide);

            Vector3 frontPos = position.add(0, 0, -halfSide);
            Vector3 rightPos = position.add(+halfSide, 0, 0);
            Vector3 bottomPos = position.add(0, 0, +halfSide);
            Vector3 leftPos = position.add(-halfSide, 0, 0);

            Vector3[] corners = new Vector3[]{leftFrontPos, rightFrontPos, rightBottomPos, leftBottomPos};
            Vector3[] stitchPoss = new Vector3[]{frontPos, rightPos, bottomPos, leftPos};
            boolean[] stitchPoints = new boolean[]{frontStitchPnt, rightStitchPnt, bottomStitchPnt, leftStitchPnt};

            final Vector3 color = calculateColor(position);
            final int numVertices = 5 + numStitchPoints();

            int i = 0;
            Integer[] quadsIndices = new Integer[numVertices];

            quadsIndices[i] = vertices.size();
            vertices.add(new VertexPositionColorNormal(center, color)); i++;


            for(int j = 0; j < 4; j++){
                addVertexIndex(corners[j], color, i, vertices, quadsIndices, positionMap);
                i++;

                if(stitchPoints[j]) {
                    addVertexIndex(stitchPoss[j], color, i, vertices, quadsIndices, positionMap);
                    i++;
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


        private void addVertexIndex(Vector3 position, Vector3 color, int currentIndex, List<VertexPositionColorNormal> vertices, Integer[] quadsIndices, Map<Vector3, Integer> positionMap){
            quadsIndices[currentIndex] = positionMap.get(position);
            if(quadsIndices[currentIndex] == null) {
                quadsIndices[currentIndex] = vertices.size();
                vertices.add(new VertexPositionColorNormal(position, color));
                positionMap.put(position, quadsIndices[currentIndex]);
            }
        }

        private int addVertexGetIndex(Vector3 position, Vector3 color, List<VertexPositionColorNormal> vertices, Map<Vector3, Integer> positionMap){
            Integer currentIndex = positionMap.get(position);
            if(currentIndex == null){
                currentIndex = vertices.size();
                vertices.add(new VertexPositionColorNormal(position, color));
                positionMap.put(position, currentIndex);
            }

            return currentIndex;
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

        /**
         * Traverse tree to find node at position
         * @param position The position to find
         * @return found node or null if it can not be found
         */
        private Node findNode(final Vector3 position){
            //Recursion seems like the obvious choice over any kind of looping solution

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

        /**
         * Check if this node is inside of the camera view
         * @return whether the node is inside
         */
        private boolean inView() {

            Vector4[] corners = calculateTransformedAABB();

            for (int i = 0; i < 3; i++) {
                final int screenBorderMax = +1;
                final int screenBorderMin = -1;
                boolean cornerInside;

                cornerInside = false;
                for (Vector4 corner: corners) {
                    if(corner.values[i] < screenBorderMax) {
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
                    if(corner.values[i] > screenBorderMin){
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

        private Vector4[] calculateTransformedAABB(){
            int halfSize = size / 2;


            float halfHeightExtent = halfSize * heightmap.heightFactor;
            float maxHeight = position.getY() + halfHeightExtent;
            float minHeight = position.getY() - halfHeightExtent;



            Vector4 centerZeroHeight = new Vector4(position, 1);
            centerZeroHeight.setY(0);

            //Create axis aligned bounding box for this node
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

            //Transform bounding box into screen space
            for (int i = 0; i < corners.length; i++){
                corners[i] = modelViewProjectionMatrix.multiply(corners[i]);
                corners[i] = corners[i].divide(corners[i].getW());//Compensate for things getting smaller farther away
            }

            return corners;
        }

        /**
         * Calculate color for this position
         * TODO: optimize! This method causes a lot of overhead. Move to GPU?
         *
         * @param position the position
         * @return resulting color
         */
        private Vector3 calculateColor(Vector3 position){
            return new Vector3(-1, -1, -1);
        }
    }


}