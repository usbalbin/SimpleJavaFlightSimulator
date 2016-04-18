package se.liu.ida.albhe417.tddd78.math;

/**
 * Project TDDD78
 *
 * File created by Albin.
 */
public class AbstractMatrix
{
    final float[][] values;

    AbstractMatrix(float[][] values){
	this.values = values;
    }

    public float getValueAt(int column, int row){
	return values[row][column];
    }

}
