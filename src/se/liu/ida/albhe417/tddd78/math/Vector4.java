package se.liu.ida.albhe417.tddd78.math;

/**
 * Vector4 is a useful 4-component float container useful for representing positions, colors, directions and much more.
 */
public class Vector4
{
    /**
     * Underlying values of the vector where values[0] corresponds to X, values[1] to Y and so on.
     */
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

    public static Vector4 createColor(int r, int g, int b, int a){
        final float hexToFloat = 256.0f;
        return new Vector4(r / hexToFloat, g / hexToFloat, b / hexToFloat, a / hexToFloat);
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

    public Vector4 divide(float other){
        return new Vector4(this.getX() / other, this.getY() / other, this.getZ() / other, this.getW() / other);
    }

    public Vector4 add(Vector4 other){
        return new Vector4(this.getX() + other.getX(), this.getY() + other.getY(), this.getZ() + other.getZ(), this.getW() + other.getW());
    }

    public Vector4 add(float otherX, float otherY, float otherZ, float otherW){
        return this.add(new Vector4(otherX, otherY, otherZ, otherW));
    }

    public Vector3 toVector3(){
        return new Vector3(getX(), getY(), getZ());
    }

    @Override
    public String toString() {
        return getX() + ";" + getY() + ";" + getZ() + ";" + getW();
    }
}
