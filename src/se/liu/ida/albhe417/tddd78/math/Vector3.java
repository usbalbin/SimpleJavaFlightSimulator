package se.liu.ida.albhe417.tddd78.math;

public class Vector3 extends AbstractVector
{
    public Vector3() {
    	super(new float[3]);
    	setX(0);setY(0);setZ(0);
    }

    public Vector3(final float x, final float y, final float z) {
	super(new float[3]);
	setX(x);setY(y);setZ(z);
    }

    public Vector3(float[] values) {
	super(values);
	if(values.length != 3)
	    throw new IllegalArgumentException("Wrong sized array");
    }

    public void setX(float x){
    	values[0] = x;
    }

    public void setY(float y){
	values[1] = y;
    }

    public void setZ(float z){
	values[2] = z;
    }

    public float getX(){
	return values[0];
    }

    public float getY(){
    	return values[1];
    }

    public float getZ(){
    	return values[2];
    }


    public float[] getNewFloats() {
        float x = getX();
        float y = getY();
        float z = getZ();

        return new Vector3(x, y, z).getFloatsRef();
    }

}
