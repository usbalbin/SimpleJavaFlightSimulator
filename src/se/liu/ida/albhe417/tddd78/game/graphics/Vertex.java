package se.liu.ida.albhe417.tddd78.game.graphics;

/**
 * Vertex is interface for representing a vertex.
 * A bunch of these linked together into triangles are useful for representing 3D objects as meshes.
 */
public interface Vertex {
    int getFloatCount();

    float[] getFloats();

    //Usable if decide to implement VertexPositionTextureNormal or any other alternative vertex type in future
    void enableVertexAttributes();
    void setupVertexAttributes();
}
