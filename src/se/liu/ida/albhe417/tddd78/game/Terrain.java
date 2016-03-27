package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Created by Albin on 15/03/2016.
 */
abstract public class Terrain extends AbstractGameObject {
    protected int width;
    protected int height;
    protected Vector3[][] heighMap;
    protected final float HEIGHT_FACTOR;

    protected final int shaderProgram;

    protected Terrain(Vector3 position, final int height_factor, final int shaderProgram){
        super(position, 0);
        HEIGHT_FACTOR = height_factor;
        this.shaderProgram = shaderProgram;
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
        float leftFront =  heighMap[(int)z + 0][(int)x + 0].getY();
        float rightFront = heighMap[(int)z + 0][(int)x + 1].getY();

        float leftBack =   heighMap[(int)z + 1][(int)x + 0].getY();
        float rightBack =  heighMap[(int)z + 1][(int)x + 1].getY();

        float xRest = x % 1;
        float zRest = z % 1;

        //Interpolate heights depending on how close
        float height =
                leftBack * (1.0f - xRest)  * (1.0f - zRest) +
                        rightBack * (xRest)        * (1.0f - zRest) +

                        leftFront * (1.0f - xRest) * (zRest)        +
                        rightFront * (xRest)       * (zRest);

        return height * HEIGHT_FACTOR;
    }

}
