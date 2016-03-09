package se.liu.ida.albhe417.tddd78.math;

public class AbstractMatrix
{
    protected final float[][] values;

    public AbstractMatrix(float[][] values){
	this.values = values;
    }

    public void setValueAt(int column, int row, float value){
	values[row][column] = value;
    }

    public float getValueAt(int column, int row){
	return values[row][column];
    }

    public float[][] getFloatsRef(){
	    return values;
    }
}
