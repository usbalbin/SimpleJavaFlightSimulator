package se.liu.ida.albhe417.tddd78.math;

import se.liu.ida.albhe417.tddd78.game.Helpers;

import javax.vecmath.Vector3f;

/**
 * Vector3 is a useful 3-component float container useful for representing positions, colors, directions, forces and much more.
 */
public class Vector3
{
    /**
     * Unit vector pointing up
     */
    public static final Vector3 UPWARD = new Vector3(0, 1, 0);

    /**
     * Unit vector pointing forward
     */
    public static final Vector3 FORWARD = new Vector3(0, 0, -1);

    /**
     * Vector with all axis set to zero
     */
    public static final Vector3 ZERO = new Vector3(0, 0, 0);

	/**
     * Underlying values of the vector where values[0] corresponds to X, values[1] to Y and so on.
     */
    public final float[] values;

    public Vector3(Vector3f vector3f){
        this(vector3f.x, vector3f.y, vector3f.z);
    }

    public Vector3(Vector3 vector3){
        this(vector3.getX(), vector3.getY(), vector3.getZ());
    }

    public Vector3(float value){
        this(value, value, value);
    }

    public Vector3(final float x, final float y, final float z) {
        this.values = new float[3];
        setX(x);setY(y);setZ(z);
    }

    public static Vector3 createColor(int r, int g, int b){
        final float hexToFloat = 256.0f;
        return new Vector3(r / hexToFloat, g / hexToFloat, b / hexToFloat);
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

    public Vector3 add(float otherX, float otherY, float otherZ){
        return this.add(new Vector3(otherX, otherY, otherZ));
    }

    public void increase(Vector3 other){
        for (int i = 0; i < values.length; i++)
            this.values[i] += other.values[i];
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

    public Vector3 abs(){
        float x = Math.abs(getX());
        float y = Math.abs(getY());
        float z = Math.abs(getZ());

        return new Vector3(x, y, z);
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

    //Would not make sense to have rotate for x and y but not z for future development
    public Vector3 getRotatedAroundZ(float angle){
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);

        float x = this.getX() * cos + this.getY() * sin;
        float y = this.getX() * sin + this.getY() * cos;
        float z = this.getZ();

        return new Vector3(x, y, z);
    }

    @Override
    public String toString() {
        return getX() + ";" + getY() + ";" + getZ();
    }

    public Vector3f toVector3f(){
        return new Vector3f(this.values);
    }

    @Override
    public boolean equals(Object obj) {
        if(this.getClass() != obj.getClass())
            return false;
        Vector3 other = (Vector3)obj;
        return Helpers.fEquals(this.getX(), other.getX()) &&
               Helpers.fEquals(this.getZ(), other.getZ());
    }

    @Override
    public int hashCode() {
        int x = Math.round(getX());
        int z = Math.round(getZ());

        int result = x;
        result = 31 * result + z;
        return result;
    }
}
