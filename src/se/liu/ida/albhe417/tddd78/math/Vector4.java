package se.liu.ida.albhe417.tddd78.math;

public class Vector4
{
    private float[] values;

    public Vector4() {
	    this.values  = new float[4];
	    setX(0);setY(0);setZ(0);setW(0);
    }

    public Vector4(final float x, final float y, final float z, float w) {
	    this.values = new float[4];
	    setX(x);setY(y);setZ(z);setW(w);
    }

    public Vector4(float[] values) {
    	if(values.length != 4)
	        throw new IllegalArgumentException("Wrong sized array");
        this.values = values;
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

    public void setW(float w){
	values[3] = w;
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

    public float getW(){
    	return values[3];
    }

    public float[] getNewFloats(){
        float x = getX();
        float y = getY();
        float z = getZ();
        float w = getW();

        return new Vector4(x, y, z, w).values;
    }

}
