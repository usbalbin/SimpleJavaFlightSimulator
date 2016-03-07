package se.liu.ida.albhe417.tddd78.math;

public class Matrix4x4 extends AbstractMatrix
{
    public Matrix4x4(float[][] matrixArray){
	super(matrixArray);
	if(matrixArray.length != 4)
	    throw new IllegalArgumentException("Wrong sized array");
	for(float[] row : matrixArray){
	    if(row.length != 4)
		throw new IllegalArgumentException("Wrong sized array");
	}
    }

    public Matrix4x4(){
    	super(new float[4][4]);
    	values[0] = new float[]{1, 0, 0, 0};
    	values[1] = new float[]{0, 1, 0, 0};
    	values[2] = new float[]{0, 0, 1, 0};
    	values[3] = new float[]{0, 0, 0, 1};
    }

    public Matrix4x4(Vector4 zero, Vector4 one, Vector4 two, Vector4 three){
	super(new float[4][4]);
	values[0] = zero.getNewFloats();
	values[1] = one.getNewFloats();
	values[2] = two.getNewFloats();
	values[3] = three.getNewFloats();
    }

    public static Matrix4x4 createPosFromVector(Vector3 position){
	Matrix4x4 res = new Matrix4x4();
	res.setValueAt(0, 0, position.getX());
	res.setValueAt(0, 1, position.getY());
	res.setValueAt(0, 2, position.getZ());
	return res;
    }

    public static Matrix4x4 createScaleFromVector(Vector3 position){
    	Matrix4x4 res = new Matrix4x4();
    	res.setValueAt(0, 0, position.getX());
    	res.setValueAt(1, 1, position.getY());
    	res.setValueAt(2, 2, position.getZ());
    	return res;
    }

}
