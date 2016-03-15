package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Albin on 09/03/2016.
 */
public class Terrain_old extends Terrain{

    public Terrain_old(Vector3 position, final int shaderProgram){
        super(position, 10, shaderProgram);
        setup(shaderProgram);
    }

    protected void setup(final int shaderProgram){
        heighMap = Helpers.imageToColors("content/heightmap.png");
        width = heighMap[0].length;
        height = heighMap.length;

        //Setup vertices
        VertexPositionColor[] vertices_1d = new VertexPositionColor[height * width];
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                float y = heighMap[z][x].getX() * 10;
                Vector3 color = heighMap[z][x];

                Vector3 position = new Vector3(x, y, z);
                vertices_1d[z * width + x] = new VertexPositionColor(position, color);
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
        DrawablePartPosColor part = new DrawablePartPosColor(vertices_1d, indices, shaderProgram);
        parts.add(part);
        setupParts(parts);
    }

    private Vector3 selectColor(float height){
        final Vector3 white = new Vector3(1, 0, 0);
        final Vector3 brown = new Vector3(0.4f, 0.2f, 0.05f);
        final Vector3 green = new Vector3(0, 1, 0);
        final Vector3 blue = new Vector3(0, 0, 1);

        if(height > 200)
            return white;
        if(height > 150)
            return brown;
        if(height > 100)
            return green;
        return blue;


    }
}
