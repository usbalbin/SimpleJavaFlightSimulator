package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;

/**
 * Created by Albin on 09/03/2016.
 */
public class Terrain extends AbstractDrawable{
    VertexPositionColor[] vertices;
    protected int width;
    private int height;

    public Terrain(Vector3 position, final int shaderProgram){
        super(position, 0);
        setup(shaderProgram);
    }

    protected void setup(final int shaderProgram){
        Vector3[][] colors = Helpers.imageToColors("content/heightmap.png");
        width = colors[0].length;
        height = colors.length;

        final Vector3 color = new Vector3(0.5f, 0.5f, 0.5f);

        //Setup vertices
        vertices = new VertexPositionColor[height * width];
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                float y = colors[z][x].getX() * 10.0f;

                Vector3 position = new Vector3(x, y, z);
                vertices[z * width + x] = new VertexPositionColor(position, new Vector3(position.getY() / 20f, position.getY() / 20f, position.getY() / 20f));
            }
        }

        //Setup indices
        int[] indices = new int[(width - 1) * (height - 1) * 6];

        int i = 0;
        for (int z = 0; z < height - 1; z++) {
            for (int x = 0; x < width - 1; x++) {
                int topLeft =       x  + (z + 1) * width;
                int topRight = (x + 1) + (z + 1) * width;
                int botLeft  =		x  +      z  * width;
                int botRight = (x + 1) +	  z  * width;

                indices[i++] = topLeft;//Indices for lower left triangle
                indices[i++] = botRight;
                indices[i++] = botLeft;

                indices[i++] = topLeft;//Indices for top right triangle
                indices[i++] = topRight;
                indices[i++] = botRight;
            }
        }
        ArrayList<AbstractDrawablePart> parts = new ArrayList<>();
        DrawablePartPosColor part = new DrawablePartPosColor(vertices, indices, shaderProgram);
        parts.add(part);
        setupParts(parts);
    }

    //TODO: Make this accurate
    public float getHeight(float x, float z){
        if(x < 0)
            x = 0;
        else if(x > width - 2)
            x = width - 2;
        if(z < 0)
            z = 0;
        else if(z > height - 2)
            z = height - 2;


        //Get heights from closest vertices
        float leftFront =  vertices[(((int)z + 0) * width) + ((int)x + 0)].position.getY();
        float rightFront = vertices[(((int)z + 0) * width) + ((int)x + 1)].position.getY();

        float leftBack =   vertices[(((int)z + 1) * width) + ((int)x + 0)].position.getY();
        float rightBack =  vertices[(((int)z + 1) * width) + ((int)x + 1)].position.getY();

        float xRest = x % 1;
        float zRest = z % 1;

        //Interpolate heights depending on how close
        float height =
                leftBack * (1.0f - xRest)  * (1.0f - zRest) +
                rightBack * (xRest)        * (1.0f - zRest) +

                leftFront * (1.0f - xRest) * (zRest)        +
                rightFront * (xRest)       * (zRest);

        return height;
    }
}
