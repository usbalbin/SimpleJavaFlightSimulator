package se.liu.ida.albhe417.tddd78.math;

public class Vector3
{
    public static final Vector3 RIGHT = new Vector3(1, 0, 0);
    public static final Vector3 UP = new Vector3(0, 1, 0);
    public static final Vector3 BACK = new Vector3(0, 0, 1);
    public static final Vector3 FORWARD = new Vector3(0, 0, -1);
    public static final Vector3 ONE = new Vector3(1, 1, 1);

    private float[] values;
    public Vector3() {
    	this.values = new float[3];
    	setX(0);setY(0);setZ(0);
    }

    public Vector3(final float x, final float y, final float z) {
	    this.values = new float[3];
	    setX(x);setY(y);setZ(z);
    }

    public Vector3(float[] values) {
	    if(values.length != 3)
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

    public float getX(){
	return values[0];
    }

    public float getY(){
    	return values[1];
    }

    public float getZ(){
    	return values[2];
    }

    public Vector3 multiply(Vector3 other){
        return new Vector3(this.getX() * other.getX(), this.getY() * other.getY(), this.getZ() * other.getZ());
    }

    public Vector3 multiply(float other){
        return new Vector3(this.getX() * other, this.getY() * other, this.getZ() * other);
    }

    public Vector3 cross(Vector3 other){
        float x = this.getY() * other.getZ() - this.getZ() * other.getY();
        float y = this.getZ() * other.getX() - this.getX() * other.getZ();
        float z = this.getX() * other.getY() - this.getY() * other.getX();

        return new Vector3(x, y, z);
    }

    public float dot(Vector3 other){
        Vector3 product = this.multiply(other);
        return product.getX() + product.getY() + product.getZ();
    }

    public Vector3 divide(float other){
        return new Vector3(this.getX() / other, this.getY() / other, this.getZ() / other);
    }

    public Vector3 add(Vector3 other){
        return new Vector3(this.getX() + other.getX(), this.getY() + other.getY(), this.getZ() + other.getZ());
    }

    public Vector3 sub(Vector3 other){
        return new Vector3(this.getX() - other.getX(), this.getY() - other.getY(), this.getZ() - other.getZ());
    }

    public float length2(){
        return this.dot(this);
    }

    public float length(){
        return (float)Math.sqrt(this.length2());
    }

    public Vector3 getNormalized(){
        return this.divide(this.length());
    }

    public Vector3 getRotatedAroundX(float angle){
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);

        float x = this.getX();
        float y = this.getY() * cos + this.getZ() * sin;
        float z = this.getY() * sin + this.getZ() * cos;

        return new Vector3(x, y, z);
    }

    public Vector3 getRotatedAroundY(float angle){
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);

        float x = this.getX() * cos + this.getZ() * sin;
        float y = this.getY();
        float z = -this.getX() * sin + this.getZ() * cos;

        return new Vector3(x, y, z);
    }

    public Vector3 getRotatedAroundZ(float angle){
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);

        float x = this.getX() * cos + this.getY() * sin;
        float y = this.getX() * sin + this.getY() * cos;
        float z = this.getZ();

        return new Vector3(x, y, z);
    }

    public Vector3 getRotated(float yaw, float pitch, float roll){
        return this.getRotatedAroundY(yaw).getRotatedAroundX(pitch).getRotatedAroundZ(roll);
    }


}
