package se.liu.ida.albhe417.tddd78.math;

import javax.vecmath.Vector4f;

public class Vector4
{
    public static final Vector4 UP = new Vector4(0, 1, 0, 1);
    public static final Vector4 FORWARD = new Vector4(0, 0, -1, 1);
    public static final Vector4 BACK = new Vector4(0, 0, 1, 1);
    public static final Vector4 RIGHT = new Vector4(1, 0, 0, 1);


    public static final Vector4 DIR_UP = new Vector4(0, 1, 0, 0);
    public static final Vector4 DIR_FORWARD = new Vector4(0, 0, -1, 0);
    public static final Vector4 DIR_BACK = new Vector4(0, 0, 1, 0);
    public static final Vector4 DIR_RIGHT = new Vector4(1, 0, 0, 0);

    public final float[] values;

    public Vector4() {
	    this.values  = new float[4];
	    setX(0);setY(0);setZ(0);setW(0);
    }

    public Vector4(final float x, final float y, final float z, final float w) {
	    this.values = new float[4];
	    setX(x);setY(y);setZ(z);setW(w);
    }

    public Vector4(Vector3 vector3, float w) {
        this.values = new float[4];
        setX(vector3.getX());setY(vector3.getY());setZ(vector3.getZ());setW(w);
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

    public Vector3 toVector3(){
        return new Vector3(getX(), getY(), getZ());
    }

    public Vector4f toVector4f(){
        return new Vector4f(this.values);
    }

    @Override
    public String toString() {
        return getX() + ";" + getY() + ";" + getZ() + ";" + getW();
    }
}
