package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

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

    //TODO replace with bitfield to save space
    private boolean leftStitchPnt = false;
    private boolean frontStitchPnt = false;
    private boolean rightStitchPnt = false;
    private boolean bottomStitchPnt = false;

    //TODO: Check need for precision
    private Vector3 position;
    private int size;
    private short level;

    private Vector3[][] heightmap;//Only for root quad

    /**
     * Create Root QuadTree
     * @param fileNameHeightmap heightmap
     */
    public QuadTree(String fileNameHeightmap){
        heightmap = Helpers.imageToColors(fileNameHeightmap);
        this.size = Math.min(heightmap.length, heightmap[0].length);
        this.position = new Vector3(size / 2, 0, -size / 2);
        this.level = 0;
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
    private QuadTree(Vector3 position, int size, short level, Vector3 cameraPos, float detailFactor, short maxLevels) {
        this.position = position;
        this.size = size;
        this.level = level;
        generateTree(cameraPos, detailFactor, maxLevels);
    }

    private void generateTree(Vector3 cameraPos, float detailFactor, short maxLevels){
        float distSquared = cameraPos.sub(position).length2();
        float distLimit = detailFactor * level;
        if(distSquared < distLimit * distLimit && level < maxLevels){
            final int childSize = size / 2;
            final int halfChildSize = childSize / 2;
            final short childLevel = (short)(1 + level);

            leftFront = new QuadTree(position.add(new Vector3(-halfChildSize, 0, -halfChildSize)), childSize, childLevel, cameraPos, detailFactor, maxLevels);
            rightFront = new QuadTree(position.add(new Vector3(+halfChildSize, 0, -halfChildSize)), childSize, childLevel, cameraPos, detailFactor, maxLevels);
            leftBottom = new QuadTree(position.add(new Vector3(-halfChildSize, 0, +halfChildSize)), childSize, childLevel, cameraPos, detailFactor, maxLevels);
            rightBottom = new QuadTree(position.add(new Vector3(+halfChildSize, 0, +halfChildSize)), childSize, childLevel, cameraPos, detailFactor, maxLevels);
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

    public void prepForDraw(Vector3[][] heightmap){
        LinkedList<VertexPositionColor> vertices = new LinkedList<>();
        LinkedList<Integer> indices = new LinkedList<>();

        stitch(null, null, null, null);
        generateVerticesAndIndices(vertices, indices, heightmap);

    }

    /**
     * Make sure that adjacent quads have same LOD at their common border
     */
    protected void stitch(QuadTree neighborLeft, QuadTree neighborFront, QuadTree neighborRight, QuadTree neighborBottom){
        //Stuff....
        //..
        //..
        if(hasChildren()){
            leftFront.stitch(neighborLeft, neighborFront, null, null);
            rightFront.stitch(null, neighborFront, neighborRight, null);
            leftBottom.stitch(neighborLeft, null, null, neighborBottom);
            rightBottom.stitch(null, null, neighborRight, neighborBottom);

        }
        else {
            QuadTree[] neighbors = {neighborLeft, neighborFront, neighborRight, neighborBottom};
            if(neighborLeft != null)
                stitchLeft(neighborLeft);
            if(neighborFront != null)
                stitchFront(neighborFront);
            if(neighborRight != null)
                stitchRight(neighborRight);
            if(neighborBottom != null)
                stitchBottom(neighborBottom);
        }
    }

    //TODO: reuse code better
    private void stitchLeft(QuadTree leftNeighbor){
        //If our neighbor has children then either the matching quad has same(do nothing) or higher
        // LOD-level(neighbor will do the stitching)
        if(leftNeighbor.hasChildren())
            return;
        leftNeighbor.addRightStitchPnt();
    }

    private void stitchFront(QuadTree frontNeighbor){
        //If our neighbor has children then either the matching quad has same(do nothing) or higher
        // LOD-level(neighbor will do the stitching)
        if(frontNeighbor.hasChildren())
            return;
        frontNeighbor.addBottomStitchPnt();
    }

    private void stitchRight(QuadTree rightNeighbor){
        //If our neighbor has children then either the matching quad has same(do nothing) or higher
        // LOD-level(neighbor will do the stitching)
        if(rightNeighbor.hasChildren())
            return;
        rightNeighbor.addLeftStitchPnt();
    }

    private void stitchBottom(QuadTree bottomNeighbor){
        //If our neighbor has children then either the matching quad has same(do nothing) or higher
        // LOD-level(neighbor will do the stitching)
        if(bottomNeighbor.hasChildren())
            return;
        bottomNeighbor.addFrontStitchPnt();
    }

    protected void generateVerticesAndIndices(List<VertexPositionColor> vertices, List<Integer> indices, Vector3[][] heightmap){
        if(hasChildren()){
            leftFront.generateVerticesAndIndices(vertices, indices, heightmap);
            rightFront.generateVerticesAndIndices(vertices, indices, heightmap);
            leftBottom.generateVerticesAndIndices(vertices, indices, heightmap);
            rightBottom.generateVerticesAndIndices(vertices, indices, heightmap);
        }

        else{
            float halfSide = size / 2;
            Vector3 center = position;
            setHeight(center, heightmap);

            Vector3 leftPos = position.add(-halfSide, 0, 0);
            Vector3 frontPos = position.add(0, 0, -halfSide);
            Vector3 rightPos = position.add(+halfSide, 0, 0);
            Vector3 bottomPos = position.add(0, 0, +halfSide);

            Vector3 leftFrontPos = position.add(-halfSide, 0, -halfSide);
            Vector3 rightFrontPos = position.add(+halfSide, 0, -halfSide);
            Vector3 leftBottomPos = position.add(-halfSide, 0, +halfSide);
            Vector3 rightBottomPos = position.add(+halfSide, 0, +halfSide);

            setHeight(leftPos, heightmap);
            setHeight(frontPos, heightmap);
            setHeight(rightPos, heightmap);
            setHeight(bottomPos, heightmap);

            setHeight(leftFrontPos, heightmap);
            setHeight(rightFrontPos, heightmap);
            setHeight(leftBottomPos, heightmap);
            setHeight(rightBottomPos, heightmap);



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
                //TODO: add predefined size to aprx 6 or something
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

    protected int numStitchPnts(){
        return (leftStitchPnt ? 1 : 0) + (frontStitchPnt ? 1 : 0) + (rightStitchPnt ? 1 : 0) + (bottomStitchPnt ? 1 : 0);
    }

    private void setHeight(Vector3 position, Vector3[][] heightmap){
        int x = (int)position.getX();
        int z = (int)position.getZ();
        float y = heightmap[(int)z][(int)x].getY();
        position.setY(y);
    }
}