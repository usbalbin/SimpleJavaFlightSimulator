package se.liu.ida.albhe417.tddd78.math;

public abstract class AbstractVector
{
    protected final float[] values;

    public AbstractVector(float[] values){
	this.values = values;
    }

    public float[] getFloatsRef(){
    	return values;
    }
}
